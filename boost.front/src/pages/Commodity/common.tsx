import {ProColumns} from '@ant-design/pro-table';
import {ProForm, ProFormDigit, ProFormSelect, ProFormText} from "@ant-design/pro-form";
import React from "react";
import {message} from "antd";

export const commodityColumns: ProColumns<API.CommodityDTO>[] = [
    {
        title: '商品码',
        dataIndex: 'commodityCode',
        key: 'commodityCode',
        search: false,
    },
    {
        title: '商品名',
        dataIndex: 'commodityName',
        key: 'commodityName',
        search: false,
    },
    {
        title: '计算巢服务ID',
        dataIndex: 'serviceId',
        key: 'serviceId',
        search: false,
    },
    {
        title: '商品默认价格',
        dataIndex: 'unitPrice',
        key: 'unitPrice',
        search: false,
    }
];
export interface CommodityFormProps {
    commodity: API.CommodityDTO | undefined;
    onSubmit: (values: API.CommodityDTO) => Promise<void>;
}

export const CommodityForm: React.FC<CommodityFormProps> = ({ commodity, onSubmit }) => {
    return (
        <ProForm<API.CommodityDTO>
            onFinish={onSubmit}
            onFinishFailed={(errorInfo) => {
                console.log(errorInfo);
                message.error('Failed to submit commodity');
            }}
            initialValues={commodity || {}}
        >
            {commodity && (
                <ProFormText
                    name="commodityCode"
                    label="商品码"
                    disabled={true}
                    rules={[{ required: true, message: 'Please input commodity code!' }]}
                />
            )}
            <ProFormText
                name="commodityName"
                label="商品名"
                rules={[{required: true, message: 'Please input commodity name!'}]}
            />
            <ProFormText
                name="serviceId"
                label="计算巢服务ID"
                rules={[{required: true, message: 'Please input service ID!'}]}
            />
            <ProFormDigit
                name="unitPrice"
                label="默认价格"
                fieldProps={{precision: 2}}
                min={0}
                rules={[{required: true, message: 'Please input unit price!'}]}
            />
            {!commodity && (<ProFormSelect
                name="chargeType"
                label="付费方式"
                options={[
                    {label: '包年包月', value: "PRE_PAID"},
                ]}
                rules={[{required: true, message: 'Please select charge type!'}]}
            />     )}
        </ProForm>
    );
};


export const specificationColumns: ProColumns<API.CommoditySpecificationDTO>[] = [
    {
        title: '套餐名',
        dataIndex: 'specificationName',
        key: 'specificationName',
        valueType: 'text',
        search: false

    },
    {
        title: '支付周期单位',
        dataIndex: 'payPeriodUnit',
        key: 'payPeriodUnit',
        valueType: 'text',
        search: false
    },
    {
        title: '支持的售卖周期',
        dataIndex: 'payPeriods',
        key: 'payPeriods',
        valueType: 'text', // 如果是数字可以选择 'digit' 或者根据实际的格式选择其他的 valueType
        search: false

    },

    {
        title: '单价',
        dataIndex: 'unitPrice',
        key: 'unitPrice',
        valueType: 'money', // 假设 unitPrice 是金额，可以使用 'money' 类型
        search: false
    },
    {
        title: '币种',
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
    initialValues?: API.CommoditySpecificationDTO | undefined;
    onSubmit: (values: API.CreateCommoditySpecificationParam | API.UpdateCommoditySpecificationParam) => Promise<void>;

}

export const SpecificationForm: React.FC<SpecificationFormProps> = ({
                                                                        initialValues,
                                                                        onSubmit,
                                                                    }) => (
    <ProForm<API.CreateCommoditySpecificationParam>
        onFinish={onSubmit}
        onFinishFailed={(errorInfo) => {
            console.log(errorInfo);
        }}
        initialValues={initialValues}

    >
        {<ProFormText
            name="specificationName"
            label="Specification Name"
            rules={[{required: true, message: 'Please input specification name!'}]}
            disabled={initialValues != undefined && initialValues?.commodityCode != undefined}
        />}
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
                optionFilterProp: "children",
                filterOption: (input, option) => {
                    const childrenString = typeof option?.children === 'string' ? option.children : '';
                    return childrenString.toLowerCase().indexOf(input.toLowerCase()) >= 0;
                }
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
