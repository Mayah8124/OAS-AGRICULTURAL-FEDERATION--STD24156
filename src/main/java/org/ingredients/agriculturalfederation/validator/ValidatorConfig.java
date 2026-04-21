package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {

    @Bean
    public MemberValidator memberValidator(DataSourceConfig dataSourceConfig) {
        return new MemberValidator(dataSourceConfig);
    }

    @Bean
    public CollectivityValidator collectivityValidator(DataSourceConfig dataSourceConfig) {
        return new CollectivityValidator(dataSourceConfig);
    }
}
