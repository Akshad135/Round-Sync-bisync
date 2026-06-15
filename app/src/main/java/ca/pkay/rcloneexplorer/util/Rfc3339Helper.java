package ca.pkay.rcloneexplorer.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rfc3339Helper {

    private static final Pattern PATTERN = Pattern.compile(
        "^(\\d{4})-(\\d{2})-(\\d{2})[T ](\\d{2}):(\\d{2}):(\\d{2})(?:\\.(\\d+))?(Z|[+-]\\d{2}:?\\d{2})?$"
    );

    public static Calendar parseCalendar(String input) throws java.text.ParseException {
        if (input == null) {
            throw new java.text.ParseException("Null input", 0);
        }
        Matcher m = PATTERN.matcher(input.trim().toUpperCase(Locale.US));
        if (!m.matches()) {
            throw new java.text.ParseException("Invalid RFC 3339 format: " + input, 0);
        }
        int year = Integer.parseInt(m.group(1));
        int month = Integer.parseInt(m.group(2)) - 1; // 0-based
        int day = Integer.parseInt(m.group(3));
        int hour = Integer.parseInt(m.group(4));
        int minute = Integer.parseInt(m.group(5));
        int second = Integer.parseInt(m.group(6));
        int millis = 0;
        String fraction = m.group(7);
        if (fraction != null) {
            if (fraction.length() > 3) {
                fraction = fraction.substring(0, 3);
            }
            while (fraction.length() < 3) {
                fraction += "0";
            }
            millis = Integer.parseInt(fraction);
        }

        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(year, month, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, millis);

        String tz = m.group(8);
        if (tz != null && !tz.equals("Z")) {
            char sign = tz.charAt(0);
            String tzOffset = tz.substring(1).replace(":", "");
            int tzHour = Integer.parseInt(tzOffset.substring(0, 2));
            int tzMin = tzOffset.length() > 2 ? Integer.parseInt(tzOffset.substring(2)) : 0;
            int offsetMs = (tzHour * 60 + tzMin) * 60 * 1000;
            if (sign == '+') {
                cal.add(Calendar.MILLISECOND, -offsetMs);
            } else {
                cal.add(Calendar.MILLISECOND, offsetMs);
            }
        }
        return cal;
    }
}
