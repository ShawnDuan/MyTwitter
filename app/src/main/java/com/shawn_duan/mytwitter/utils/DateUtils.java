package com.shawn_duan.mytwitter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by sduan on 10/29/16.
 */

public class DateUtils {

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = android.text.format.DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] strings = relativeDate.split(" ");
        return strings[0] + strings[1].charAt(0);
    }
}
