package org.hibernate.jpa.internal.async;

import com.jakobk.async.db.DbConnectionPool;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.cache.spi.CacheKey;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.StatefulPersistenceContext;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.AsyncSessionImplementor;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.resource.transaction.TransactionCoordinator;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous counterpart of {@link org.hibernate.internal.SessionImpl}.
 *
 * Most of the methods from {@link org.hibernate.engine.spi.SessionImplementor} throw an
 * {@link IllegalStateException}, however, some are implemented with (mock) implementations in order to suffice
 * the internal API requirements of hibernate, when using (mostly) existing code to translate and execute JPQL queries.
 *
 * Implements the methods from {@link org.hibernate.engine.spi.AsyncSessionImplementor}
 *
 * @author Jakob Korherr
 */
public class AsyncSessionImpl implements AsyncSessionImplementor {

    private final SessionFactoryImplementor sessionFactory;
    private final DbConnectionPool dbConnectionPool;

    private final PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext( this );

    public AsyncSessionImpl(SessionFactoryImplementor sessionFactory, DbConnectionPool dbConnectionPool) {
        this.sessionFactory = sessionFactory;
        this.dbConnectionPool = dbConnectionPool;
    }

    @Override
    public PreparedStatement createRecordingPreparedStatement(String sql) {
        return new RecordingPreparedStatement(sql);
    }

    @Override
    public CompletableFuture<ResultSet> executeQueryAsync(PreparedStatement recordingPreparedStatement) {
        if (recordingPreparedStatement instanceof RecordingPreparedStatement) {
            RecordingPreparedStatement st = (RecordingPreparedStatement) recordingPreparedStatement;

            return dbConnectionPool.sendPreparedStatement(st.getSql(), st.getParameters())
                    .thenApply(result -> result.getResultSet().get());
        } else {
            throw new IllegalArgumentException("PreparedStatement must be a RecordingPreparedStatement");
        }
    }

    @Override
    public CompletableFuture<Integer> executeUpdateAsync(PreparedStatement recordingPreparedStatement) {
        if (recordingPreparedStatement instanceof RecordingPreparedStatement) {
            RecordingPreparedStatement st = (RecordingPreparedStatement) recordingPreparedStatement;

            return dbConnectionPool.sendPreparedStatement(st.getSql(), st.getParameters())
                    .thenApply(result -> (int) result.getAffectedRowCount());
        } else {
            throw new IllegalArgumentException("PreparedStatement must be a RecordingPreparedStatement");
        }
    }

    @Override
    public String getTenantIdentifier() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public JdbcConnectionAccess getJdbcConnectionAccess() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
        return new EntityKey( id, persister );
    }

    @Override
    public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Interceptor getInterceptor() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void setAutoClear(boolean enabled) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void disableTransactionAutoJoin() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isTransactionInProgress() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public long getTimestamp() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public SessionFactoryImplementor getFactory() {
        return sessionFactory;
    }

    @Override
    public List list(String query, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public List list(Criteria criteria) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
        return null;
    }

    @Override
    public Serializable getContextEntityIdentifier(Object object) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public String bestGuessEntityName(Object object) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public String guessEntityName(Object entity) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Object instantiate(String entityName, Serializable id) throws HibernateException {
        return getFactory().getEntityPersister( entityName ).instantiate( id, this );
    }

    @Override
    public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Object getFilterParameterValue(String filterParameterName) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Type getFilterParameterType(String filterParameterName) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Map getEnabledFilters() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public int getDontFlushFromFind() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return temporaryPersistenceContext;
    }

    @Override
    public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public CacheMode getCacheMode() {
        return CacheMode.IGNORE;
    }

    @Override
    public void setCacheMode(CacheMode cm) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isOpen() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isConnected() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public FlushMode getFlushMode() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void setFlushMode(FlushMode fm) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Connection connection() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void flush() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Query getNamedQuery(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Query getNamedSQLQuery(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isEventSource() {
        return false;
    }

    @Override
    public void afterScrollOperation() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public String getFetchProfile() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void setFetchProfile(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public TransactionCoordinator getTransactionCoordinator() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public JdbcCoordinator getJdbcCoordinator() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isClosed() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean shouldAutoClose() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isAutoCloseSessionEnabled() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public LoadQueryInfluencers getLoadQueryInfluencers() {
        return LoadQueryInfluencers.NONE;
    }

    @Override
    public Query createQuery(NamedQueryDefinition namedQueryDefinition) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public SessionEventListenerManager getEventListenerManager() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public <T> T execute(Callback<T> callback) {
        throw new IllegalStateException("not implemented");
    }
}
