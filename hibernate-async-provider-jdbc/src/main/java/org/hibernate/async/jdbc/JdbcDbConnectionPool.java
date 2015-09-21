package org.hibernate.async.jdbc;

import com.jakobk.async.db.DbConnection;
import com.jakobk.async.db.DbConnectionPool;
import com.jakobk.async.db.QueryResult;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 *
 */
public class JdbcDbConnectionPool implements DbConnectionPool {

    private final ConnectionProvider connectionProvider;
    private final ExecutorService executorService;

    public JdbcDbConnectionPool(ConnectionProvider connectionProvider, ExecutorService executorService) {
        this.connectionProvider = connectionProvider;
        this.executorService = executorService;
    }

    @Override
    public CompletableFuture<DbConnectionPool> connect() {
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public CompletableFuture<DbConnectionPool> disconnect() {
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public CompletableFuture<QueryResult> sendQuery(String sql) {
        return new JdbcDbConnection(connectionProvider, executorService).connect().thenCompose((connection) ->
                connection.sendQuery(sql)
                        .exceptionally(ex -> new JdbcQueryResult(-1))
                        .thenCompose(result -> connection.disconnect()
                                .thenApply(disconnectedConnection -> result)));
    }

    @Override
    public CompletableFuture<QueryResult> sendPreparedStatement(String sql, List<Object> objects) {
        return new JdbcDbConnection(connectionProvider, executorService).connect().thenCompose((connection) ->
                connection.sendPreparedStatement(sql, objects)
                        .exceptionally(ex -> new JdbcQueryResult(-1))
                        .thenCompose(result -> connection.disconnect()
                                .thenApply(disconnectedConnection -> result)));
    }

    @Override
    public <A> CompletableFuture<A> inTransaction(Function<DbConnection, CompletableFuture<A>> function) {
        return new JdbcDbConnection(connectionProvider, executorService).connect().thenCompose((connection) ->
                connection.inTransaction(function)
                        .exceptionally(ex -> null)
                        .thenCompose(result -> connection.disconnect()
                                .thenApply(disconnectedConnection -> result)));
    }
}
