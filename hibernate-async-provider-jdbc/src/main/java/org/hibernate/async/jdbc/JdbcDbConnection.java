package org.hibernate.async.jdbc;

import com.jakobk.async.db.DbConnection;
import com.jakobk.async.db.QueryResult;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 *
 */
public class JdbcDbConnection implements DbConnection {

    private final ConnectionProvider connectionProvider;
    private final ExecutorService executorService;

    private Connection connection;

    public JdbcDbConnection(ConnectionProvider connectionProvider, ExecutorService executorService) {
        this.connectionProvider = connectionProvider;
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<? extends DbConnection> connect() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                connection = connectionProvider.getConnection();
            } catch (SQLException e) {
                throw new HibernateException(e);
            }
            return this;
        }, executorService);
    }

    @Override
    public CompletableFuture<? extends DbConnection> disconnect() {
        return CompletableFuture.supplyAsync(() -> {
            Connection localConnection = connection;
            connection = null;
            try {
                connectionProvider.closeConnection(localConnection);
            } catch (SQLException e) {
                throw new HibernateException(e);
            }
            return this;
        }, executorService);
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public CompletableFuture<QueryResult> sendQuery(String sql) {
        if (!isConnected()) {
            throw new IllegalStateException("not connected");
        }

        return CompletableFuture.supplyAsync(() -> {
            try (Statement s = connection.createStatement()) {
                boolean resultSet = s.execute(sql);
                if (resultSet) {
                    return new JdbcQueryResult(s.getResultSet());
                } else {
                    return new JdbcQueryResult(s.getUpdateCount());
                }
            } catch (SQLException sqle) {
                throw new HibernateException(sqle);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<QueryResult> sendPreparedStatement(String sql, List<Object> objects) {
        if (!isConnected()) {
            throw new IllegalStateException("not connected");
        }

        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int i = 1;
                for (Object object : objects) {
                    ps.setObject(i++, object);
                }
                boolean resultSet = ps.execute();
                if (resultSet) {
                    return new JdbcQueryResult(ps.getResultSet());
                } else {
                    return new JdbcQueryResult(ps.getUpdateCount());
                }
            } catch (SQLException sqle) {
                throw new HibernateException(sqle);
            }
        }, executorService);
    }

    @Override
    public <A> CompletableFuture<A> inTransaction(Function<DbConnection, CompletableFuture<A>> txFunction) {
        startTransaction();
        try {
            return txFunction.apply(this).whenComplete((result, throwable) -> {
                if (result != null) {
                    commit();
                } else {
                    rollback();
                }
            });
        } catch (Exception e) {
            rollback();
            throw new HibernateException(e);
        }
    }

    private void commit() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new HibernateException(e);
        }
    }

    private void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new HibernateException(e);
        }
    }

    private void startTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new HibernateException(e);
        }
    }
}
