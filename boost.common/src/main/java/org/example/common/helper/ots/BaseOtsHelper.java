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
import com.alicloud.openservices.tablestore.model.DeleteRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowDeleteChange;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.filter.ColumnValueFilter;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.MatchQuery;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import com.alicloud.openservices.tablestore.model.search.query.RangeQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermsQuery;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.common.ListResult;
import org.example.common.adapter.OtsClient;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.utils.EncryptionUtil;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.OtsUtil;
import org.example.common.utils.ReflectionUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BaseOtsHelper {

    private final OtsClient otsClient;

    private static final Integer DEFAULT_MAX_RESULTS = 10;

    private static final Integer RANGE_FILTER_SIZE = 2;

    public BaseOtsHelper(OtsClient otsClient) {
        this.otsClient = otsClient;
    }

    public <T> T getEntity(String tableName, PrimaryKey primaryKey, ColumnValueFilter filter, Class<T> clazz) {
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(tableName, primaryKey);
        if (filter != null) {
            criteria.setFilter(filter);
        }
        criteria.setMaxVersions(1);
        log.info("criteria:{}", JsonUtil.toJsonString(criteria));
        GetRowResponse getRowResponse = otsClient.getRow(criteria);
        Optional<Row> optionalRow = Optional.ofNullable(getRowResponse != null ? getRowResponse.getRow() : null);
        return optionalRow.map(object -> OtsUtil.convertRowToDTO(object, clazz))
                .orElseThrow(() -> new BizException(ErrorInfo.ENTITY_NOT_EXIST.getStatusCode(), ErrorInfo.ENTITY_NOT_EXIST.getCode(),
                        String.format(ErrorInfo.ENTITY_NOT_EXIST.getMessage(), tableName+primaryKey.toString())));
    }

    public void createEntity(String tableName, PrimaryKey primaryKey, List<Column> columns) {
        RowPutChange rowPutChange = new RowPutChange(tableName, primaryKey);
        rowPutChange.addColumns(columns);
        log.info("rowPutChange:{}", JsonUtil.toJsonString(rowPutChange));
        otsClient.putRow(rowPutChange);
    }

    public Boolean updateEntity(String tableName, PrimaryKey primaryKey, List<Column> columns) {
        RowUpdateChange rowUpdateChange = new RowUpdateChange(tableName, primaryKey);
        rowUpdateChange.put(columns);
        log.info("rowUpdateChange:{}", JsonUtil.toJsonString(rowUpdateChange));
        otsClient.updateRow(rowUpdateChange);
        return Boolean.TRUE;
    }

    public Boolean deleteEntity(String tableName, PrimaryKey primaryKey) {
        RowDeleteChange rowDeleteChange = new RowDeleteChange(tableName, primaryKey);
        DeleteRowRequest deleteRowRequest = new DeleteRowRequest(rowDeleteChange);
        log.info("deleteRowRequest:{}", JsonUtil.toJsonString(deleteRowRequest));
        otsClient.deletRow(deleteRowRequest);
        return Boolean.TRUE;
    }

    private Query createRangeQuery(OtsFilter filter) {
        if (filter.values == null || filter.values.size() != RANGE_FILTER_SIZE || filter.values.contains(null)) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE);
        }
        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName(filter.key);
        Object start = filter.values.get(0);
        Object end = filter.values.get(1);
        rangeQuery.greaterThanOrEqual(OtsUtil.createColumnValue(start));
        rangeQuery.lessThanOrEqual(OtsUtil.createColumnValue(end));
        return rangeQuery;
    }

    private SearchQuery createSearchQuery(List<OtsFilter> matchFilters, List<OtsFilter> queryFilters, List<OtsFilter> multiMatchFilters) {
        List<Query> queries = Optional.ofNullable(queryFilters)
                .map(filters -> filters.stream()
                        .map(this::createRangeQuery)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
        SearchQuery searchQuery = new SearchQuery();
        if (matchFilters != null && !matchFilters.isEmpty()) {
            matchFilters.forEach((filter) -> {
                MatchQuery matchQuery = new MatchQuery();
                matchQuery.setFieldName(filter.key);
                matchQuery.setText(String.valueOf(filter.values.get(0)));
                queries.add(matchQuery);
            });
        }

        if (multiMatchFilters != null && !multiMatchFilters.isEmpty()) {
            multiMatchFilters.forEach((filter) -> {
                TermsQuery termsQuery = new TermsQuery();
                termsQuery.setFieldName(filter.getKey());
                for (Object value : filter.values) {
                    termsQuery.addTerm(OtsUtil.createColumnValue(value));
                }
                queries.add(termsQuery);
            });
        }
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustQueries(queries);
        searchQuery.setQuery(boolQuery);
        searchQuery.setGetTotalCount(true);
        searchQuery.setLimit(DEFAULT_MAX_RESULTS);
        return searchQuery;
    }

    public <T> ListResult<T> listEntities(String tableName, String searchIndexName, List<OtsFilter> matchFilters,
                                             List<OtsFilter> queryFilters, List<OtsFilter> multiMatchFilter, String nextToken, List<Sort.Sorter> sorters, Class<T> clazz) {
        SearchQuery searchQuery = createSearchQuery(matchFilters, queryFilters, multiMatchFilter);
        SearchRequest searchRequest = new SearchRequest(tableName, searchIndexName, searchQuery);
        if (!StringUtils.isEmpty(nextToken)) {
            byte[] tokenBytes = EncryptionUtil.decode(nextToken);
            searchRequest.getSearchQuery().setToken(tokenBytes);
        } else {
            if (sorters != null &&!sorters.isEmpty()) {
                searchQuery.setSort(new Sort(sorters));
            }
        }
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        columnsToGet.setColumns(ReflectionUtil.getPropertyNames(clazz));
        searchRequest.setColumnsToGet(columnsToGet);
        log.info("searchRequest:{}", JsonUtil.toJsonString(searchRequest));
        SearchResponse searchResponse = otsClient.search(searchRequest);
        if (searchResponse == null || searchResponse.getRows() == null) {
            return ListResult.genSuccessListResult(null, 0);
        }
        byte[] nextTokenBytes = searchResponse.getNextToken();
        nextToken = nextTokenBytes == null || nextTokenBytes.length == 0 ? null : EncryptionUtil.encode(nextTokenBytes);
        List<T> result = searchResponse.getRows().stream()
                .map(row -> OtsUtil.convertRowToDTO(row, clazz))
                .collect(Collectors.toList());
        return ListResult.genSuccessListResult(result, searchResponse.getTotalCount(), nextToken);
    }

    @Data
    @Builder
    public static class OtsFilter {
        String key;
        List<Object> values;

        public static OtsFilter createMatchFilter(String key, Object value) {
            return OtsFilter.builder().key(key).values(Collections.singletonList(value)).build();
        }

        public static OtsFilter createRangeFilter(String key, Object fromValue, Object toValue) {
            return OtsFilter.builder().key(key).values(Arrays.asList(fromValue, toValue)).build();
        }

        public static OtsFilter createTermsFilter(String key, List<Object> values) {
            return OtsFilter.builder().key(key).values(values).build();
        }
    }
}
