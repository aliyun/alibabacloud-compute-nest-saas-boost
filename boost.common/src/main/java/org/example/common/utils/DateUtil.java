/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

package org.example.common.utils;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class DateUtil {

    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final String SIMPLE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmssSSS";

    private static final Pattern ISO8601_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$");

    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})\\s(\\d{2}):(\\d{2}):(\\d{2})$");

    private static final ZoneOffset UTC_ZONE_OFFSET = ZoneOffset.UTC;

    /**
     * Mapping of Time Formatting Patterns to Formatters
     */
    private static final ConcurrentHashMap<String, DateTimeFormatter> DATE_TIME_FORMATTER_STYLE_MAP = new ConcurrentHashMap<>();

    /**
     * Default Time Zone: Shanghai, China
     */
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    public static Long parseFromSimpleDateFormat(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return getUtcEpochMills(dateTime);
    }

    public static Long parseFromSimpleDateFormat(String dateString, String timeZone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        ZoneId zoneId = ZoneId.of(timeZone);
        return dateTime.atZone(zoneId).withZoneSameInstant(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static Long parseFromIsO8601DateString(String dateString){
        if (dateString == null || dateString.length() == 0) {
            return null;
        }
        DateTimeFormatter formatter = getIsO8601DateFormat(ISO8601_DATE_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        return dateTime.atZone(UTC_ZONE_ID).withZoneSameInstant(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static Long getCurrentLocalDateTimeMills(){
        LocalDateTime currentDate = LocalDateTime.now();
        return getUtcEpochMills(currentDate);
    }

    public static String getCurrentIs08601Time() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime utcDateTime = localDateTime.atZone(DEFAULT_ZONE_ID).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ISO8601_DATE_FORMAT);
        return formatter.format(utcDateTime);
    }

    public static Long getIsO8601FutureDateMills(String dateString, long days) {
        DateTimeFormatter formatter = getIsO8601DateFormat(ISO8601_DATE_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        LocalDateTime futureDate = dateTime.plusDays(days);
        return futureDate.atZone(UTC_ZONE_ID).withZoneSameInstant(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static String getCurrentTimePlusMinutes(int minutes) {
        LocalDateTime currentTime = LocalDateTime.now().plusMinutes(minutes);
        return currentTime.format(DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT));
    }

    public static Long getOneYearAgoLocalDateTimeMills(){
        LocalDateTime currentDate = LocalDateTime.now();
        return getUtcEpochMills(currentDate.minusYears(1));
    }

    private static DateTimeFormatter getIsO8601DateFormat(String pattern) {
        DateTimeFormatter formatter = DATE_TIME_FORMATTER_STYLE_MAP.get(pattern);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(pattern, Locale.US)
                    .withZone(DEFAULT_ZONE_ID);
            DATE_TIME_FORMATTER_STYLE_MAP.put(pattern, formatter);
        }
        return formatter;
    }

    public static String convertToIso8601Format(String dateString) {
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(dateString, inputFormatter)
                .atZone(DEFAULT_ZONE_ID)
                .toOffsetDateTime()
                .withOffsetSameInstant(UTC_ZONE_OFFSET)
                .toLocalDateTime();

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(ISO8601_DATE_FORMAT);
        return dateTime.format(outputFormatter);
    }

    public static boolean isValidSimpleDateTimeFormat(String datetime) {
        if (StringUtils.isEmpty(datetime)) {
            return false;
        }
        return DATE_TIME_PATTERN.matcher(datetime).matches();
    }

    public static boolean isValidIsO8601DateFormat(String datetime) {
        if (StringUtils.isEmpty(datetime)) {
            return false;
        }
        return ISO8601_PATTERN.matcher(datetime).matches();
    }

    public static String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);
        return now.format(formatter);
    }

    public static Long getMinutesAgoLocalDateTimeMillis(int minutes) {
        LocalDateTime currentDate = LocalDateTime.now().minusMinutes(minutes);
        return getUtcEpochMills(currentDate);
    }

    public static Long getUtcEpochMills(LocalDateTime localDateTime) {
        return localDateTime.atZone(DEFAULT_ZONE_ID).withZoneSameInstant(UTC_ZONE_OFFSET)
                .toInstant().toEpochMilli();
    }
}
