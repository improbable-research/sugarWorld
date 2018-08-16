import numpy as np
import keras
from sklearn.linear_model import LinearRegression
from sklearn.ensemble import RandomForestRegressor
from sklearn.preprocessing import MinMaxScaler
from gdp import GDPModel


# Benchmarks
class NaiveMean(GDPModel):
    def _fit_transform(self):
        Y_means = self.Y_train().mean()
        idx = self.Y_data.index
        return np.tile(Y_means, (len(idx), 1)), idx


class NaiveConstant(GDPModel):
    def _fit_transform(self):
        idx = self.Y_data.index
        return self.X_data.iloc[:, :4], idx


# Linear regression over the last n time steps
class Autoregressive(GDPModel):
    def __init__(self, X_data, Y_data, window):
        super(Autoregressive, self).__init__(X_data, Y_data)
        self.window = window

    def _fit_transform(self):
        X_train = self.X_train()
        Y_train = self.Y_train()
        X_test = self.X_test()

        # Build training set
        n_features = len(X_train.columns) * self.window
        n_samples = len(X_train) - self.window + 1
        x_train = np.empty((n_samples, n_features))
        for i in range(n_samples):
            x_train[i, :] = X_train.iloc[i: (i + self.window)].values.reshape(n_features)
        y_train = Y_train.iloc[(self.window - 1):].values

        # Build model
        lr = LinearRegression()
        lr.fit(x_train, y_train)

        # Predict training set
        train_preds = lr.predict(x_train)

        # Build test set
        n_samples = len(X_test) - self.window + 1
        x_test = np.empty((n_samples, n_features))
        for i in range(n_samples):
            x_test[i, :] = X_test.iloc[i: (i + self.window)].values.reshape(n_features)

        # Predict test set
        test_preds = lr.predict(x_test)

        # Return predictions and respective time index
        idx = X_train.index[(self.window - 1):].append(X_test.index[(self.window - 1):])
        return np.concatenate([train_preds, test_preds]), idx


# Ensemble model using only features from the current step
class MarkovEnsemble(GDPModel):
    def __init__(self, X_data, Y_data, **kwargs):
        super(MarkovEnsemble, self).__init__(X_data, Y_data)
        self.rf_params = dict(
            n_estimators=5000,
            max_features='sqrt',
        )
        self.rf_params.update(kwargs)

    def _fit_transform(self):
        rf = RandomForestRegressor(**self.rf_params)
        rf.fit(self.X_train(), self.Y_train())
        idx = self.Y_data.index
        predictions = rf.predict(self.X_data.drop('split', axis=1))
        return predictions, idx


# Recurrent NN using features from the last n steps
class LSTM(GDPModel):
    def __init__(self, X_data, Y_data, window, **kwargs):
        super(LSTM, self).__init__(X_data, Y_data)
        self.window = window
        self.fit_params = dict(
            epochs=300,
            batch_size=12,
            verbose=0,
        )
        self.fit_params.update(kwargs)

    def _fit_transform(self):
        X_train = self.X_train()
        Y_train = self.Y_train()
        X_test = self.X_test()

        # Scale variables
        feature_scaler = MinMaxScaler(feature_range=(0, 1))
        target_scaler = MinMaxScaler(feature_range=(0, 1))
        features_scaled = feature_scaler.fit_transform(X_train.values)
        target_scaled = target_scaler.fit_transform(Y_train.values)

        # Build training set
        n_samples = len(features_scaled) - self.window + 1
        n_features = features_scaled.shape[1]
        x_train = np.empty(shape=(n_samples, self.window, n_features))
        y_train = np.empty(shape=(n_samples, target_scaled.shape[1]))
        for i in range(n_samples):
            x_train[i, :, :] = features_scaled[i:i + self.window, :]
            y_train[i, :] = target_scaled[i + self.window - 1, :]

        # Build model
        model = keras.Sequential()
        model.add(keras.layers.LSTM(20, input_shape=(self.window, n_features)))
        model.add(keras.layers.BatchNormalization())
        model.add(keras.layers.Dense(4))
        model.compile(loss='mean_squared_error', optimizer='adam')
        model.fit(x_train, y_train, **self.fit_params)

        # Predict training set
        train_preds = target_scaler.inverse_transform(model.predict(x_train))

        # Build test set
        features_scaled = feature_scaler.transform(X_test.values)
        n_samples = len(features_scaled) - self.window + 1
        x_test = np.empty(shape=(n_samples, self.window, n_features))
        for i in range(n_samples):
            x_test[i, :, :] = features_scaled[i:i + self.window, :]

        # Predict test set
        test_preds = target_scaler.inverse_transform(model.predict(x_test))

        # Return predictions and respective time index
        idx = X_train.index[(self.window - 1):].append(X_test.index[(self.window - 1):])
        return np.concatenate([train_preds, test_preds]), idx