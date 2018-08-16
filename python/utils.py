import pandas as pd


def prepend_columns(data, prefix):
    if prefix is None:
        return data
    data.columns = ['{}_{}'.format(prefix, c.strip()) for c in data.columns]
    return data


def load_data(path, prefix=None):
    data = pd.read_csv(path)
    data = prepend_columns(data, prefix)
    return data


def run_gdp_model(model, X_data, Y_data, **kwargs):
    m = model(X_data, Y_data, **kwargs)
    m.fit_transform()
    rmse = m.rmse()
    print('Train error: {}'.format(rmse['train']))
    print('Test error: {}'.format(rmse['test']))
    m.plot()
    return m
