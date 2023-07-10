package com.example.demo.types;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

public class LongArrayUserType implements UserType<long[]> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<long[]> returnedClass() {
        return long[].class;
    }

    @Override
    public boolean equals(long[] x, long[] y) {
       if (x.length != y.length) {
           return false;
       }
       for (int i =0; i <x.length; i++) {
           if (!(x[i] == y[i])) {
               return false;
           }
       }
       return true;
    }

    @Override
    public int hashCode(long[] x) {
        return Arrays.hashCode(x);
    }

    @Override
    public long[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        Array array = rs.getArray(position);
        if (array == null) {
            return null;
        }
        return (long[]) array.getArray();
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement,long[] value, int index, SharedSessionContractImplementor session) throws SQLException {
        Connection connection = preparedStatement.getConnection();
        if (value == null) {
            preparedStatement.setNull(index, Types.ARRAY);
        } else {
            Long[] castObject = ArrayUtils.toObject(value);
            Array array = connection.createArrayOf("Array(UInt64)", castObject);
            preparedStatement.setArray(index, array);
        }
    }


    @Override
    public long[] deepCopy(long[] value) {
        if (value == null) {
            return null;
        }
        long[] source = (long[]) value;
        return Arrays.copyOf(source, source.length);
    }



    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(long[] value) {
        Object deepCopy = deepCopy(value);
        if (deepCopy instanceof Serializable) {
            return (Serializable) deepCopy;
        }
        throw new UnsupportedOperationException("Cannot serialize " + value);

    }

    @Override
    public long[] assemble(Serializable cached, Object owner) {
        return new long[0];
    }


}
