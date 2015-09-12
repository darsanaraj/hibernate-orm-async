package org.hibernate.jpa.spi.async;

import java.util.Map;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public interface DbConnectionPoolProvider {

    public DbConnectionPool createDbConnectionPool(Map<String, Object> properties);

    public String getName();

}
