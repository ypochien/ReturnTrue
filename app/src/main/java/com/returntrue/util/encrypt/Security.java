package com.returntrue.util.encrypt;


import com.returntrue.util.JLog;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 計算神秘數字
 *
 * @author JosephWang
 */
public class Security {

    private static final int s1 = 101, s2 = 151;
    private static final String s3 = "fetecs";

    public static String getSc(Long currentTimeMillis) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(currentTimeMillis);
        String baseTime = sdf.format(date);
        JLog.d(JLog.JosephWang, "baseTime: " + baseTime);
        int year = Integer.parseInt(baseTime.substring(0, 4));
        int month = Integer.parseInt(baseTime.substring(4, 6));
        int day = Integer.parseInt(baseTime.substring(6, 8));
        int hour = Integer.parseInt(baseTime.substring(8, 10));
        int min = Integer.parseInt(baseTime.substring(10, 12));
//        int sec = Integer.parseInt(baseTime.substring(12, 14));
//        Log.logd("year: " + year, 4);
//        Log.logd("month: " + month, 4);
//        Log.logd("day: " + day, 4);
//        Log.logd("hour: " + hour, 4);
//        Log.logd("min: " + min, 4);
//        Log.logd("sec: " + sec, 4);
        int sum = s1 * year + s1 * (hour * 100 + month) + s2 * (min * 100 + day) - s1 * min;
        String sha1 = AeSimpleSHA1.SHA1(String.valueOf(sum));
//        Log.logd("sha1: " + sha1, 4);
        String sc = MD5.getMD5EncryptedString(sha1 + s3);
        JLog.d(JLog.JosephWang, "sc: " + sc);
        return sc;
    }
    
    public static String getSc() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return getSc(System.currentTimeMillis());
    }
}
