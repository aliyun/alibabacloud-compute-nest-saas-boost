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

import java.time.Instant;
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

    public static final String TIME_FORMAT = "yyyyMMddHHmmss";

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

    public static Long parseFromSimpleDateFormat(String simpleDateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(simpleDateString, formatter);
        return getUtcEpochMillis(dateTime);
    }

    public static Long parseFromSimpleDateFormat(String simpleDateString, String timeZone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(simpleDateString, formatter);
        ZoneId zoneId = ZoneId.of(timeZone);
        return dateTime.atZone(zoneId).withZoneSameInstant(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static Long parseFromIsO8601DateString(String is08601DateString){
        if (is08601DateString == null || is08601DateString.length() == 0) {
            return null;
        }
        DateTimeFormatter formatter = getIsO8601DateFormat(ISO8601_DATE_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(is08601DateString, formatter);
        return dateTime.atZone(UTC_ZONE_ID).withZoneSameInstant(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static Long getCurrentLocalDateTimeMillis(){
        LocalDateTime currentDate = LocalDateTime.now();
        return getUtcEpochMillis(currentDate);
    }

    public static String getCurrentIs08601Time() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime utcDateTime = localDateTime.atZone(DEFAULT_ZONE_ID).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ISO8601_DATE_FORMAT);
        return formatter.format(utcDateTime);
    }

    public static Long getIsO8601FutureDateMillis(String is08601DateString, long days) {
        DateTimeFormatter formatter = getIsO8601DateFormat(ISO8601_DATE_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(is08601DateString, formatter);
        LocalDateTime futureDate = dateTime.plusDays(days);
        return futureDate.atZone(UTC_ZONE_ID).withZoneSameInstant(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static Long getIsO8601FutureDateMillis(long utcTimestamp, long days) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(utcTimestamp), UTC_ZONE_ID);
        LocalDateTime futureDate = dateTime.plusDays(days);
        return futureDate.atZone(UTC_ZONE_ID).withZoneSameInstant(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static String parseIs08601DateMillis(long utcTimestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(utcTimestamp), UTC_ZONE_ID);
        return dateTime.format(DateTimeFormatter.ofPattern(ISO8601_DATE_FORMAT));
    }

    public static String getCurrentTimePlusMinutes(int minutes) {
        LocalDateTime currentTime = LocalDateTime.now().plusMinutes(minutes);
        return currentTime.format(DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT));
    }

    public static Long getOneYearAgoLocalDateTimeMillis(){
        LocalDateTime currentDate = LocalDateTime.now();
        return getUtcEpochMillis(currentDate.minusYears(1));
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

    public static String simpleDateStringConvertToIso8601Format(String simpleDateString) {
        if (StringUtils.isEmpty(simpleDateString)) {
            return null;
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(SIMPLE_DATETIME_FORMAT);
        LocalDateTime dateTime = LocalDateTime.parse(simpleDateString, inputFormatter)
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

    public static String getCurrentTimeString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);
        return now.format(formatter);
    }

    public static Long getMinutesAgoLocalDateTimeMillis(int minutes) {
        LocalDateTime currentDate = LocalDateTime.now().minusMinutes(minutes);
        return getUtcEpochMillis(currentDate);
    }

    public static Long getUtcEpochMillis(LocalDateTime localDateTime) {
        return localDateTime.atZone(DEFAULT_ZONE_ID).withZoneSameInstant(UTC_ZONE_OFFSET)
                .toInstant().toEpochMilli();
    }
}
