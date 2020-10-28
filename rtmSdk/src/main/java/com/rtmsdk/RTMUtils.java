package com.rtmsdk;

import com.fpnn.sdk.ErrorRecorder;
import com.fpnn.sdk.proto.Message;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

public class RTMUtils {
    static private AtomicLong orderId = new AtomicLong();

    static public long genMid() {
        long id = getCurrentMilliseconds()<<16;
        return id + orderId.incrementAndGet();
    }

    static long getCurrentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    static long getCurrentMilliseconds() {
        return System.currentTimeMillis();
    }

    static Map<String, String>  wantStringMap(Message message,String key) {
        Map<String, String> map = new HashMap<>();
        try {
            if (message != null) {
                Map<String, String> ret = (Map<String, String>) message.want(key);
                for (String value : ret.keySet())
                    map.put(value, ret.get(value));
            }
            return map;
        }
        catch (NoSuchElementException e)
        {
            ErrorRecorder.record(e.getMessage());
        }
        return map;
    }

    static void wantLongList(Message message,String key, List<Long> list) {
        try {
            if (message == null)
                return;
            List<Object> attrsList = (List<Object>) message.want(key);
            for (Object value : attrsList) {
                if (value instanceof Integer)
                    list.add(((Integer) value).longValue());
                else if (value instanceof Long)
                    list.add(((Long) value).longValue());
                else if (value instanceof BigInteger)
                    list.add(((BigInteger) value).longValue());
                else
                    list.add(Long.valueOf(String.valueOf(value)));
            }
        }
        catch (NoSuchElementException e)
        {
            ErrorRecorder.record(e.getMessage());
        }
    }

    static void wantIntList(Message message,String key, List<Integer> list) {
        try {
            if (message == null)
                return;
            List<Object> attrsList = (List<Object>) message.want(key);
            for (Object value : attrsList) {
                if (value instanceof Integer)
                    list.add(((Integer) value).intValue());
                else if (value instanceof Long)
                    list.add(((Long) value).intValue());
                else if (value instanceof BigInteger)
                    list.add(((BigInteger) value).intValue());
                else
                    list.add(Integer.valueOf(String.valueOf(value)));
            }
        }
        catch (NoSuchElementException e)
        {
            ErrorRecorder.record(e.getMessage());
        }
    }

    static void getIntList(Message message,String key, List<Integer> list) {
        if (message == null)
            return;
        List<Object> attrsList = (List<Object>)message.get(key);
        for (Object value : attrsList) {
            if (value instanceof Integer)
                list.add(((Integer) value).intValue());
            else if (value instanceof Long)
                list.add(((Long) value).intValue());
            else if (value instanceof BigInteger)
                list.add(((BigInteger) value).intValue());
            else
                list.add(Integer.valueOf(String.valueOf(value)));
        }
    }

    static void getStringList(Message message,String key, List<String> list) {
        if (message == null)
            return;
        List<Object> attrsList = (List<Object>)message.get(key);
        for (Object value : attrsList) {
            list.add(String.valueOf(value));
        }
    }

    static List<Map<String, String>> wantListHashMap(Message message, String key) {
        List<Map<String, String>> attributes = new ArrayList<>();
        try {
            List<Object> attrsList = (List<Object>) message.want(key);
            for (Object value : attrsList)
                attributes.add(new HashMap<>((Map<String, String>) value));
        }
        catch (NoSuchElementException e)
        {
            ErrorRecorder.record(e.getMessage());
        }
        return attributes;
    }

    static HashSet<Long> wantLongHashSet(Message message, String key) {
        HashSet<Long> uids = new HashSet<Long>();
        try{
            List<Object> list = (List<Object>)message.want(key);
            for (Object value : list) {
                if (value instanceof Integer)
                    uids.add(((Integer) value).longValue());
                else if (value instanceof Long)
                    uids.add(((Long) value).longValue());
                else if (value instanceof BigInteger)
                    uids.add(((BigInteger) value).longValue());
                else
                    uids.add(Long.valueOf(String.valueOf(value)));
            }
        }
        catch (NoSuchElementException e)
        {
            ErrorRecorder.record(e.getMessage());
        }
        return uids;
    }

    static long wantLong(Object obj) {
        long value = -1;
        if (obj instanceof Integer)
            value = ((Integer) obj).longValue();
        else if (obj instanceof Long)
            value = (Long) obj;
        else if (obj instanceof BigInteger)
            value = ((BigInteger) obj).longValue();
        else if (obj instanceof Short)
            value = ((Short) obj).longValue();
        else if (obj instanceof Byte)
            value = ((Byte) obj).longValue();
        else
            value = Long.valueOf(String.valueOf(obj));
        return value;
    }

    static int wantInt(Object obj) {
        int value = -1;
        if (obj instanceof Integer)
            value = (Integer) obj;
        else if (obj instanceof Long)
            value = ((Long) obj).intValue();
        else if (obj instanceof BigInteger)
            value = ((BigInteger) obj).intValue();
        else if (obj instanceof Short)
            value = ((Short) obj).intValue();
        else if (obj instanceof Byte)
            value = ((Byte) obj).intValue();
        else
            value = Integer.valueOf(String.valueOf(obj));
        return value;
    }

    public static String bytesToHexString(byte[] bytes, boolean isLowerCase) {
        String from = isLowerCase ? "%02x" : "%02X";
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format(from, b);
        }
        return sb.toString();
    }

}