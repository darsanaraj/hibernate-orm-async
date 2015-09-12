package org.hibernate.jpa.spi.async;

import java.util.function.Consumer;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public interface DbConnection extends QueryExecutor {

    void begin(Consumer<Transaction> onTransaction, Consumer<Throwable> onError);

    void close();

}
