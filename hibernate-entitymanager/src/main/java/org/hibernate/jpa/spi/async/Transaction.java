package org.hibernate.jpa.spi.async;

import java.util.function.Consumer;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public interface Transaction extends QueryExecutor {

    /**
     * Commits a transaction.
     *
     * @param onCompleted Called when commit completes
     * @param onError Called on exception thrown
     */
    void commit(Runnable onCompleted, Consumer<Throwable> onError);

    /**
     * Rollbacks a transaction.
     *
     * @param onCompleted Called when rollback completes
     * @param onError Called on exception thrown
     */
    void rollback(Runnable onCompleted, Consumer<Throwable> onError);

}
