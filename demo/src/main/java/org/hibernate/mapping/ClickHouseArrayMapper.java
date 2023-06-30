package org.hibernate.mapping;

import java.util.*;

public class ClickHouseArrayMapper {


    public static LinkedList<String> getOrderedStringSet(Object[] result) {
        LinkedList<String> strings = new LinkedList();
        if (result == null) {
            return new LinkedList();
        }
        int length = result.length;

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < length; i++) {
            char letter = (char) ((byte) result[i]);
            if (letter == ',' || letter == ']') {
                strings.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(letter);
            }

        }

        return strings;
    }

    public static LinkedList<Integer> getOrderedIntegerSet(Object[] result) {
        LinkedList<Integer> strings = new LinkedList();
        if (result == null) {
            return new LinkedList();
        }
        int length = result.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < length; i++) {
            char letter = (char) ((byte) result[i]);
            if (letter == ',' || letter == ']') {
                strings.add(Integer.valueOf(sb.toString()));
                sb = new StringBuilder();
            } else {
                sb.append(letter);
            }
        }
        return strings;
    }

}
