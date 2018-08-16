import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.metrics import mean_squared_error


class GDPModel(object):
    countries = range(1, 5)
    colnames_actual = ['country_{}_actual'.format(c) for c in countries]
    colnames_predicted = ['country_{}_predicted'.format(c) for c in countries]
    train_perc = 0.8

    def __init__(self, X_data, Y_data):
        self.X_data, self.Y_data = X_data.copy(), Y_data.copy()
        self.Y_data.columns = self.colnames_actual
        self.validation = None

        n_train = int(self.train_perc * len(X_data))
        n_test = len(X_data) - n_train
        split = ['train'] * n_train + ['test'] * n_test
        self.X_data['split'] = self.Y_data['split'] = split

    @staticmethod
    def _data_split(data, split):
        return data[data['split'] == split].drop('split', axis=1)

    def X_train(self):
        return self._data_split(self.X_data, 'train')

    def X_test(self):
        return self._data_split(self.X_data, 'test')

    def Y_train(self):
        return self._data_split(self.Y_data, 'train')

    def Y_test(self):
        return self._data_split(self.Y_data, 'test')

    def _fit_transform(self):
        raise NotImplementedError

    def fit_transform(self):
        preds, idx = self._fit_transform()
        Y_predictions = pd.DataFrame(preds, index=idx)
        Y_predictions.columns = self.colnames_predicted
        self.validation = self.Y_data.join(Y_predictions, how='inner')

    def rmse(self):
        validation = self.validation.dropna()
        return validation.groupby('split').apply(
            lambda g: np.sqrt(mean_squared_error(g[self.colnames_actual], g[self.colnames_predicted]))
        )

    def plot(self, half_width=150):
        fig, axes = plt.subplots(nrows=4, ncols=1)
        test_start = next(i for i in self.Y_data.index if self.Y_data.loc[i]['split'] == 'test')
        for i in self.countries:
            ax = self.validation.loc[
                 (test_start - half_width):(test_start + half_width)
                 ].plot(
                y=['country_{}_actual'.format(i), 'country_{}_predicted'.format(i)],
                use_index=True,
                figsize=(30, 10),
                ax=axes[i-1]
            )
            ax.axvline(test_start, color='k', linestyle='--')

