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

package org.example.common.adapter.impl;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.CapacityDataSize;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.ConsumedCapacity;
import com.alicloud.openservices.tablestore.model.GetRangeRequest;
import com.alicloud.openservices.tablestore.model.GetRangeResponse;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.PutRowResponse;
import com.alicloud.openservices.tablestore.model.RangeRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.Response;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.UpdateRowRequest;
import com.alicloud.openservices.tablestore.model.UpdateRowResponse;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.example.common.config.AliyunConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.MockitoAnnotations.initMocks;

class OtsClientImplTest {

    @Injectable
    private AliyunConfig mockAliyunConfig;

    @Injectable
    private SyncClient syncClient;

    @Tested
    private OtsClientImpl otsClientImplUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        otsClientImplUnderTest = new OtsClientImpl();
    }

    @Test
    void testPutRow() {
        RowPutChange rowPutChange = new RowPutChange("tableName");
        PutRowResponse response = new PutRowResponse(new Response(), new Row(new PrimaryKey(), Arrays.asList(new Column("test", ColumnValue.fromString("a")))), new ConsumedCapacity(new CapacityDataSize()));
        new Expectations(){{
            syncClient.putRow(withAny(new PutRowRequest()));
            result = response;
        }};
        otsClientImplUnderTest.putRow(rowPutChange);
        new Verifications(){{
            times = 1;
        }};
    }

    @Test
    void testUpdateRow() {
        RowUpdateChange rowUpdateChange = new RowUpdateChange("tableName");
        UpdateRowResponse response = new UpdateRowResponse(new Response(), new Row(new PrimaryKey(), Arrays.asList(new Column("test", ColumnValue.fromString("a")))), new ConsumedCapacity(new CapacityDataSize()));
        new Expectations(){{
            syncClient.updateRow(withAny(new UpdateRowRequest()));
            result = response;
        }};
        otsClientImplUnderTest.updateRow(rowUpdateChange);
        new Verifications(){{
            times = 1;
        }};
    }

    @Test
    void testGetRow() {
        GetRowRequest getRowRequest = new GetRowRequest(new SingleRowQueryCriteria("tableName"));
        GetRowResponse response = new GetRowResponse(new Response(), new Row(new PrimaryKey(), Arrays.asList(new Column("test", ColumnValue.fromString("a")))), new ConsumedCapacity(new CapacityDataSize()));
        new Expectations(){{
            syncClient.getRow((withAny(getRowRequest)));
            result = response;
        }};
        otsClientImplUnderTest.getRow(new SingleRowQueryCriteria("tableName"));
        new Verifications(){{
            times = 1;
        }};
    }

    @Test
    void testGetRange() {
        GetRangeResponse response = new GetRangeResponse(new Response(), new ConsumedCapacity(new CapacityDataSize()));
        new Expectations(){{
            syncClient.getRange((withAny(new GetRangeRequest())));
            result = response;
        }};
        otsClientImplUnderTest.getRange(new RangeRowQueryCriteria("tableName"));
        new Verifications(){{
            times = 1;
        }};
    }

    @Test
    void testSearch() {
        SearchResponse response = new SearchResponse(new Response());
        new Expectations(){{
            syncClient.search((withAny(new SearchRequest())));
            result = response;
        }};
        SearchRequest request = SearchRequest.newBuilder().build();
        otsClientImplUnderTest.search(request);
        new Verifications(){{
            times = 1;
        }};
    }
}
