import {ProColumns} from '@ant-design/pro-table';
import {ProForm, ProFormDigit, ProFormSelect, ProFormText} from "@ant-design/pro-form";
import React, {ReactNode, useState} from "react";
import {Button, Form, message, Space} from "antd";
import {CopyOutlined} from "@ant-design/icons";
import copy from 'copy-to-clipboard';
import {getServiceMetadata, listServices} from "@/services/backend/serviceManager";
import {listAllSpecifications} from "@/services/backend/specification";

export const commodityColumns: ProColumns<API.CommodityDTO>[] = [
    {
        title: '商品码',
        dataIndex: 'commodityCode',
        key: 'commodityCode',
        search: false,
        render: (dom: ReactNode, record: API.CommodityDTO, index: number) => {
            const text = dom as string;
            return (
                <>
                    {text}
                    <Button
                        type="text"
                        size="small"
                        icon={<CopyOutlined/>}
                        onClick={() => {
                            copy(text);
                            message.success('商品码已复制到剪贴板');
                        }}
                    />
                </>
            );
        }
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
    },
    {
        title: '商品描述',
        dataIndex: 'description',
        key: 'description',
        search: false,
        hideInTable: true
    },
    {
        title: '状态',
        dataIndex: 'commodityStatus',
        key: 'commodityStatus',
        search: false,
        hideInTable: true
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
        valueType: 'text',
        search: false

    },
    {
        title: '服务版本',
        dataIndex: 'serviceVersion',
        key: 'serviceVersion',
        search: false
    },
];

export interface CommodityFormProps {
    commodity: API.CommodityDTO | undefined;
    onSubmit: (values: API.CommodityDTO) => Promise<void>;

    onCancel: () => void;
}

