package org.hibernate.jpa.internal.async;

import com.jakobk.async.db.DbConnectionPool;
import org.hibernate.HibernateException;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.AsyncSessionImplementor;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.spi.QueryTranslator;

import javax.persistence.async.AsyncEntityManager;
import javax.persistence.async.AsyncEntityTransaction;
import javax.persistence.async.AsyncQuery;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous EntityManager implementation.
 *
 * @author Jakob Korherr
 */
public class AsyncEntityManagerImpl implements AsyncEntityManager, AsyncSessionHolder {

    private final DbConnectionPool dbConnectionPool;
    private final SessionFactoryImplementor sessionFactory;

    public AsyncEntityManagerImpl(DbConnectionPool dbConnectionPool, SessionFactoryImplementor sessionFactory) {
        this.dbConnectionPool = dbConnectionPool;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public CompletableFuture<AsyncEntityTransaction> beginTransaction() {
        return null;
    }

    @Override
    public void persist() {

    }

    @Override
    public CompletableFuture<Void> flush() {
        return null;
    }

    @Override
    public <T> AsyncQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return new AsyncQueryImpl<>(qlString, resultClass, this, this);
    }

    @Override
    public void close() {

    }

    protected DbConnectionPool getDbConnectionPool() {
        return dbConnectionPool;
    }

    protected SessionFactoryImplementor getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public AsyncSessionImplementor getAsyncSession() {
        return new AsyncSessionImpl(sessionFactory);
    }
}
