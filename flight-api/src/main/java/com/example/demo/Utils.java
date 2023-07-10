package com.example.demo;

import org.apache.commons.lang3.ArrayUtils;

import java.util.LinkedList;

public class Utils {

    public static String[] getStringArray(Object[] objects) {
        LinkedList<String> lists = new LinkedList<>();
        if (objects == null) {
            return null;
        }
        int length = objects.length;

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < length; i++) {
            char letter = (char) ((byte) objects[i]);
            if (letter == ',' || letter == ']') {
                lists.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(letter);
            }

        }
        return lists.toArray(new String[lists.size()]);
    }

    public static long[] getIntegerArray(Object[] result) {
        LinkedList<Long> lists = new LinkedList<>();
        if (result == null) {
            return null;
        }
        int length = result.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < length; i++) {
            char letter = (char) ((byte) result[i]);
            if (letter == ',' || letter == ']') {
                lists.add(Long.valueOf(sb.toString()));
                sb = new StringBuilder();
            } else {
                sb.append(letter);
            }
        }
        return ArrayUtils.toPrimitive(lists.toArray(new Long[lists.size()]));
    }

}
