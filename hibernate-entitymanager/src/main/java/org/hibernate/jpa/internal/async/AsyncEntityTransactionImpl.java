package org.hibernate.jpa.internal.async;

import org.hibernate.engine.spi.AsyncSessionImplementor;

import javax.persistence.async.AsyncEntityTransaction;
import javax.persistence.async.AsyncQuery;

/**
 * Implementation of an asynchronous transaction, which is able to create and execute queries in an isolated transaction.
 */
public class AsyncEntityTransactionImpl implements AsyncEntityTransaction {

    private final AsyncSessionImplementor asyncSession;
    private boolean rollbackOnly = false;
    private boolean active = true;

    public AsyncEntityTransactionImpl(AsyncSessionImplementor asyncSession) {
        this.asyncSession = asyncSession;
    }

    @Override
    public void setRollbackOnly() {
        rollbackOnly = true;
    }

    @Override
    public boolean getRollbackOnly() {
        return rollbackOnly;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public <T> AsyncQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return new AsyncQueryImpl<>(qlString, resultClass, asyncSession);
    }

    void close() {
        active = false;
    }

}
