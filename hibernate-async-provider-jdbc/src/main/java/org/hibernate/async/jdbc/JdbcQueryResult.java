package org.hibernate.async.jdbc;

import com.jakobk.async.db.QueryResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 */
public class JdbcQueryResult implements QueryResult {

    private final Optional<ResultSet> resultSetOptional;
    private final int updateCount;

    public JdbcQueryResult(ResultSet resultSetOptional) throws SQLException {
        this.resultSetOptional = Optional.of(new PreFetchedResultSet(resultSetOptional));
        this.updateCount = 0;
    }

    public JdbcQueryResult(int updateCount) {
        this.resultSetOptional = Optional.empty();
        this.updateCount = updateCount;
    }

    @Override
    public Optional<ResultSet> getResultSet() {
        return resultSetOptional;
    }

    @Override
    public long getAffectedRowCount() {
        return updateCount;
    }

    @Override
    public String getStatusMessage() {
        return null;
    }
}