export const CommodityForm: React.FC<CommodityFormProps> = ({commodity, onSubmit, onCancel}) => {
    const [payPeriodUnit, setPayPeriodUnit] = useState(commodity?.payPeriodUnit);
    const [form] = Form.useForm(); // 引入 useForm 钩子

    // 根据单位购买周期动态生成允许购买的周期的选项
    const getPayPeriodOptions = (unit) => {
        if (unit === 'Month') {
            return Array.from({length: 12}, (_, i) => ({label: (i + 1).toString(), value: i + 1}));
        } else if (unit === 'Day') {
            return Array.from({length: 30}, (_, i) => ({label: (i + 1).toString(), value: i + 1}));
        } else if (unit === 'Year') {
            return Array.from({length: 2}, (_, i) => ({label: (i + 1).toString(), value: i + 1}));
        }
        return [];
    };

    return (
        <ProForm<API.CommodityDTO>
            form={form} // 将表单实例传给 ProForm
            onFinish={onSubmit}
            onFinishFailed={(errorInfo) => {
                console.log(errorInfo);
                message.error('Failed to submit commodity');
            }}
            submitter={{
                render: (_, dom) => (
                    <>
                        <Space size={"small"}>
                            <Button
                                onClick={() => {
                                    onCancel();
                                }}
                            >
                                取消
                            </Button>
                            {dom[1]}
                        </Space>

                    </>
                ),
            }}
            initialValues={commodity || {}}
        >
            {commodity?.commodityCode && (
                <ProFormText
                    name="commodityCode"
                    label="商品码"
                    disabled={true}
                    rules={[{required: true, message: 'Please input commodity code!'}]}

                />
            )}
            <ProFormText
                name="commodityName"
                label="商品名"
                rules={[{required: true, message: 'Please input commodity name!'}]}
            />
            <ProFormSelect
                name="serviceId"
                label="计算巢服务ID"
                showSearch={true}
                dependencies={["serviceId"]}
                request={async (commodity) => {
                    try {
                        const response:API.ServiceVersionModel[] = await listServices({
                            serviceId:commodity?.serviceId
                        });
                        if (response && response.length !== 0) {
                            return response.map(item => ({
                                label: item.serviceName+"-"+item.serviceId,
                                value: item.serviceId
                            }));
                        }
                    } catch (e) {
                    }
                    return [];
                }
                }
                rules={[{required: true, message: 'Please input service ID!'},
                    () => ({
                        validator(_, value) {
                            if (!value || value.startsWith("service-")) {
                                return Promise.resolve();
                            }
                            return Promise.reject(new Error('服务ID必须以 "service-"开始'));
                        },
                    }),]}
            />
            <ProFormDigit
                name="unitPrice"
                label="默认价格"
                fieldProps={{precision: 2}}
                min={0}
                rules={[{required: true, message: 'Please input unit price!'}]}
            />
            <ProFormText
                name="description"
                label="描述"
                rules={[{required: true, message: 'Please input service ID!'}]}
            />
            <ProFormSelect
                name="payPeriodUnit"
                label="单位购买周期"
                options={[
                    {label: '月', value: 'Month'},
                    {label: '日', value: 'Day'},
                    {label: '年', value: 'Year'}
                ]}
                rules={[{required: true, message: 'Please select pay period unit!'}]}
                fieldProps={{
                    onChange: (value) => {
                        if (value !== payPeriodUnit) {
                            setPayPeriodUnit(value as string);
                            form.setFieldsValue({ payPeriods: undefined });
                        }
                    }
                }}
            />
            <ProFormSelect
                name="payPeriods"
                label="允许购买的周期"
                mode="multiple"
                options={getPayPeriodOptions(payPeriodUnit)}
                fieldProps={{
                    optionFilterProp: "children",
                    filterOption: (input, option) => {
                        const childrenString = typeof option?.children === 'string' ? option.children : '';
                        return childrenString.toLowerCase().indexOf(input.toLowerCase()) >= 0;
                    }
                }}
                rules={[{required: true, message: 'Please select pay periods!'}]}
            />
            {!commodity?.commodityCode && (<ProFormSelect
                name="chargeType"
                label="付费方式"
                options={[
                    {label: '包年包月', value: "PrePaid"},
                ]}
                rules={[{required: true, message: 'Please select charge type!'}]}
            />)}
            <ProFormSelect
                name="serviceVersion"
                label="服务版本"
                rules={[{ required: true, message: 'Please select a service version!' }]}
                dependencies={["serviceId"]}
                placeholder={"只展示当前服务的默认服务版本"}
                request={async (commodity) => {
                    if (!commodity?.serviceId) {
                        return [];
                    }
                    try {
                        const response:API.ServiceVersionModel[] = await listServices({
                            serviceId: commodity?.serviceId
                        });
                        if (response && response.length !== 0) {
                            return response.map(item => ({
                                label: item.serviceVersion,
                                value: item.serviceVersion
                            }));
                        }
                    } catch (e) {
                    }
                    return [];
                }
                }
            />

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
        valueType: 'text',
        search: false

    },

    {
        title: '单价',
        dataIndex: 'unitPrice',
        key: 'unitPrice',
        valueType: 'money',
        search: false,
    },
    {
        title: '币种',
        dataIndex: 'currency',
        key: 'currency',
        valueType: 'text',
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

    onCancel: () => void

}

export const SpecificationForm: React.FC<SpecificationFormProps> = ({
                                                                        initialValues,
                                                                        onSubmit,
                                                                        onCancel
                                                                    }) => {
    const [payPeriodUnit, setPayPeriodUnit] = useState(initialValues?.payPeriodUnit);
    const [form] = Form.useForm(); // 引入 useForm 钩子

    // 根据单位购买周期动态生成允许购买的周期的选项
    const getPayPeriodOptions = (unit) => {
        if (unit === 'Month') {
            return Array.from({length: 12}, (_, i) => ({label: (i + 1).toString(), value: i + 1}));
        } else if (unit === 'Day') {
            return Array.from({length: 30}, (_, i) => ({label: (i + 1).toString(), value: i + 1}));
        } else if (unit === 'Year') {
            return Array.from({length: 2}, (_, i) => ({label: (i + 1).toString(), value: i + 1}));
        }
        return [];
    };

    return (
        <ProForm<API.CreateCommoditySpecificationParam>
            form={form} // 将表单实例传给 ProForm
            onFinish={onSubmit}
            onFinishFailed={(errorInfo) => {
                console.log(errorInfo);
            }}
            submitter={{
                render: (_, dom) => (
                    <>
                        <Space size={"small"}>
                            <Button
                                onClick={() => {
                                    onCancel();
                                }}
                            >
                                取消
                            </Button>
                            {dom[1]}
                        </Space>

                    </>
                ),
            }}
            initialValues={initialValues}
        >
            <ProFormText
                name="commodityCode"
                label="商品码"
                disabled={true}
                hidden={true}
            />
            {<ProFormSelect
                name="specificationName"
                dependencies={["specificationName"]}
                label="套餐名"
                rules={[{required: true, message: 'Please input specification name!'}]}
                request={async () => {
                    console.log(initialValues);
                    if (!initialValues?.commodityCode) {
                        return [];

                    }
                    try {
                        let specificationNamesSet = new Set<string>();

                        const specificationDTOs:API.ListResultCommoditySpecificationDTO_ = await listAllSpecifications({commodityCode: initialValues?.commodityCode});
                        if (specificationDTOs && specificationDTOs.code === "200" && specificationDTOs.data && specificationDTOs.data.length > 0) {
                            specificationDTOs.data.forEach((dto: API.CommoditySpecificationDTO) => {
                                if (dto.specificationName) {
                                    specificationNamesSet.add(dto.specificationName);
                                }
                            });
                        }
                        const response:API.BaseResultServiceMetadataModel_ = await getServiceMetadata({
                            commodityCode: initialValues?.commodityCode
                        });
                        if (response && response.code === "200" && response.data?.specificationNameList && response.data?.specificationNameList.length > 0) {
                            const filteredList = response.data.specificationNameList.filter(item => !specificationNamesSet.has(item));

                            return filteredList.map(item => ({
                                label: item,
                                value: item
                            }));

                        }
                    } catch (e) {
                    }
                    return [];
                }
                }
            />}
            <ProFormDigit
                name="unitPrice"
                label="单价"
                fieldProps={{precision: 2}}
                min={0}
                rules={[{required: true, message: 'Please input unit price!'}]}
            />
            <ProFormSelect
                name="payPeriodUnit"
                label="单位购买周期"
                options={[
                    {label: '月', value: 'Month'},
                    {label: '日', value: 'Day'},
                    {label: '年', value: 'Year'}
                ]}
                rules={[{required: true, message: 'Please select pay period unit!'}]}
                fieldProps={{
                    onChange: (value) => {
                        if (value !== payPeriodUnit) {
                            setPayPeriodUnit(value as string);
                            form.setFieldsValue({ payPeriods: undefined });
                        }
                    }
                }}
            />
            <ProFormSelect
                name="payPeriods"
                label="允许购买的周期"
                mode="multiple"
                options={getPayPeriodOptions(payPeriodUnit)}
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
    );
}
