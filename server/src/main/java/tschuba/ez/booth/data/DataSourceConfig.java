package tschuba.ez.booth.data;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Configuration class for setting up the DataSource bean.
 * This class reads database connection properties from the environment
 * and configures a DriverManagerDataSource accordingly.
 */
@Component
public class DataSourceConfig {

    private static final String DATASOURCE_PREFIX = "tschuba.ez.booth.datasource.";
    private static final String DRIVER_CLASS_NAME_PROPERTY = DATASOURCE_PREFIX + "driverClassName";
    private static final String URL_PROPERTY = DATASOURCE_PREFIX + "url";
    private static final String USERNAME_PROPERTY = DATASOURCE_PREFIX + "username";
    private static final String PASSWORD_PROPERTY = DATASOURCE_PREFIX + "password";

    private final Environment env;

    @Autowired
    public DataSourceConfig(@NonNull Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(requireNonNull(env.getProperty(DRIVER_CLASS_NAME_PROPERTY)));
        dataSource.setUrl(requireNonNull(env.getProperty(URL_PROPERTY)));
        dataSource.setUsername(requireNonNull(env.getProperty(USERNAME_PROPERTY)));
        dataSource.setPassword(Objects.requireNonNull(env.getProperty(PASSWORD_PROPERTY)));
        return dataSource;
    }
}
