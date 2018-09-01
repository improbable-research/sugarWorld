import pandas as pd


def prepend_columns(data, prefix):
    if prefix is None:
        return data
    data.columns = ['{}_{}'.format(prefix, str(c).strip()) for c in data.columns]
    return data


def load_data(path, prefix=None):
    data = pd.read_csv(path)
    data = prepend_columns(data, prefix)
    return data


def time_series_model_runner(time_series):
    def run(model, **kwargs):
        m = model(time_series, **kwargs)
        m.fit()
        v = m.validate()
        rmse = v.rmse()
        print('Train error: {}'.format(rmse['train']))
        print('Test error: {}'.format(rmse['test']))
        v.plot()
        return m
    return run

