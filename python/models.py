import numpy as np
import pandas as pd
import keras
from sklearn.linear_model import LinearRegression
from sklearn.ensemble import RandomForestRegressor
from xgboost import XGBRegressor
from sklearn.multioutput import MultiOutputRegressor
from sklearn.preprocessing import MinMaxScaler
from gdp import GDPModel


# Benchmark
class gdpNaiveMean(GDPModel):
    def _fit_transform(self):
        Y_means = self.Y_train().mean()
        return np.tile(Y_means, (len(self.Y_data), 1))


# Benchmark
class gdpNaiveConstant(GDPModel):
    def _fit_transform(self):
        return self.X_data.iloc[:, :4]


class gdpLinearRegression(GDPModel):
    def _fit_transform(self):
        x_train = self._lagged_features_frame(self.X_train())
        y_train = self.Y_train().iloc[(self.window - 1):].values
        x_test = self._lagged_features_frame(self.X_test())

        lr = LinearRegression(**self.params)
        lr.fit(x_train, y_train)

        train_preds = lr.predict(x_train)
        test_preds = lr.predict(x_test)

        return np.concatenate([train_preds, test_preds])


class gdpRandomForest(GDPModel):
    params = dict(
        n_estimators=5000,
        max_features='sqrt',
    )

    def _fit_transform(self):
        x_train = self._lagged_features_frame(self.X_train())
        y_train = self.Y_train().iloc[(self.window - 1):].values
        x_test = self._lagged_features_frame(self.X_test())

        rf = RandomForestRegressor(**self.params)
        rf.fit(x_train, y_train)

        train_preds = rf.predict(x_train)
        test_preds = rf.predict(x_test)

        return np.concatenate([train_preds, test_preds])


class gdpXGBoost(GDPModel):
    params = dict(
        n_estimators=5000,
        max_depth=5,
        subsample=0.5,
    )

    def _fit_transform(self):
        x_train = self._lagged_features_frame(self.X_train())
        y_train = self.Y_train().iloc[(self.window - 1):].values
        x_test = self._lagged_features_frame(self.X_test())

        xgb = MultiOutputRegressor(XGBRegressor(**self.params))
        xgb.fit(x_train, y_train)

        train_preds = xgb.predict(x_train)
        test_preds = xgb.predict(x_test)

        return np.concatenate([train_preds, test_preds])


class gdpLSTM(GDPModel):
    params = dict(
        epochs=300,
        batch_size=12,
        verbose=0,
    )

    def _fit_transform(self):
        n_features = len(self.X_data.columns) - 1

        feature_scaler = MinMaxScaler(feature_range=(0, 1))
        target_scaler = MinMaxScaler(feature_range=(0, 1))
        X_train_scaled = pd.DataFrame(feature_scaler.fit_transform(self.X_train().values))
        Y_train_scaled = pd.DataFrame(target_scaler.fit_transform(self.Y_train().values))
        X_test_scaled = pd.DataFrame(feature_scaler.transform(self.X_test().values))

        x_train = self._lagged_features_frame(X_train_scaled).reshape((-1, self.window, n_features))
        y_train = Y_train_scaled.iloc[(self.window - 1):].values
        x_test = self._lagged_features_frame(X_test_scaled).reshape((-1, self.window, n_features))


        model = keras.Sequential()
        model.add(keras.layers.LSTM(20, input_shape=(self.window, n_features)))
        model.add(keras.layers.BatchNormalization())
        model.add(keras.layers.Dropout(0.2))
        model.add(keras.layers.Dense(4))
        model.compile(loss='mean_squared_error', optimizer='adam')
        model.fit(x_train, y_train, **self.params)

        train_preds = target_scaler.inverse_transform(model.predict(x_train))
        test_preds = target_scaler.inverse_transform(model.predict(x_test))

        return np.concatenate([train_preds, test_preds])
