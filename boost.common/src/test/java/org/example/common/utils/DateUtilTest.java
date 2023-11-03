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

import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilTest {

    private Clock clock = Clock.fixed(Instant.parse("2014-12-21T10:15:30.00Z"), ZoneId.of("Asia/Shanghai"));

    @BeforeEach
    public void setClock() {
        mockCurrentDateTime(clock);
    }

    @Test
    void testParseFromSimpleDateFormat() {
        assertThat(DateUtil.parseFromSimpleDateFormat("2020-09-09 09:09:09")).isEqualTo(DateUtil.parseFromIsO8601DateString("2020-09-09T01:09:09Z"));
        Assertions.assertDoesNotThrow(() -> DateUtil.parseFromIsO8601DateString(""));
    }

    @Test
    void testParseFromSimpleDateFormatInShangHai() {
        assertThat(DateUtil.parseFromSimpleDateFormat("2020-09-09 09:09:09", "Asia/Shanghai")).isEqualTo(DateUtil.parseFromIsO8601DateString("2020-09-09T01:09:09Z"));
    }

    @Test
    void testGetCurrentLocalDateTimeMills() {
        String dateTimeExpected = "2014-12-21T10:15:30Z";
        Long expectedCurrentTimeMills = DateUtil.parseFromIsO8601DateString(dateTimeExpected);
        Long realCurrentTimeMills = DateUtil.getCurrentLocalDateTimeMills();
        assertThat(realCurrentTimeMills).isEqualTo(expectedCurrentTimeMills);
    }

    @Test
    void testGetCurrentTimePlusMinutes() {
        String dateTimeExpected = "2014-12-21 18:16:30";
        String currentTimePlusMinutes = DateUtil.getCurrentTimePlusMinutes(1);
        assertThat(currentTimePlusMinutes).isEqualTo(dateTimeExpected);
    }

    @Test
    void testGetCurrentIs08601Time() {
        String dateTimeExpected = "2014-12-21T10:15:30Z";
        String currentIs08601Time = DateUtil.getCurrentIs08601Time();
        assertThat(dateTimeExpected).isEqualTo(currentIs08601Time);
    }

    @Test
    void testGetOneYearAgoLocalDateTimeMills() {
        String dateTimeExpected = "2013-12-21T10:15:30Z";
        Long expectedTimeStamp = DateUtil.parseFromIsO8601DateString(dateTimeExpected);
        Long oneYearAgoLocalDateTimeMills = DateUtil.getOneYearAgoLocalDateTimeMills();
        assertThat(expectedTimeStamp).isEqualTo(oneYearAgoLocalDateTimeMills);
    }

    @Test
    void testConvertToIso8601Format() {
        assertThat(DateUtil.convertToIso8601Format("2020-09-09 09:09:09")).isEqualTo("2020-09-09T01:09:09Z");
        assertThat(DateUtil.convertToIso8601Format("")).isNotEqualTo("2020-09-09T01:09:09Z");
    }

    @Test
    void testIsValidSimpleDateTimeFormat() {
        assertThat(DateUtil.isValidSimpleDateTimeFormat("datetime")).isFalse();
    }

    @Test
    void testGetMinutesAgoLocalDateTimeMillis() {
        String dateTimeExpected = "2014-12-21T10:14:30Z";
        Long realMinutesAgoTime = DateUtil.parseFromIsO8601DateString(dateTimeExpected);
        Long minutesAgoLocalDateTimeMillis = DateUtil.getMinutesAgoLocalDateTimeMillis(1);
        assertThat(minutesAgoLocalDateTimeMillis).isEqualTo(realMinutesAgoTime);
    }

    @Test
    void testIsValidIsO8601DateFormat() {
        String datetime1 = "";
        boolean result1 = DateUtil.isValidIsO8601DateFormat(datetime1);
        Assertions.assertFalse(result1);

        String datetime2 = "2022-01-01T12:00:00Z";
        boolean result2 = DateUtil.isValidIsO8601DateFormat(datetime2);
        Assertions.assertTrue(result2);

        String datetime3 = "2022-01-01 12:00:00";
        boolean result3 = DateUtil.isValidIsO8601DateFormat(datetime3);
        Assertions.assertFalse(result3);
    }

    @Test
    void testGetCurrentTimestamp() {
        String dateTimeExpected = "20141221181530000";
        String realTimeStamp = DateUtil.getCurrentTimestamp();
        assertThat(dateTimeExpected).isEqualTo(realTimeStamp);
    }

    private void mockCurrentDateTime(Clock clock) {
        new MockUp<LocalDateTime>() {
            @Mock
            public LocalDateTime now() {
                return LocalDateTime.now(clock);
            }
        };
    }

    @Test
    void testGetFutureDateTime() {
        String dateTimeExpected = "2014-12-22T10:15:30Z";
        Long realMinutesAgoTime = DateUtil.parseFromIsO8601DateString(dateTimeExpected);
        Long minutesAgoLocalDateTimeMillis = DateUtil.getIsO8601FutureDateMills("2014-12-21T10:15:30Z",1);
        assertThat(minutesAgoLocalDateTimeMillis).isEqualTo(realMinutesAgoTime);
    }
}
