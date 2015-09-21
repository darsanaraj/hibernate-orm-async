package org.hibernate.async.postgresql;

import com.github.mauricio.async.db.Configuration;
import com.jakobk.async.db.ConfigurationBuilder;
import com.jakobk.async.db.DbConnectionPool;
import com.jakobk.async.db.postgresql.PostgresqlConnectionPool;
import org.hibernate.async.spi.DbConnectionPoolProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import java.util.Map;

/**
 *
 */
public class PostgresqlDbConnectionPoolProvider implements DbConnectionPoolProvider {

    private static final String PROPERTY_PREFIX = "hibernate.async.";
    private static final String USERNAME_PROPERTY_KEY = PROPERTY_PREFIX + "username";
    private static final String PASSWORD_PROPERTY_KEY = PROPERTY_PREFIX + "password";
    private static final String DATABASE_PROPERTY_KEY = PROPERTY_PREFIX + "database";

    @Override
    public DbConnectionPool createDbConnectionPool(SessionFactoryImplementor sessionFactory, Map<String, Object> properties) {
        Configuration configuration = new ConfigurationBuilder()
                .withUsername(getRequiredProperty(USERNAME_PROPERTY_KEY, properties))
                .withPassword(getRequiredProperty(PASSWORD_PROPERTY_KEY, properties))
                .withDatabase(getRequiredProperty(DATABASE_PROPERTY_KEY, properties))
                .build();
        return new PostgresqlConnectionPool(configuration);  // TODO pool configuration
    }

    @Override
    public String getName() {
        return "postgresql-hibernate";
    }

    private String getRequiredProperty(String key, Map<String, Object> properties) {
        Object setting = properties.get(key);
        if (setting == null) {
            throw new IllegalArgumentException(key + " not set");
        }
        return setting.toString();
    }

}
