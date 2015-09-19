package org.hibernate.async.spi;

import com.jakobk.async.db.DbConnectionPool;

import java.util.Map;

/**
 * Provider SPI for an asynchronous database connection pool.
 *
 * The provider has to be in the classpath and registered via Java's ServiceLoader mechanism (i.e. META-INF/services),
 * and additionally the persistence unit must reference the respective pool provider name via the property
 * hibernate.async.provider in persistence.xml.
 *
 * @author Jakob Korherr
 */
public interface DbConnectionPoolProvider {

    public DbConnectionPool createDbConnectionPool(Map<String, Object> properties);  // TODO integrate DbConnection interface into Hibernate SPI

    public String getName();

}
