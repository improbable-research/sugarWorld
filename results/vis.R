gdp_data <- 
  read_csv('~/Projects/sugarWorld/results/gdp.csv')
gdp_data %>%
  head(250) %>%
  mutate(t = row_number()) %>%
  gather(country, gdp, -t) %>%
  ggplot(aes(x = t, y = gdp, color = country)) %>%
  + geom_line()

gov_balance_data <- 
  read_csv('~/Projects/sugarWorld/results/government_balance.csv')
gov_balance_data %>%
  head(250) %>%
  mutate(t = row_number()) %>%
  gather(country, gov_balance, -t) %>%
  ggplot(aes(x = t, y = gov_balance, color = country)) %>%
  + geom_line()

ind_balance_data <- 
  read_csv('~/Projects/sugarWorld/results/industry_balance.csv')
ind_balance_data %>%
  head(250) %>%
  mutate(t = row_number()) %>%
  gather(country, ind_balance, -t) %>%
  ggplot(aes(x = t, y = ind_balance, color = country)) %>%
  + geom_line()

wf_balance_data <- 
  read_csv('~/Projects/sugarWorld/results/workforce_balance.csv')
wf_balance_data %>%
  head(250) %>%
  mutate(t = row_number()) %>%
  gather(country, wf_balance, -t) %>%
  ggplot(aes(x = t, y = wf_balance, color = country)) %>%
  + geom_line()

ind_stock_data <- 
  read_csv('~/Projects/sugarWorld/results/industry_stock.csv')
ind_stock_data %>%
  head(250) %>%
  mutate(t = row_number()) %>%
  gather(country, ind_stock, -t) %>%
  ggplot(aes(x = t, y = ind_stock, color = country)) %>%
  + geom_line()
