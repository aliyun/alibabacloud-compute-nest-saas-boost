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

package org.example.common;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ListResult.class)
public class ListResultTest {

    @Mock
    private List<String> mockData;

    @Test
    public void testGetData() {
        ListResult<String> listResult = new ListResult<>();
        listResult.setData(mockData);

        Assert.assertEquals(mockData, listResult.getData());
    }

    @Test
    public void testGetCount() {
        ListResult<String> listResult = new ListResult<>();
        listResult.setCount(10L);

        Assert.assertEquals(10L, listResult.getCount().longValue());
    }

    @Test
    public void testGetNextToken() {
        ListResult<String> listResult = new ListResult<>();
        listResult.setNextToken("token");

        Assert.assertEquals("token", listResult.getNextToken());
    }

    @Test
    public void testGenSuccessListResult() {
        List<String> testData = new ArrayList<>();
        testData.add("data1");
        testData.add("data2");

        ListResult<String> listResult = ListResult.genSuccessListResult(testData, 2);

        Assert.assertEquals(testData, listResult.getData());
        Assert.assertEquals(2L, listResult.getCount().longValue());
    }

    @Test
    public void testGenSuccessListResultWithNextToken() {
        List<String> testData = new ArrayList<>();
        testData.add("data1");
        testData.add("data2");

        ListResult<String> listResult = ListResult.genSuccessListResult(testData, 2, "token");

        Assert.assertEquals(testData, listResult.getData());
        Assert.assertEquals(2L, listResult.getCount().longValue());
        Assert.assertEquals("token", listResult.getNextToken());
    }
}
