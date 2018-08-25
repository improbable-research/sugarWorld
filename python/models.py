import numpy as np
import keras
import sklearn.linear_model
import sklearn.preprocessing
import sklearn.ensemble
import sklearn.multioutput
import xgboost
from time_series import TimeSeriesModel


# Benchmark
class NaiveMean(TimeSeriesModel):
    def _fit(self, x_train, y_train):
        self.model = y_train.mean(axis=0)

    def _predict(self, x):
        return np.tile(self.model, (len(x), 1))


class LinearRegression(TimeSeriesModel):
    model = sklearn.linear_model.LinearRegression


class RandomForest(TimeSeriesModel):
    params = dict(
        n_estimators=5000,
        max_features='sqrt',
    )
    model = sklearn.ensemble.RandomForestRegressor


class XGBoost(TimeSeriesModel):
    params = dict(
        n_estimators=5000,
        max_depth=5,
        subsample=0.5,
    )

    def _fit(self, x_train, y_train):
        self.model = sklearn.multioutput.MultiOutputRegressor(xgboost.XGBRegressor(**self.params))
        self.model.fit(x_train, y_train)

    def _predict(self, x):
        return self.model.predict(x)


class LSTM(TimeSeriesModel):
    params = dict(
        units=30,
        dropout=0.2,
        epochs=300,
        batch_size=12,
        verbose=0,
    )

    def _fit(self, x_train, y_train):
        n_features = len(self.time_series.cols())
        params = self.params.copy()

        self.feature_scaler = sklearn.preprocessing.MinMaxScaler(feature_range=(0, 1))
        self.target_scaler = sklearn.preprocessing.MinMaxScaler(feature_range=(0, 1))
        x_train_scaled = self.feature_scaler.fit_transform(x_train).reshape((-1, self.n_lookback, n_features))
        y_train_scaled = self.target_scaler.fit_transform(y_train)

        self.model = keras.Sequential()
        self.model.add(keras.layers.LSTM(params.pop('units'), input_shape=(self.n_lookback, n_features)))
        self.model.add(keras.layers.BatchNormalization())
        self.model.add(keras.layers.Dropout(params.pop('dropout')))
        self.model.add(keras.layers.Dense(y_train.shape[1]))
        self.model.compile(loss='mean_squared_error', optimizer='adam')
        self.model.fit(x_train_scaled, y_train_scaled, **params)

    def _predict(self, x):
        n_features = len(self.time_series.cols())
        x_scaled = self.feature_scaler.transform(x).reshape((-1, self.n_lookback, n_features))
        return self.target_scaler.inverse_transform(self.model.predict(x_scaled))
