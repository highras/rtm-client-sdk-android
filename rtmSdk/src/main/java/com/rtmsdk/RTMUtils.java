package com.rtmsdk;

import com.fpnn.sdk.proto.Message;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class RTMUtils {
    static private AtomicLong orderId = new AtomicLong();

    static public long genMid() {
        long id = getCurrentMilliseconds();
        return id + orderId.incrementAndGet();
    }

    static long getCurrentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    static long getCurrentMilliseconds() {
        return System.currentTimeMillis();
    }

    static byte[] intToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte) (value & 0xFF);
        byteArray[1] = (byte) (value >> 8 & 0xFF);
        byteArray[2] = (byte) (value >> 16 & 0xFF);
        byteArray[3] = (byte) (value >> 24 & 0xFF);
        return byteArray;
    }

    static int byteArrayToInt(byte[] byteArray){
        if(byteArray.length != 4){
            return 0;
        }
        int value = byteArray[0] & 0xFF;
        value |= byteArray[1] << 8;
        value |= byteArray[2] << 16;
        value |= byteArray[3] << 24;
        return value;
    }

    static Map<String, String>  wantStringMap(Message message,String key) {
        Map<String, String> map = new HashMap<>();
        if (message != null){
            Map<String, String> ret = (Map<String, String>) message.want(key);
            for (String value : ret.keySet())
                map.put(value, ret.get(key));
        }
        return map;
    }

    static void wantLongList(Message message,String key, List<Long> list) {
        if (message == null)
            return;
        List<Object> attrsList = (List<Object>)message.want(key);
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

    static List<Map<String, String>> wantListHashMap(Message message, String key) {
        List<Map<String, String>> attributes = new ArrayList<>();
        List<Object> attrsList = (List<Object>)message.want(key);
        for (Object value : attrsList)
            attributes.add(new HashMap<>((Map<String, String>) value));
        return attributes;
    }

    static HashSet<Long> wantLongHashSet(Message message, String key) {
        HashSet<Long> uids = new HashSet<Long>();
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

}


class MD5Utils {
    private static final String[] hexDigIts = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    static String getMd5(byte[] origin, boolean upper) {
        return getMd5(new String(origin), upper, "utf-8");
    }

    static String getMd5(String origin, boolean upper) {
        return getMd5(origin, upper, "utf-8");
    }

    static String getMd5(byte[] origin, boolean upper, String charsetname) {
        return getMd5(new String(origin), upper, charsetname);
    }

    static String getMd5(String origin, boolean upper, String charsetname) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (null == charsetname || "".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resultString != null && upper)
            return resultString.toUpperCase();
        return resultString;
    }

    static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte i: b)
            resultSb.append(byteToHexString(i));
        return resultSb.toString();
    }

    static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigIts[d1] + hexDigIts[d2];
    }
}