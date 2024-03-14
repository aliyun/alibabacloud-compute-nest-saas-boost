import {ProColumns} from '@ant-design/pro-table';
import {ProForm, ProFormDigit, ProFormSelect, ProFormText} from "@ant-design/pro-form";
import React from "react";

export const commodityColumns = [
    {
        title: 'Commodity Code',
        dataIndex: 'commodityCode',
        key: 'commodityCode',
    },
    {
        title: 'Commodity Name',
        dataIndex: 'commodityName',
        key: 'commodityName',
    },
    {
        title: 'Service ID',
        dataIndex: 'serviceId',
        key: 'serviceId',
    }
]


export const specificationColumns: ProColumns<API.CommoditySpecificationDTO>[] = [
    {
        title: 'Specification Name',
        dataIndex: 'specificationName',
        key: 'specificationName',
        valueType: 'text',
        search: false

    },
    {
        title: 'Pay Period Unit',
        dataIndex: 'payPeriodUnit',
        key: 'payPeriodUnit',
        valueType: 'text',
        search: false
    },
    {
        title: 'Pay Periods',
        dataIndex: 'payPeriods',
        key: 'payPeriods',
        valueType: 'text', // 如果是数字可以选择 'digit' 或者根据实际的格式选择其他的 valueType
        search: false

    },

    {
        title: 'Unit Price',
        dataIndex: 'unitPrice',
        key: 'unitPrice',
        valueType: 'money', // 假设 unitPrice 是金额，可以使用 'money' 类型
        search: false
    },
    {
        title: 'Currency',
        dataIndex: 'currency',
        key: 'currency',
        valueType: 'text', // 根据数据的实际类型选择 valueType
        search: false,
    },
];

export interface SpecificationModalProps {
    commodity: API.CommodityDTO;
    visible: boolean;
    onClose: () => void;
}

interface SpecificationFormProps {
    initialValues?: API.CommoditySpecificationDTO | {};
    onSubmit: (values: API.CreateCommoditySpecificationParam | API.updateCommoditySpecificationParams) => void;
    visible: boolean;

}

export const SpecificationForm: React.FC<SpecificationFormProps> = ({
                                                                        initialValues,
                                                                        onSubmit,
                                                                        visible
                                                                    }) => (
    <ProForm<API.CreateCommoditySpecificationParam>
        onFinish={onSubmit}
        visible={visible}
        onFinishFailed={(errorInfo) => {
            console.log(errorInfo);
        }}
        initialValues={initialValues}

    >
        <ProFormText
            name="specificationName"
            label="Specification Name"
            rules={[{required: true, message: 'Please input specification name!'}]}
        />
        <ProFormDigit
            name="unitPrice"
            label="Unit Price"
            fieldProps={{precision: 2}}
            min={0}
            rules={[{required: true, message: 'Please input unit price!'}]}
        />
        <ProFormSelect
            name="payPeriodUnit"
            label="Pay Period Unit"
            options={[
                {label: 'Month', value: 'Month'},
                {label: 'Day', value: 'Day'},
                {label: 'Year', value: 'Year'}
            ]}
            rules={[{required: true, message: 'Please select pay period unit!'}]}
        />
        <ProFormSelect
            name="payPeriods"
            label="Pay Periods"
            mode="multiple"
            options={Array.from({length: 12}, (_, i) => ({label: (i + 1).toString(), value: i + 1}))}
            fieldProps={{
                optionFilterProp: "children", // 搜索时根据标签文字进行筛选
                filterOption: (input, option) => option?.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }}
            rules={[{required: true, message: 'Please select pay periods!'}]}
        />
        <ProFormSelect
            name="currency"
            label="Currency"
            hidden={true}
            initialValue={"CNY"}
            fieldProps={{
                defaultValue: "CNY"
            }}
            options={[
                {label: 'CNY', value: 'CNY'}
            ]}
        />
    </ProForm>
)
