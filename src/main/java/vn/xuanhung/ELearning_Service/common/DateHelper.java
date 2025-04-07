package vn.xuanhung.ELearning_Service.common;

import org.joda.time.DateTime;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateHelper {
    private static DateTimeFormatter patternYYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter patternddMMyyyy = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static DateTimeFormatter patternYYYYMMDDHHMMSS = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    public static DateTime toDateTime(Date date) {
       return new DateTime(date);
    }
}
