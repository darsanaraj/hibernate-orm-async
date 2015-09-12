package org.hibernate.jpa.internal.async;

import org.hibernate.HibernateException;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.jpa.spi.async.DbConnectionPool;

import javax.persistence.async.AsyncEntityManager;
import javax.persistence.async.AsyncEntityTransaction;
import javax.persistence.async.AsyncQuery;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous EntityManager implementation.
 *
 * @author Jakob Korherr
 */
public class AsyncEntityManagerImpl implements AsyncEntityManager {

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
    public <T> CompletableFuture<AsyncQuery<T>> createQuery(String qlString, Class<T> resultClass) {
        HQLQueryPlan queryPlan = getHQLQueryPlan(qlString, false);
        String[] sqlStrings = queryPlan.getSqlStrings();
        for (String sqlString : sqlStrings) {
            System.out.println(sqlString);
        }

        QueryTranslator[] translators = queryPlan.getTranslators();
        QueryTranslator translator = translators[0];
        return translator.listAsync(new AsyncSessionImpl(sessionFactory), new QueryParameters())
                .thenApply(list -> {
                    list.forEach(objectRow -> {
//                        Object[] row = (Object[]) objectRow;
//                        System.out.println(row[0] + " " + row[1]);
                        System.out.println(objectRow);
                    });
                    return null;
                });

    }

    protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
        return sessionFactory.getQueryPlanCache().getHQLQueryPlan(query, shallow, Collections.emptyMap());
    }

    @Override
    public void close() {

    }
}
