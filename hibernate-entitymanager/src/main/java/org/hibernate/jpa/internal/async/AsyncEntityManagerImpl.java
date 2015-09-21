package org.hibernate.jpa.internal.async;

import com.jakobk.async.db.DbConnectionPool;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import javax.persistence.RollbackException;
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
        return dbConnectionPool.inTransaction(connection -> {
            AsyncEntityTransactionImpl tx = new AsyncEntityTransactionImpl(new AsyncSessionImpl(sessionFactory, connection));
            return txFunction.apply(tx).whenComplete((result, throwable) -> {
                try {
                    if (throwable != null) {
                        if (throwable instanceof RuntimeException) {
                            throw (RuntimeException) throwable;
                        } else {
                            throw new HibernateException(throwable);
                        }
                    }
                    if (tx.getRollbackOnly()) {
                        throw new RollbackException("Rollback transaction, because rollbackOnly is true");
                    }
                } finally {
                    tx.close();
                }
            });
        });
    }

    @Override
    public <T> AsyncQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return new AsyncQueryImpl<>(qlString, resultClass, asyncSession);
    }

    @Override
    public void close() {
        // nothing to do here yet
    }

}
