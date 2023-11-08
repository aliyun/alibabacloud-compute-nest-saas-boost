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
import {getServiceCost, getServiceMetadata} from "@/services/backend/serviceInstance";
import {
    ParameterGroupsInterface,
    ParameterTypeInterface,
    ParameterTypeInterfaceArray,
    Specification
} from "@/pages/ServiceInstanceList/components/CreateServiceInstance/ServiceMetadataInterface";
import {
    ProForm,
    ProFormContext,
    ProFormDependency,
    ProFormDigit,
    ProFormSelect,
    ProFormText
} from "@ant-design/pro-form";
import {validatePassword} from "@/pages/ServiceInstanceList/components/PasswordFormItem";
import {CustomParameters, defaultSpecification, RegionParameters} from "@/specificationConfig";
import {PayPeriodFormItem} from "@/pages/ServiceInstanceList/components/CreateServiceInstance/PayPeriodFormItem";
import inner from "bizcharts/src/components/Tooltip/inner";


export const SpecificationFormItem: React.FC = ({}) => {
    const [selectedCard, setSelectedCard] = useState<string | null>(null);
    const [elements, setElements] = useState<JSX.Element[] | undefined>(undefined);
    const form = useContext(ProFormContext);
    const [selectedMonths, setSelectedMonths] = useState<number>(1);
    const [currentPrice, setCurrentPrice] = useState<number | null>(null);
    const [specifications, setSpecifications] = useState<Specification[]>([]);
    const [templateName, setTemplateName] = useState<String | undefined>(undefined);
    const specificationParameterList: string[] = [];

    const handleSpecificationChange = (cardTitle: string | null, months: number) => {
        const value = cardTitle ? {specificationName: cardTitle, payPeriod: months} : null;
        const currentValues = form.formRef?.current.getFieldsValue();
        if (form) {
            form.formRef?.current?.setFieldsValue({
                ...currentValues,
                SpecificationName: cardTitle,
                PayPeriod: months,
                templateName: templateName
            });
        }
    };

    const handleCardClick = (cardTitle: string) => {
        setSelectedCard(cardTitle === selectedCard ? null : cardTitle);
        handleSpecificationChange(cardTitle, selectedMonths);
    };

    const handleOptionChange = (month: number) => {
        setSelectedMonths(month);
        handleSpecificationChange(selectedCard, month);
    };

    function isDependencyWithSpecification(parameterName: string) {
        return specificationParameterList.includes(parameterName);
    }

    function createFormItem(key: string, value: ParameterTypeInterface) {
        const parsedValue = value as ParameterTypeInterface;
        const noEcho = parsedValue?.NoEcho ?? false;
        const allowPattern = parsedValue?.AllowedPattern;
        let patternRegEx;
        if (allowPattern !== undefined) {
            patternRegEx = new RegExp(allowPattern);
        }
        const label = parsedValue?.Label != undefined ? parsedValue?.Label['zh-cn'] : "";
        const defaultValue = parsedValue?.Default;
        const rules = [{pattern: patternRegEx, message: label, required: true},];
        const parsedType = parsedValue?.Type != undefined ? parsedValue?.Type.toLowerCase() : "";
        console.log(parsedValue);
        let dependencyWithSpecification = isDependencyWithSpecification(key);
        if (parsedType === 'boolean') {
            return (
                <ProFormSelect initialValue={defaultValue} valueEnum={{true: '是', false: '否'}}
                               label={label} key={key} name={key} rules={[{required: true}]}/>
            );
        } else if (dependencyWithSpecification) {
            return (
                <ProFormDependency name={["SpecificationName"]}
                                   shouldUpdate={(prevValues, curValues) => prevValues.type !== curValues.type}>
                    {({SpecificationName}) => {
                        console.log("aab");
                        console.log(SpecificationName);
                        const currentSpecificationParameters = specifications.findLast((specification: Specification) => {
                            return specification.Name == SpecificationName;
                        })?.Parameters[key];

                        const valueEnum = currentSpecificationParameters?.reduce((obj, value) => {
                            obj[value] = value;
                            return obj;
                        }, {} as { [key: string]: string });
                        console.log(valueEnum);
                        if (currentSpecificationParameters !== undefined) {
                            console.log(currentSpecificationParameters[0]);
                            return (
                                <ProFormSelect label={label}
                                               valueEnum={valueEnum}
                                               rules={[{required: true}]} fieldProps={{
                                    onChange: (e) => {

                                    }, value: currentSpecificationParameters[0]
                                }}>
                                </ProFormSelect>)
                        } else {
                            return (<ProFormText label={label} key={key} name={key} rules={rules}/>);
                        }

                    }
                    }

                </ProFormDependency>
            );
        } else if (parsedType === 'string') {
            const allowedValues = parsedValue.AllowedValues ?? [];
            if (allowedValues.length > 0) {

                const valueEnum = allowedValues.reduce((obj, value) => {
                    obj[value] = value;
                    return obj;
                }, {} as { [key: string]: string });
                return (
                    <ProFormSelect initialValue={defaultValue} label={label} key={key} name={key}
                                   valueEnum={valueEnum}
                                   rules={[{required: true}]}>
                    </ProFormSelect>
                );


            } else {
                return noEcho ?
                    <ProFormText.Password initialValue={parsedValue.Default} label={label}
                                          key={key} name={key} rules={[
                        {required: true, message: label},
                        validatePassword,
                    ]}/> :
                    <ProFormText initialValue={defaultValue} label={label} key={key} name={key} rules={rules}/>;
            }
        } else if (parsedType === 'number') {
            const minValue = parsedValue?.MinValue;
            const maxValue = parsedValue?.MaxValue;
            return (<ProFormDigit initialValue={defaultValue} label={label} key={key} name={key} rules={rules}
                                  min={minValue} max={maxValue}/>);
        } else {
            return (<ProFormText initialValue={defaultValue} label={label} key={key} name={key} rules={rules}/>);
        }
    }

    useEffect(() => {
        const elements: JSX.Element[] = [];
        Object.keys(CustomParameters).forEach(key => {
            const element = createFormItem(key, CustomParameters[key]);
            elements.push(element);
        })
        const fetchData = async () => {
            try {
                const response = await getServiceMetadata({});
                console.log(response);
                if (response.data !== undefined) {
                    if (response.data.templateName !== undefined) {
                        setTemplateName(response.data.templateName);
                    }
                    // if (response.data.allowedRegions !== undefined) {
                    //     RegionParameters[0].AllowedValues = response.data.allowedRegions;
                    // }
                    Object.keys(RegionParameters).forEach(key => {
                        const element = createFormItem(key, RegionParameters[key]);
                        elements.push(element);
                    })
                    if (response.data.specifications !== undefined && response.data.specifications.length > 2) {

                        let specificationsString = typeof response.data.specifications !== 'object' ? JSON.parse(response.data.specifications) : response.data.specifications;
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
                            specificationParameterList.push(parameterName);
                        })
                        setSpecifications(mappedSpecifications.length > 0 ? mappedSpecifications : defaultSpecification);
                    } else {
                        setSpecifications(defaultSpecification);
                    }

                    if (response.data.parameterMetadata !== undefined) {
                        let parameterMetadata = typeof response.data.parameterMetadata !== 'object' ? JSON.parse(response.data.parameterMetadata) : response.data.parameterMetadata;
                        console.log(parameterMetadata);
                        const parameterTypeList: ParameterTypeInterfaceArray = parameterMetadata.Parameters;
                        const parameterGroups: ParameterGroupsInterface = parameterMetadata.Metadata;
                        console.log(parameterTypeList);
                        console.log(parameterGroups);
                        if (parameterTypeList) {
                            if (parameterGroups !== undefined && parameterGroups["ALIYUN::ROS::Interface"] !== undefined && parameterGroups["ALIYUN::ROS::Interface"].ParameterGroups !== undefined) {
                                const group = parameterGroups["ALIYUN::ROS::Interface"].ParameterGroups;
                                group.forEach(parameterGroup => {
                                    for (const parameterName of parameterGroup.Parameters) {
                                        let parameterTypeListElement = parameterTypeList[parameterName];
                                        if (parameterTypeListElement !== undefined) {
                                            const element = createFormItem(parameterName, parameterTypeListElement);
                                            elements.push(element);
                                        }
                                    }
                                });
                            } else {
                                Object.keys(parameterTypeList).map(key => {
                                    const element = createFormItem(key, parameterTypeList[key]);
                                    elements.push(element);
                                });
                            }
                        }
                        setElements(elements);
                    }
                }
            } catch (error) {
                setSpecifications(defaultSpecification);
                setElements(elements);
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
                    },
                });
            }
        };

        fetchData();
    }, []);

    useEffect(() => {
        const fetchServiceCost = async () => {
            try {
                if (selectedCard) {
                    const response = await getServiceCost({
                        specificationName: selectedCard,
                        payPeriod: selectedMonths,
                        payPeriodUnit: "Month",
                    } as API.getServiceCostParams);
                    console.log(response.data);
                    setCurrentPrice(response.data || null);
                }
            } catch (error) {
                // 处理错误
                setCurrentPrice(100);
            }
        };

        fetchServiceCost();
    }, [selectedCard, selectedMonths]);


    const numColumns = specifications.length;
    const colSpan = 24 / numColumns;
    return (

        <ProForm.Group>
            <ProForm.Item name={"SpecificationName"}>
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
                                    onClick={() => handleCardClick(spec.Name)}
                                >
                                    <h3>{spec?.Name}</h3>
                                    <p>{spec?.Description}</p>
                                </div>
                            </Col>
                        ))}
                    </Row>

                </ProCard>
            </ProForm.Item>

            <ProForm.Item>
                <ProCard title="按月购买" bordered headerBordered={false} gutter={16} hoverable>

                    <PayPeriodFormItem onChange={handleOptionChange}/>

                    <div style={{textAlign: "right", padding: "16px"}}>
                        当前价格: <span
                        style={{color: "red"}}>{currentPrice ? currentPrice.toFixed(2) : "加载中..."}</span>
                    </div>
                </ProCard>

                <ProCard type={"inner"} title={"配置参数"} bordered headerBordered hoverable>
                    {elements}
                </ProCard>
            </ProForm.Item>


        </ProForm.Group>
    )
}
