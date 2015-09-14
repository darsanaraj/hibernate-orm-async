package org.hibernate.jpa.spi.async;

import com.jakobk.async.db.DbConnection;

import java.util.Map;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public interface DbConnectionPoolProvider {

    public DbConnection createDbConnectionPool(Map<String, Object> properties);  // TODO integrate DbConnection interface into Hibernate SPI

    public String getName();

}
