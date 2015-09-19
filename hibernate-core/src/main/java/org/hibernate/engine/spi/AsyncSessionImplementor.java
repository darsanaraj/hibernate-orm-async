package org.hibernate.engine.spi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous version of {@link SessionImplementor}.
 *
 * Extends {@link SessionImplementor}, although many methods of it are not applicable to the asynchronous
 * implementation. That is because the asynchronous session needs to be used in many places in existing hibernate
 * code, in order to implement asynchronous functionalities, e.g. JPQL query translation and execution. Implementations
 * are therefore expected to provide only mock implementations of most of the methods that come from
 * {@link SessionImplementor}.
 *
 * @author Jakob Korherr
 */
public interface AsyncSessionImplementor extends SessionImplementor {

    PreparedStatement createRecordingPreparedStatement(String sql);

    CompletableFuture<ResultSet> executeQueryAsync(PreparedStatement recordingPreparedStatement);

    CompletableFuture<Integer> executeUpdateAsync(PreparedStatement recordingPreparedStatement);

    // TODO async db methods

}
