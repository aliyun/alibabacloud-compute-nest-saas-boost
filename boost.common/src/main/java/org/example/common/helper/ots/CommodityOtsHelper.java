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
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.ListResult;
import org.example.common.constant.CommodityOtsConstant;
import org.example.common.dataobject.CommodityDO;
import org.example.common.dto.CommodityDTO;
import org.example.common.utils.EncryptionUtil;
import org.example.common.utils.OtsUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static org.example.common.constant.CommodityOtsConstant.TABLE_NAME;

@Slf4j
@Component
public class CommodityOtsHelper {

    @Resource
    private BaseOtsHelper baseOtsHelper;

    public CommodityDTO createCommodity(CommodityDO commodity) {
        PrimaryKey primaryKey = createPrimaryKey(commodity.getCommodityCode());
        List<Column> columns = OtsUtil.convertToColumnList(commodity);
        baseOtsHelper.createEntity(TABLE_NAME, primaryKey, columns);
        CommodityDTO commodityDTO = new CommodityDTO();
        BeanUtils.copyProperties(commodity, commodityDTO);
        return commodityDTO;
    }

    public Boolean updateCommodity(CommodityDO commodityDO) {
        PrimaryKey primaryKey = createPrimaryKey(commodityDO.getCommodityCode());
        List<Column> columns = OtsUtil.convertToColumnList(commodityDO);
        return baseOtsHelper.updateEntity(TABLE_NAME, primaryKey, columns);
    }

    public ListResult<CommodityDTO> listCommodities(String nextToken, List<BaseOtsHelper.OtsFilter> filters, List<Sort.Sorter> sorters) {
        return baseOtsHelper.listEntities(TABLE_NAME, CommodityOtsConstant.SEARCH_INDEX_NAME, filters, null, null, nextToken, sorters, CommodityDTO.class);
    }

    public CommodityDTO getCommodity(String commodityCode) {
        PrimaryKey primaryKey = createPrimaryKey(commodityCode);
        return baseOtsHelper.getEntity(TABLE_NAME, primaryKey, null, CommodityDTO.class);
    }

    public Boolean deleteCommodity(String commodityCode) {
        PrimaryKey primaryKey = createPrimaryKey(commodityCode);
        return baseOtsHelper.deleteEntity(TABLE_NAME, primaryKey);
    }

    private PrimaryKey createPrimaryKey(String commodityCode) {
        String commodityCodeMd5 = EncryptionUtil.getMd5HexString(commodityCode);
        if (StringUtils.isEmpty(commodityCodeMd5)) {
            return null;
        }
        return PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(CommodityOtsConstant.PRIMARY_KEY_NAME, PrimaryKeyValue.fromString(commodityCodeMd5))
                .build();
    }
}

