package com.example.demo.types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

public class StringArrayUserType implements UserType<String[]> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<String[]> returnedClass() {
        return String[].class;
    }

    @Override
    public boolean equals(String[] x, String[] y) {
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; i++) {
            if (!x[i].equals(y[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode(String[] x) {
        return Arrays.hashCode(x);
    }

    @Override
    public String[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        Array array = rs.getArray(position);
        if (array == null) {
            return null;
        }
        return (String[]) array.getArray();
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, String[] value, int index, SharedSessionContractImplementor session) throws SQLException {
        Connection connection = preparedStatement.getConnection();
        if (value == null) {
            preparedStatement.setNull(index, Types.ARRAY);
        } else {
            String[] castObject = value;
            Array array = connection.createArrayOf("Array(String)", castObject);
            preparedStatement.setArray(index, array);
        }
    }


    @Override
    public String[] deepCopy(String[] value) {
        if (value == null) {
            return null;
        }
        String[] source = value;
        return Arrays.copyOf(source, source.length);
    }


    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(String[] value) {
        Object deepCopy = deepCopy(value);
        if (deepCopy instanceof Serializable) {
            return (Serializable) deepCopy;
        }
        throw new UnsupportedOperationException("Cannot serialize " + value);

    }

    @Override
    public String[] assemble(Serializable cached, Object owner) {
        return new String[0];
    }


}
