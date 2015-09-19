package org.hibernate.jpa.internal.async;

import org.hibernate.engine.spi.AsyncSessionImplementor;

/**
 *
 */
public interface AsyncSessionHolder {  // TODO jakobk: really necessary??

    public AsyncSessionImplementor getAsyncSession();

}
