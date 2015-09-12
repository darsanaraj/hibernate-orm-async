package org.hibernate.jpa.spi.async;

import java.util.Collection;
import java.util.Iterator;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public interface ResultSet extends Iterable<Row> {

    /**
     * @return Column names in order.
     */
    Collection<String> getColumns();

    /**
     * @return Row iterator
     */
    Iterator<Row> iterator();

    /**
     * @param index Row index starting from 0
     * @return Row, never null
     */
    Row row(int index);

    /**
     * @return Amount of result rows.
     */
    int size();

    /**
     * @return Amount of modified rows.
     */
    int updatedRows();

}
