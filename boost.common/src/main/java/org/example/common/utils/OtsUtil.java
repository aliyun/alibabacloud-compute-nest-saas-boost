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

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;
import com.alicloud.openservices.tablestore.model.Row;
import lombok.extern.slf4j.Slf4j;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.constant.PaymentType;
import org.example.common.constant.ProductName;
import org.example.common.constant.TradeStatus;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class OtsUtil {

    private static final Map<Class<?>, Function<Object, ColumnValue>> COLUMN_VALUE_CONVERTERS = new HashMap<>();

    private static final Map<Class<?>, Function<String, Object>> CLASS_TO_FUNCTION_CONVERTERS = new HashMap<>();

    static {
        CLASS_TO_FUNCTION_CONVERTERS.put(String.class, str -> str);
        CLASS_TO_FUNCTION_CONVERTERS.put(Integer.class, Integer::parseInt);
        CLASS_TO_FUNCTION_CONVERTERS.put(int.class, Integer::parseInt);
        CLASS_TO_FUNCTION_CONVERTERS.put(Long.class, Long::parseLong);
        CLASS_TO_FUNCTION_CONVERTERS.put(long.class, Long::parseLong);
        CLASS_TO_FUNCTION_CONVERTERS.put(Double.class, Double::parseDouble);
        CLASS_TO_FUNCTION_CONVERTERS.put(double.class, Double::parseDouble);
        CLASS_TO_FUNCTION_CONVERTERS.put(Boolean.class, Boolean::parseBoolean);
        CLASS_TO_FUNCTION_CONVERTERS.put(boolean.class, Boolean::parseBoolean);
        CLASS_TO_FUNCTION_CONVERTERS.put(Float.class, Float::parseFloat);
        CLASS_TO_FUNCTION_CONVERTERS.put(float.class, Float::parseFloat);
        CLASS_TO_FUNCTION_CONVERTERS.put(PaymentType.class, PaymentType::valueOf);
        CLASS_TO_FUNCTION_CONVERTERS.put(TradeStatus.class, TradeStatus::valueOf);
        CLASS_TO_FUNCTION_CONVERTERS.put(ProductName.class, ProductName::valueOf);
        CLASS_TO_FUNCTION_CONVERTERS.put(PayPeriodUnit.class, PayPeriodUnit::valueOf);
    }

    static {
        COLUMN_VALUE_CONVERTERS.put(Integer.class, fieldValue -> ColumnValue.fromLong((Integer) fieldValue));
        COLUMN_VALUE_CONVERTERS.put(Long.class, fieldValue -> ColumnValue.fromLong((Long)fieldValue));
        COLUMN_VALUE_CONVERTERS.put(String.class, fieldValue -> ColumnValue.fromString((String) fieldValue));
        COLUMN_VALUE_CONVERTERS.put(Float.class, fieldValue -> ColumnValue.fromDouble((Float) fieldValue));
        COLUMN_VALUE_CONVERTERS.put(Double.class, fieldValue -> ColumnValue.fromDouble((Double) fieldValue));
        COLUMN_VALUE_CONVERTERS.put(Boolean.class, fieldValue -> ColumnValue.fromBoolean((Boolean) fieldValue));
        COLUMN_VALUE_CONVERTERS.put(PaymentType.class, fieldValue -> ColumnValue.fromString(((PaymentType)fieldValue).name()));
        COLUMN_VALUE_CONVERTERS.put(TradeStatus.class, fieldValue -> ColumnValue.fromString(((TradeStatus)fieldValue).name()));
        COLUMN_VALUE_CONVERTERS.put(ProductName.class, fieldValue -> ColumnValue.fromString(((ProductName)fieldValue).name()));
        COLUMN_VALUE_CONVERTERS.put(PayPeriodUnit.class, fieldValue -> ColumnValue.fromString(((PayPeriodUnit)fieldValue).name()));
        // 添加其他类型的映射关系
    }

    public static List<Column> convertToColumnList(Object parameters) {
        if (parameters == null) {
            return null;
        }
        List<Column> columnList = new ArrayList<>();
        Field[] fields = ReflectionUtil.getAllFields(parameters.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Object fieldValue = field.get(parameters);
                Column column = createColumn(fieldName, fieldValue);
                if (column != null) {
                    columnList.add(column);
                }
            } catch (IllegalAccessException e) {
                log.error("Column convert failed.", e);
            }
        }

        return columnList;
    }

    private static Column createColumn(String fieldName, Object fieldValue) {
        if (fieldValue == null) {
            return null;
        }
        ColumnValue columnValue = createColumnValue(fieldValue);
        if (columnValue == null) {
            throw new BizException(ErrorInfo.COLUMN_VALUE_IS_NULL);
        }
        return new Column(fieldName, columnValue);
    }

    public static ColumnValue createColumnValue(Object fieldValue) {
        Function<Object, ColumnValue> valueCreator = COLUMN_VALUE_CONVERTERS.get(fieldValue.getClass());
        if (valueCreator == null) {
            return null;
        }
        return valueCreator.apply(fieldValue);
    }

    public static <T> T convertRowToDTO(Row row, Class<T> dtoClass) {
        T dto = null;
        try {
            dto = dtoClass.getDeclaredConstructor().newInstance();
            for (PrimaryKeyColumn primaryKeyColumn : row.getPrimaryKey().getPrimaryKeyColumns()) {
                setValue(dto, primaryKeyColumn.getName(), primaryKeyColumn.getValue());
            }
            for (Column column : row.getColumns()) {
                setValue(dto, column.getName(), column.getValue());
            }
        } catch (Exception e) {
            log.error("Row to Dto failed.", e);
        }
        return dto;
    }

    private static <T> void setValue(T dto, String columnName, Object columnValue) {
        try {
            Field field = ReflectionUtil.getFieldByColumnName(dto.getClass(), columnName);
            if (field != null) {
                Method method = ReflectionUtil.findMethod(dto.getClass(), "set" + capitalize(field.getName()), field.getType());
                if (method != null) {
                    Object convertedValue = convertValue(columnValue, field.getType());
                    method.invoke(dto, convertedValue);
                }
            }
        } catch (Exception e) {
            log.error("Set Column value failed.", e);
        }
    }

    private static Object convertValue(Object value, Class<?> targetType) {
        Function<String, Object> converter = CLASS_TO_FUNCTION_CONVERTERS.get(targetType);
        if (converter != null) {
            return converter.apply(value.toString());
        }
        return value;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
