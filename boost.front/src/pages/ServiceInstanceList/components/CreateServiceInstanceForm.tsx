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

import {Col, Modal, Row} from "antd";
import ProCard from "@ant-design/pro-card";
import React, {useContext, useEffect, useState} from "react";

import {ProForm, ProFormContext, ProFormSelect} from "@ant-design/pro-form";
import {CustomParameters, defaultSpecification} from "@/pages/PageCustomConfig";
import {PayPeriodFormItem} from "@/pages/ServiceInstanceList/components/PayPeriodFormItem";
import inner from "bizcharts/src/components/Tooltip/inner";
import {getServiceCost, getServiceMetadata} from "@/services/backend/serviceManager";
import {createFormItem} from "@/util/FormItemUtil";
import PayFormItem from "@/pages/ServiceInstanceList/components/PayTypeFormItem";
import {
    ParameterGroupsInterface,
    ParameterTypeInterfaceArray,
    Specification
} from "@/pages/ServiceInstanceList/components/interface";
import {aliyunRegions} from "@/constants";


export const CreateServiceInstanceForm: React.FC = () => {
    const [selectedCard, setSelectedCard] = useState<string | null>(null);
    const [elements, setElements] = useState<JSX.Element[] | undefined>(undefined);
    const [regionIds, setRegionIds] = useState<{ [key: string]: string } | undefined>(undefined);
    const [deployedRegionId, setDeployedRegionId] = useState<string | undefined>(undefined);
    const form = useContext(ProFormContext);
    const [selectedMonths, setSelectedMonths] = useState<number>(1);
    const [currentPrice, setCurrentPrice] = useState<number | null>(null);
    const [specifications, setSpecifications] = useState<Specification[]>([]);
    const [templateName, setTemplateName] = useState<string | undefined>(undefined);
    const [specificationParameterList, setSpecificationParameterList] = useState<string[]>([]);
    const [errorModalVisible, setErrorModalVisible] = useState(false); // 新增状态


    const handleSpecificationChange = (cardTitle: string | null, months: number) => {
        const currentValues = form.formRef?.current.getFieldsValue();
        console.log(currentValues);
        if (form) {
            form.formRef?.current?.setFieldsValue({
                ...currentValues,
                SpecificationName: cardTitle,
                PayPeriod: months,
                templateName: templateName
            });
        }
    };

    const handleSpecificationCardClick = (cardTitle: string) => {
        if (cardTitle == selectedCard) {
            form.formRef?.current.setFieldValue("SpecificationName", undefined);
            setSelectedCard(null);
            handleSpecificationChange(null, selectedMonths);
        } else {
            setSelectedCard(cardTitle);
            handleSpecificationChange(cardTitle, selectedMonths);
        }
        console.log(specificationParameterList);
        specificationParameterList.forEach((item) => {
            form.formRef?.current.setFieldValue(item, undefined);
        });
    };

    const handleOptionChange = (month: number) => {
        setSelectedMonths(month);
        handleSpecificationChange(selectedCard, month);
    };

    useEffect(() => {
        const elements: JSX.Element[] = [];
        Object.keys(CustomParameters).forEach(key => {
            const element = createFormItem(key, CustomParameters[key], templateName, specifications, specificationParameterList, form);
            elements.push(element);
        })
        const fetchData = async () => {
            try {
                const serviceMetadataResponse = await getServiceMetadata({});
                const serviceMetadata = serviceMetadataResponse?.data;
                if (serviceMetadata !== undefined) {
                    if (serviceMetadata.templateName !== undefined) {
                        setTemplateName(serviceMetadata.templateName);
                    }
                    //Initialize the allowed deployment regions first.
                    let allowedRegionIds: string[];
                    if (serviceMetadata.allowedRegions !== undefined && serviceMetadata.allowedRegions !== "[]") {
                        allowedRegionIds = JSON.parse(serviceMetadata.allowedRegions);
                    } else {
                        allowedRegionIds = aliyunRegions;
                    }
                    setDeployedRegionId(allowedRegionIds.at(0));
                    const valueEnum = allowedRegionIds.reduce((obj, value) => {
                        obj[value] = value;
                        return obj;
                    }, {} as { [key: string]: string });
                    setRegionIds(valueEnum);
                    /**
                     * Step 2: Load the package and preserve package-dependent parameters if the package is not empty.
                     * Use the default package configuration if the package does not exist.
                     */
                    if (serviceMetadata.specifications !== undefined && serviceMetadata.specifications.length > 2) {

                        let specificationsString = typeof serviceMetadata.specifications !== 'object' ? JSON.parse(serviceMetadata.specifications) : serviceMetadata.specifications;
                        const mappedSpecifications: Specification[] = specificationsString.map((spec: any) => {
                            return {
                                Name: spec?.Name,
                                Parameters: spec?.Parameters,
                                OrderList: spec?.OrderList,
                                Type: spec?.Type,
                                Description: spec?.Description,
                            };
                        });
                        mappedSpecifications[0].OrderList.map((parameterName: string) => {
                            setSpecificationParameterList(prevList => {
                                const uniqueSet = new Set(prevList);
                                uniqueSet.add(parameterName);
                                return Array.from(uniqueSet);
                            });

                        });
                        setSpecifications(mappedSpecifications.length > 0 ? mappedSpecifications : defaultSpecification);
                    } else {
                        setSpecifications(defaultSpecification);
                    }

                    /**
                     * Render the parameter input boxes according to the parameter order in the ROS template metadata.
                     * If a parameter does not exist in the metadata parameter list, render it separately at the end.
                     * If no metadata information is available, render the parameters in the order of the ROS template's Parameter index.
                     */
                    if (serviceMetadata.parameterMetadata !== undefined) {
                        let parameterMetadata = typeof serviceMetadata.parameterMetadata !== 'object' ? JSON.parse(serviceMetadata.parameterMetadata) : serviceMetadata.parameterMetadata;
                        console.log(parameterMetadata);
                        const parameterTypeList: ParameterTypeInterfaceArray = parameterMetadata.Parameters;
                        const nameSet = new Set<string>();
                        Object.keys(parameterTypeList).forEach((name) => {
                            nameSet.add(name);
                        });
                        const parameterGroups: ParameterGroupsInterface = parameterMetadata.Metadata;
                        if (parameterTypeList) {
                            if (parameterGroups !== undefined && parameterGroups["ALIYUN::ROS::Interface"] !== undefined && parameterGroups["ALIYUN::ROS::Interface"].ParameterGroups !== undefined) {
                                const group = parameterGroups["ALIYUN::ROS::Interface"].ParameterGroups;
                                group.forEach(parameterGroup => {
                                    for (const parameterName of parameterGroup.Parameters) {
                                        let parameterTypeListElement = parameterTypeList[parameterName];
                                        if (parameterTypeListElement !== undefined) {
                                            const element = createFormItem(parameterName, parameterTypeListElement, templateName, specifications, specificationParameterList, form);
                                            elements.push(element);
                                            nameSet.delete(parameterName);
                                        }
                                    }
                                });
                                nameSet.forEach((name) => {
                                    const element = createFormItem(name, parameterTypeList[name], templateName, specifications, specificationParameterList, form);
                                    elements.push(element);
                                })
                            } else {
                                Object.keys(parameterTypeList).map(key => {
                                    const element = createFormItem(key, parameterTypeList[key], templateName, specifications, specificationParameterList, form);
                                    elements.push(element);
                                });
                            }
                        }
                        setElements(elements);
                        console.log(elements);
                    }

                }
            } catch (error) {
                setSpecifications(defaultSpecification);
                setElements(elements);
                setErrorModalVisible(true);
                if (!errorModalVisible) { // 检查是否已经弹出过错误框
                    Modal.error({
                        title: '获取服务失败',
                        content: (
                            <div>
                                <p>当前套餐和价格均为默认价格。请访问文档修改配置：</p>
                                <p>
                                    <a
                                        href="https://aliyun.github.io/alibabacloud-compute-nest-saas-boost/"
                                        target="_blank"
                                        rel="noopener noreferrer"
                                    >
                                        https://aliyun.github.io/alibabacloud-compute-nest-saas-boost/
                                    </a>
                                </p>
                            </div>
                        ),
                        onOk: () => {
                            setErrorModalVisible(false);
                        },
                    });
                }
            }
        };

        fetchData();
    }, [templateName]);

    useEffect(() => {
        const fetchServiceCost = async () => {
            try {
                if (selectedCard && selectedMonths) {
                    const response = await getServiceCost({
                        specificationName: selectedCard,
                        payPeriod: selectedMonths,
                        payPeriodUnit: "Month",
                    } as API.getServiceCostParams);
                    setCurrentPrice(response.data || null);
                    return;
                }
                setCurrentPrice(null);
            } catch (error) {
                Modal.error({
                    title: '套餐名不匹配',
                    content: (
                        <div>
                            <p>套餐名不匹配，请修改后重新运行流水线：</p>
                            <p>
                                <a
                                    href="https://aliyun.github.io/alibabacloud-compute-nest-saas-boost/"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                >
                                    https://aliyun.github.io/alibabacloud-compute-nest-saas-boost/
                                </a>
                            </p>
                        </div>
                    ),
                });
                setCurrentPrice(100);
            }
        };

        fetchServiceCost();
    }, [selectedCard, selectedMonths]);


    const numColumns = specifications.length;
    const colSpan = 24 / numColumns;
    return (
                <ProForm.Item>
                    <ProForm.Item name={"SpecificationName"} rules={[{required: true, message: '请选择套餐'}]}>
                        <ProCard title="套餐" bordered headerBordered gutter={16} hoverable>
                            <Row gutter={[16, 16]}>
                                {specifications.map((spec) => (
                                    <Col span={colSpan} key={spec?.Name}>
                                        <div
                                            style={{
                                                background: selectedCard === spec.Name ? '#89c1f5' : '#f5f5f5',
                                                cursor: 'pointer',
                                                boxShadow: selectedCard === spec.Name ? '0 0 5px #1890ff' : 'none',
                                                padding: 16,
                                                minHeight: '200px',
                                            }}
                                            onClick={() => handleSpecificationCardClick(spec.Name)}
                                        >
                                            <h3 style={{textAlign: 'center'}}>{spec?.Name}</h3>
                                            <p>{spec?.Description}</p>
                                        </div>
                                    </Col>
                                ))}
                            </Row>
                        </ProCard>
                    </ProForm.Item>
                    <ProCard title="按月购买" bordered headerBordered={false} gutter={16} hoverable>

                        <PayPeriodFormItem onChange={handleOptionChange}/>

                        <div style={{textAlign: "right", padding: "16px"}}>
                            当前价格: <span
                            style={{color: "red"}}>{currentPrice ? currentPrice.toFixed(2) : "加载中..."}</span>
                        </div>
                    </ProCard>
                    <ProCard title={"部署地域"} bordered headerBordered hoverable>
                        <ProFormSelect key={"RegionId"} name={"RegionId"}
                                       valueEnum={regionIds}
                                       rules={[{required: true, message: '请选择部署地域'}]} fieldProps={{
                            onChange: (e) => {
                                setDeployedRegionId(e);
                                form.formRef?.current.setFieldValue("ZoneId", undefined);
                            }
                        }}>
                        </ProFormSelect>
                    </ProCard>
                    <ProCard type={"inner"} title={"配置参数"} bordered headerBordered hoverable>
                        {elements}
                    </ProCard>
                    <PayFormItem/>
                </ProForm.Item>
    )
}
