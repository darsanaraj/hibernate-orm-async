package org.hibernate.engine.spi;

import org.hibernate.ScrollMode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public interface AsyncSessionImplementor extends SessionImplementor {

    PreparedStatement createRecordingPreparedStatement(String sql);  // TODO interface for prepared statement

    CompletableFuture<ResultSet> executeQueryAsync(PreparedStatement recordingPreparedStatement);

    CompletableFuture<Integer> executeUpdateAsync(PreparedStatement recordingPreparedStatement);

    // TODO async db methods

}
