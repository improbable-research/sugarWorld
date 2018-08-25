import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error
from utils import prepend_columns


class TimeSeries(object):
    noise_level = 0.0
    train_perc = 0.8

    def __init__(self, data_true, target_cols=None, **kwargs):
        self.data_true = data_true.copy()
        self.target_cols = target_cols or data_true.columns
        self.__dict__.update(**kwargs)
        self.data_observed = self.observe(data_true)

    def observe(self, data):
        observed = data.copy()
        values = observed.values
        values *= np.random.normal(1.0, self.noise_level, values.shape)
        return observed

    def idx(self, slice=None):
        n = len(self.data_true)
        n_train = int(self.train_perc * n)
        if slice is None:
            return self.data_true.index
        elif slice == 'train':
            return self.data_true.index[range(n_train)]
        elif slice == 'test':
            return self.data_true.index[range(n_train, n)]

    def cols(self, target=False):
        return self.target_cols if target else self.data_true.columns

    def view(self, lens='true', slice=None, target=False):
        if lens == 'true':
            return self.data_true.loc[self.idx(slice), self.cols(target)]
        elif lens == 'observed':
            return self.data_observed.loc[self.idx(slice), self.cols(target)]

    @staticmethod
    def _offset(direction, n):
        if direction == 'forward':
            return 1
        elif direction == 'backward':
            return -n + 1
        else:
            raise ValueError('direction {} not recognized'.format(direction))

    @classmethod
    def _roll(cls, direction, data, n, stride):
        n_cols = len(data.columns) * n
        n_rows = len(data) - n + 1
        x = np.empty((n_rows, n_cols))
        for i in range(n_rows):
            x[i, :] = data.iloc[i:(i + n), :].values.reshape(n_cols)

        idx = data.index[:(len(data) - n + 1)] - cls._offset(direction, n)
        x = pd.DataFrame(x, index=idx)

        return x.iloc[np.arange(0, len(x), stride, int)]

    @classmethod
    def _unroll(cls, direction, data, n):
        n_cols = len(data.columns) / n
        n_row = len(data) * n
        x = data.values.reshape((n_row, n_cols))
        idx = pd.Index([])
        for i in data.index:
            idx = idx.append(pd.Index(np.arange(i, i + n) + cls._offset(direction, n)))
        return pd.DataFrame(x, index=idx)

    @classmethod
    def roll_forward(cls, data, n, stride=1):
        return cls._roll('forward', data, n, stride)

    @classmethod
    def roll_backward(cls, data, n, stride=1):
        return cls._roll('backward', data, n, stride)

    @classmethod
    def unroll_forward(cls, data, n):
        return cls._unroll('forward', data, n)

    @classmethod
    def unroll_backward(cls, data, n):
        return cls._unroll('backward', data, n)


class TimeSeriesModel(object):
    model = None
    params = dict()

    def __init__(self, time_series, n_lookback=1, n_lookahead=1, **kwargs):
        self.time_series = time_series
        self.n_lookback = n_lookback
        self.n_lookahead = n_lookahead
        self.is_fit = False
        self.params.update(kwargs)

    def check_model(f):
        def wrapped(self, *args, **kwargs):
            if any((
                self.model is None,
                not hasattr(self.model, 'fit'),
                not hasattr(self.model, 'predict')
            )):
                return NotImplementedError
            return f(self, *args, **kwargs)
        return wrapped

    def fit(self):
        X_train = TimeSeries.roll_backward(
            self.time_series.view('observed', 'train'),
            self.n_lookback,
        )
        Y_train = TimeSeries.roll_forward(
            self.time_series.view('observed', 'train', target=True),
            self.n_lookahead,
        )
        idx = X_train.index.intersection(Y_train.index)
        x_train, y_train = X_train.loc[idx].values, Y_train.loc[idx].values
        self._fit(x_train, y_train)
        self.is_fit = True

    @check_model
    def _fit(self, x_train, y_train):
        self.model = self.model(**self.params)
        self.model.fit(x_train, y_train)

    def predict(self, X):
        if not self.is_fit:
            raise ValueError('Model not yet fit')

        X_rolled = TimeSeries.roll_backward(X, self.n_lookback, stride=self.n_lookahead)
        Y_hat_rolled = pd.DataFrame(
            self._predict(X_rolled.values),
            index=X_rolled.index,
        )
        preds = TimeSeries.unroll_forward(Y_hat_rolled, self.n_lookahead)
        preds.columns = self.time_series.cols(target=True)
        return preds

    @check_model
    def _predict(self, x):
        return self.model.predict(x)

    def validate(self):
        Y_hat = self.predict(self.time_series.view('observed'))
        Y = self.time_series.view('true', target=True)
        return TimeSeriesModelValidation(Y_hat, Y, self.time_series.idx('train'), self.time_series.idx('test'))


class TimeSeriesModelValidation(object):
    def __init__(self, predicted, actual, train_idx, test_idx):
        self.predicted = prepend_columns(predicted, 'predicted')
        self.actual = prepend_columns(actual, 'actual')
        self.predicted_cols = predicted.columns
        self.actual_cols = actual.columns
        self.actual.loc[train_idx, 'slice'] = 'train'
        self.actual.loc[test_idx, 'slice'] = 'test'
        self.data = pd.merge(predicted, actual, left_index=True, right_index=True)

    def rmse(self):
        return self.data.groupby('slice').apply(
            lambda g: np.sqrt(mean_squared_error(g[self.actual_cols], g[self.predicted_cols]))
        )

    def plot(self, half_width=150):
        fig, axes = plt.subplots(nrows=4, ncols=1)
        test_start = self.data.loc[self.data['slice'] == 'test'].index[0]
        for i in range(len(self.predicted_cols)):
            ax = self.data.loc[
                 (test_start - half_width):(test_start + half_width)
                ].plot(
                y=[self.actual_cols[i], self.predicted_cols[i]],
                use_index=True,
                figsize=(30, 10),
                ax=axes[i-1]
            )
            ax.axvline(test_start, color='k', linestyle='--')
