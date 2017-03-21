package xianglesong.com.twandroid.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {
    public static long getTime(String dateTime) {
        Locale locale = Locale.CHINA;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                locale);
        try {
            return sdf.parse(dateTime).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDateTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    public static String getTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String getCurrentDateTime() {
        Date date = Calendar.getInstance().getTime();
        return getDateTime(date);
    }

    public static String getHour(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        return simpleDateFormat.format(date);
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.getHour(new Date()));

        new File("d:/ksname\2015-06-02\15\57777b70c017413c8a8a5fb36ee5de58");
    }
}
