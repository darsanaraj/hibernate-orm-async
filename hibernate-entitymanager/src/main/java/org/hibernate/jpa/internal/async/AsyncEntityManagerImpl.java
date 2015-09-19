package org.hibernate.jpa.internal.async;

import com.jakobk.async.db.DbConnectionPool;
import org.hibernate.engine.spi.AsyncSessionImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import javax.persistence.async.AsyncEntityManager;
import javax.persistence.async.AsyncEntityTransaction;
import javax.persistence.async.AsyncQuery;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Asynchronous EntityManager implementation.
 *
 * @author Jakob Korherr
 */
public class AsyncEntityManagerImpl implements AsyncEntityManager {

    private final DbConnectionPool dbConnectionPool;
    private final SessionFactoryImplementor sessionFactory;
    private final AsyncSessionImpl asyncSession;

    public AsyncEntityManagerImpl(DbConnectionPool dbConnectionPool, SessionFactoryImplementor sessionFactory) {
        this.dbConnectionPool = dbConnectionPool;
        this.sessionFactory = sessionFactory;
        this.asyncSession = new AsyncSessionImpl(sessionFactory, dbConnectionPool);
    }

    @Override
    public <A> CompletableFuture<A> inTransaction(Function<AsyncEntityTransaction, CompletableFuture<A>> txFunction) {
        throw new IllegalStateException("not implemented");   // TODO jakobk
    }

    @Override
    public <T> AsyncQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return new AsyncQueryImpl<>(qlString, resultClass, this);
    }

    @Override
    public void close() {
        // nothing to do here yet
    }

    protected SessionFactoryImplementor getSessionFactory() {
        return sessionFactory;
    }

    public AsyncSessionImplementor getAsyncSession() {
        return asyncSession;
    }
}
