package org.hibernate.jpa.internal.async;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.query.spi.ParameterMetadata;
import org.hibernate.engine.spi.AsyncSessionImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.jpa.internal.QueryImpl;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.async.AsyncQuery;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 *  @author Jakob Korherr
 */
public class AsyncQueryImpl<ResultType> implements AsyncQuery<ResultType> {

    private final Class<ResultType> resultClass;
    private final AsyncSessionImplementor asyncSession;
    private final AsyncHibernateQueryDelegate queryDelegate;
    private final AsyncJpaQueryDelegate jpaQueryDelegate;

    public AsyncQueryImpl(String qlString, Class<ResultType> resultClass, AsyncSessionImplementor asyncSession) {
        this.resultClass = resultClass;
        this.asyncSession = asyncSession;

        HQLQueryPlan hqlQueryPlan = getHQLQueryPlan(qlString, false);
        queryDelegate = new AsyncHibernateQueryDelegate(qlString, asyncSession, hqlQueryPlan.getParameterMetadata());
        jpaQueryDelegate = new AsyncJpaQueryDelegate(queryDelegate);
    }

    protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
        return asyncSession.getFactory().getQueryPlanCache().getHQLQueryPlan(query, shallow, Collections.emptyMap());
    }

    @Override
    public CompletableFuture<List<ResultType>> getResultList() {
        Map<String, TypedValue> namedParamsCopy = queryDelegate.getNamedParams();
        String expandedQuery = queryDelegate.expandParameterLists(namedParamsCopy);  // side-effect: also adds named params to map!
        HQLQueryPlan hqlQueryPlan = getHQLQueryPlan(expandedQuery, false);
        return hqlQueryPlan.performListAsync(queryDelegate.getQueryParameters(namedParamsCopy), asyncSession)
                .thenApply(uncheckedList -> {
                    @SuppressWarnings("unchecked")
                    List<ResultType> checkedList = (List<ResultType>) uncheckedList;
                    return checkedList;
                });
    }

    @Override
    public CompletableFuture<ResultType> getSingleResult() {
        return getResultList().thenApply(result -> {
            if ( result.size() == 0 ) {
                throw new NoResultException( "No entity found for query" );
            } else if ( result.size() > 1 ) {
                final Set<ResultType> uniqueResult = new HashSet<>(result);
                if ( uniqueResult.size() > 1 ) {
                    throw new NonUniqueResultException( "result returns more than one elements" );
                } else {
                    return uniqueResult.iterator().next();
                }
            } else {
                return result.get( 0 );
            }
        });
    }

    @Override
    public CompletableFuture<Integer> executeUpdate() {
        Map<String, TypedValue> namedParamsCopy = queryDelegate.getNamedParams();
        String expandedQuery = queryDelegate.expandParameterLists(namedParamsCopy);  // side-effect: also adds named params to map!
        HQLQueryPlan hqlQueryPlan = getHQLQueryPlan(expandedQuery, false);
        return hqlQueryPlan.performExecuteUpdateAsync(queryDelegate.getQueryParameters(namedParamsCopy), asyncSession);
    }

    @Override
    public AsyncQuery<ResultType> setMaxResults(int maxResult) {
        jpaQueryDelegate.setMaxResults(maxResult);
        return this;
    }

    @Override
    public int getMaxResults() {
        return jpaQueryDelegate.getMaxResults();
    }

    @Override
    public AsyncQuery<ResultType> setFirstResult(int startPosition) {
        jpaQueryDelegate.setFirstResult(startPosition);
        return this;
    }

    @Override
    public int getFirstResult() {
        return jpaQueryDelegate.getFirstResult();
    }

    @Override
    public AsyncQuery<ResultType> setHint(String hintName, Object value) {
        jpaQueryDelegate.setHint(hintName, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return jpaQueryDelegate.getHints();
    }

    @Override
    public <T> AsyncQuery<ResultType> setParameter(Parameter<T> param, T value) {
        jpaQueryDelegate.setParameter(param, value);
        return this;
    }

    @Override
    public AsyncQuery<ResultType> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        jpaQueryDelegate.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public AsyncQuery<ResultType> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        jpaQueryDelegate.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public AsyncQuery<ResultType> setParameter(String name, Object value) {
        jpaQueryDelegate.setParameter(name, value);
        return this;
    }

    @Override
    public AsyncQuery<ResultType> setParameter(String name, Calendar value, TemporalType temporalType) {
        jpaQueryDelegate.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public AsyncQuery<ResultType> setParameter(String name, Date value, TemporalType temporalType) {
        jpaQueryDelegate.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public AsyncQuery<ResultType> setParameter(int position, Object value) {
        jpaQueryDelegate.setParameter(position, value);
        return this;
    }

    @Override
    public AsyncQuery<ResultType> setParameter(int position, Calendar value, TemporalType temporalType) {
        jpaQueryDelegate.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public AsyncQuery<ResultType> setParameter(int position, Date value, TemporalType temporalType) {
        jpaQueryDelegate.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Parameter<?>> getParameters() {
        return jpaQueryDelegate.getParameters();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return jpaQueryDelegate.getParameter(name);
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return jpaQueryDelegate.getParameter(name, type);

    }

    @Override
    public Parameter<?> getParameter(int position) {
        return jpaQueryDelegate.getParameter(position);
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return jpaQueryDelegate.getParameter(position, type);
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return jpaQueryDelegate.isBound(param);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return jpaQueryDelegate.getParameterValue(param);
    }

    @Override
    public Object getParameterValue(String name) {
        return jpaQueryDelegate.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        return jpaQueryDelegate.getParameterValue(position);
    }

    @Override
    public AsyncQuery<ResultType> setFlushMode(FlushModeType flushMode) {
        jpaQueryDelegate.setFlushMode(flushMode);
        return this;
    }

    @Override
    public FlushModeType getFlushMode() {
        return jpaQueryDelegate.getFlushMode();
    }

    @Override
    public AsyncQuery<ResultType> setLockMode(LockModeType lockMode) {
        jpaQueryDelegate.setLockMode(lockMode);
        return this;
    }

    @Override
    public LockModeType getLockMode() {
        return jpaQueryDelegate.getLockMode();
    }

    private static class AsyncHibernateQueryDelegate extends org.hibernate.internal.AbstractQueryImpl {

        private LockOptions lockOptions = new LockOptions();

        public AsyncHibernateQueryDelegate(String queryString, AsyncSessionImplementor asyncSession, ParameterMetadata parameterMetadata) {
            super(queryString, null, asyncSession, parameterMetadata);
        }

        @Override
        public String expandParameterLists(Map namedParamsCopy) {
            return super.expandParameterLists(namedParamsCopy);
        }

        @Override
        public Map<String, TypedValue> getNamedParams() {
            return super.getNamedParams();
        }

        @Override
        public LockOptions getLockOptions() {
            return lockOptions;
        }

        @Override
        public Query setLockOptions(LockOptions lockOption) {
            this.lockOptions.setLockMode(lockOption.getLockMode());
            this.lockOptions.setScope(lockOption.getScope());
            this.lockOptions.setTimeOut(lockOption.getTimeOut());
            return this;
        }

        @Override
        public Query setLockMode(String alias, LockMode lockMode) {
            lockOptions.setAliasSpecificLockMode( alias, lockMode );
            return this;
        }

        @Override
        public Iterator iterate() {
            throw new IllegalStateException("async query - synchronous method not implemented");
        }

        @Override
        public ScrollableResults scroll() {
            throw new IllegalStateException("async query - synchronous method not implemented");
        }

        @Override
        public ScrollableResults scroll(ScrollMode scrollMode) {
            throw new IllegalStateException("async query - synchronous method not implemented");
        }

        @Override
        public List list() {
            throw new IllegalStateException("async query - synchronous method not implemented");
        }

        @Override
        public int executeUpdate() {
            throw new IllegalStateException("async query - synchronous method not implemented");
        }

    }

    private class AsyncJpaQueryDelegate extends QueryImpl<ResultType> {

        public AsyncJpaQueryDelegate(AsyncHibernateQueryDelegate query) {
            super(query, null);
        }

        @Override
        protected void checkOpen(boolean markForRollbackIfClosed) {
            // nothing
        }
    }

}
