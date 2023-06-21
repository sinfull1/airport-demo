package org.hibernate.mapping;

public class ClickHouseArrayMapper {




    public static String getStringArray(Object[] result) {
        StringBuilder sb = new StringBuilder();
        for (Object o : result) {
            sb.append((char) ((byte) o));
        }

        return sb.toString();
    }
    public static String getIntArray(Object[] result) {
        StringBuilder sb = new StringBuilder();
        for (Object o : result) {
            sb.append((char) ((byte) o));
        }

        return sb.toString();
    }
}
