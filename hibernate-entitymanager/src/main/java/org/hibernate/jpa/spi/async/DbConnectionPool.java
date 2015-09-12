package org.hibernate.jpa.spi.async;

import java.util.function.Consumer;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public interface DbConnectionPool {

    void getConnection(Consumer<DbConnection> handler, Consumer<Throwable> onError);

    void release(DbConnection connection);

    void close();

}
