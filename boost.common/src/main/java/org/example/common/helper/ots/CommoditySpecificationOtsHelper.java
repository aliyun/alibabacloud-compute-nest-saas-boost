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
import org.example.common.constant.CommoditySpecificationOtsConstant;
import org.example.common.dataobject.CommoditySpecificationDO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.utils.EncryptionUtil;
import org.example.common.utils.OtsUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class CommoditySpecificationOtsHelper {

    @Resource
    private BaseOtsHelper baseOtsHelper;

    public void createCommoditySpecification(CommoditySpecificationDO specification) {
        PrimaryKey primaryKey = createPrimaryKey(specification.getCommodityCode(), specification.getSpecificationName());
        List<Column> columns = OtsUtil.convertToColumnList(specification);
        baseOtsHelper.createEntity(CommoditySpecificationOtsConstant.TABLE_NAME, primaryKey, columns);
    }

    public Boolean updateCommoditySpecification(CommoditySpecificationDO specificationDO) {
        PrimaryKey primaryKey = createPrimaryKey(specificationDO.getCommodityCode(), specificationDO.getSpecificationName());
        List<Column> columns = OtsUtil.convertToColumnList(specificationDO);
        return baseOtsHelper.updateEntity(CommoditySpecificationOtsConstant.TABLE_NAME, primaryKey, columns);
    }

    public ListResult<CommoditySpecificationDTO> listCommoditySpecifications(String nextToken, List<BaseOtsHelper.OtsFilter> matchFilters, List<Sort.Sorter> sorters) {
        return baseOtsHelper.listEntities(CommoditySpecificationOtsConstant.TABLE_NAME, CommoditySpecificationOtsConstant.SEARCH_INDEX_NAME,
                matchFilters, null, null, nextToken, sorters, CommoditySpecificationDTO.class);
    }

    public CommoditySpecificationDTO getCommoditySpecification(String commodityCode, String specificationName) {
        PrimaryKey primaryKey = createPrimaryKey(commodityCode, specificationName);
        return baseOtsHelper.getEntity(CommoditySpecificationOtsConstant.TABLE_NAME, primaryKey, null, CommoditySpecificationDTO.class);
    }

    public Boolean deleteCommoditySpecification(String commodityCode, String specificationName) {
        PrimaryKey primaryKey = createPrimaryKey(commodityCode, specificationName);
        return baseOtsHelper.deleteEntity(CommoditySpecificationOtsConstant.TABLE_NAME, primaryKey);
    }

    private PrimaryKey createPrimaryKey(String commodityCode, String specificationName) {
        String commodityCodeMd5 = EncryptionUtil.getMd5HexString(commodityCode);
        String specificationNameMd5 = EncryptionUtil.getMd5HexString(specificationName);
        if (StringUtils.isEmpty(commodityCodeMd5) || StringUtils.isEmpty(specificationNameMd5)) {
            return null;
        }
        return PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(CommoditySpecificationOtsConstant.PRIMARY_KEY_NAME_PID, PrimaryKeyValue.fromString(specificationNameMd5))
                .addPrimaryKeyColumn(CommoditySpecificationOtsConstant.PRIMARY_KEY_NAME_COMMODITY_ID, PrimaryKeyValue.fromString(commodityCodeMd5))
                .build();
    }
}

