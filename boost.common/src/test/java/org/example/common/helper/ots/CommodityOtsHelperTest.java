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

package org.example.common.helper.ots;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import org.example.common.ListResult;
import org.example.common.constant.CommodityOtsConstant;
import org.example.common.dataobject.CommodityDO;
import org.example.common.dto.CommodityDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.common.constant.CommodityOtsConstant.TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class CommodityOtsHelperTest {

    @Mock
    private BaseOtsHelper mockBaseOtsHelper;

    @InjectMocks
    private CommodityOtsHelper commodityOtsHelper;

    private String commodityCode = "COMMODITY1";

    private CommodityDO commodityDO;

    private CommodityDTO commodityDTO;

    @BeforeEach
    void setUp() {
        initMocks(this);
        commodityDO = new CommodityDO();
        commodityDO.setCommodityCode(commodityCode);
        commodityDTO = new CommodityDTO();
        commodityDTO.setCommodityCode(commodityCode);
    }

    @Test
    void testUpdateCommodity() {
        when(mockBaseOtsHelper.updateEntity(anyString(), any(PrimaryKey.class), anyList())).thenReturn(Boolean.TRUE);
        Boolean result = commodityOtsHelper.updateCommodity(commodityDO);
        verify(mockBaseOtsHelper).updateEntity(anyString(), any(PrimaryKey.class), anyList());
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testGetCommodity() {
        when(mockBaseOtsHelper.getEntity(anyString(), any(PrimaryKey.class), any(), any())).thenReturn(commodityDTO);
        CommodityDTO result = commodityOtsHelper.getCommodity(commodityCode);
        assertEquals(commodityDTO.getCommodityCode(), result.getCommodityCode());
    }

    @Test
    void testDeleteCommodity() {
        when(mockBaseOtsHelper.deleteEntity(anyString(), any(PrimaryKey.class))).thenReturn(Boolean.TRUE);
        Boolean result = commodityOtsHelper.deleteCommodity(commodityCode);
        verify(mockBaseOtsHelper).deleteEntity(anyString(), any(PrimaryKey.class));
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testCreateCommodity() {
        CommodityDO commodityDO = new CommodityDO();
        commodityDO.setCommodityCode("testCode");
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("TestColumn", ColumnValue.fromString("TestValue")));

        CommodityDTO createdCommodityDTO = commodityOtsHelper.createCommodity(commodityDO);

        verify(mockBaseOtsHelper).createEntity(eq(TABLE_NAME), any(), any());
        assertNotNull(createdCommodityDTO);
    }

    @Test
    void testListCommodities() {
        String nextToken = "nextToken";
        List<BaseOtsHelper.OtsFilter> filters = Collections.singletonList(new BaseOtsHelper.OtsFilter("key", Collections.singletonList("value")));
        ListResult<CommodityDTO> expectedResult = new ListResult<>();
        expectedResult.setData(Collections.emptyList());
        expectedResult.setCount(0L);
        expectedResult.setNextToken(nextToken);

        when(mockBaseOtsHelper.listEntities(eq(TABLE_NAME), eq(CommodityOtsConstant.SEARCH_INDEX_NAME), eq(filters), any(), any(), eq(nextToken), isNull(), eq(CommodityDTO.class)))
                .thenReturn(expectedResult);

        ListResult<CommodityDTO> result = commodityOtsHelper.listCommodities(nextToken, filters, null);

        assertEquals(expectedResult, result);
        assertNotNull(result);
        assertEquals(0L, result.getCount());
    }
}
