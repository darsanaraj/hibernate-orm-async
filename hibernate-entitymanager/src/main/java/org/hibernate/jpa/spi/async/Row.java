package org.hibernate.jpa.spi.async;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * TODO
 *
 * @author Jakob Korherr
 */
public interface Row {

    String getString(int index);

    String getString(String column);

    Byte getByte(int index);

    Byte getByte(String column);

    Character getChar(int index);

    Character getChar(String column);

    Short getShort(int index);

    Short getShort(String column);

    Integer getInt(int index);

    Integer getInt(String column);

    Long getLong(int index);

    Long getLong(String column);

    BigInteger getBigInteger(int index);

    BigInteger getBigInteger(String column);

    BigDecimal getBigDecimal(int index);

    BigDecimal getBigDecimal(String column);

    Date getDate(int index);

    Date getDate(String column);

    Time getTime(int index);

    Time getTime(String column);

    Timestamp getTimestamp(int index);

    Timestamp getTimestamp(String column);

    byte[] getBytes(int index);

    byte[] getBytes(String column);

    <T> T get(int index, Class<T> type);

    <T> T get(String column, Class<T> type);

    <TArray> TArray getArray(String column, Class<TArray> arrayType);

    <TArray> TArray getArray(int index, Class<TArray> arrayType);
}
