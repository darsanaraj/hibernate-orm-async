package org.hibernate.async.jdbc;

import com.jakobk.async.db.DbConnectionPool;
import org.hibernate.async.spi.DbConnectionPoolProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Asynchronous DB connection pool provider implementation that uses JDBC for database access.
 *
 * This provider uses Hibernate's internal {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} to
 * access Hibernate's underlying JDBC connections, and implements asynchronous DB access using these JDBC connections
 * and a separate thread pool.
 *
 * It therefore does not actually implement real asynchronous, non-blocking database access, but it rather defers the db
 * accessing code to a separate thread pool, and thus makes it seem as if it were really asynchronous and non-blocking.
 *
 * This provider should only be used for unit testing purposes against databases where no real asynchronous driver
 * exists (i.e. all known in-memory databases).
 */
public class JdbcDbConnectionPoolProvider implements DbConnectionPoolProvider {

    @Override
    public DbConnectionPool createDbConnectionPool(SessionFactoryImplementor sessionFactory, Map<String, Object> properties) {
        return new JdbcDbConnectionPool(
                sessionFactory.getConnectionProvider(),
                Executors.newCachedThreadPool()
        );
    }

    @Override
    public String getName() {
        return "jdbc-async-simulator";
    }

}
