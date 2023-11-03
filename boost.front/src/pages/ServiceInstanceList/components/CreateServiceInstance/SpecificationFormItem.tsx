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

import {Col, Row} from "antd";
import ProCard from "@ant-design/pro-card";
import React, {useContext, useEffect, useState} from "react";
import {PayPeriodFormItem} from "@/pages/ServiceInstanceList/components/CreateServiceInstance/PayPeriodFormItem";
import {getServiceCost, getServiceMetadata} from "@/services/backend/serviceInstance";
import {
    ParameterGroupsInterface,
    ParameterTypeInterface,
    ParameterTypeInterfaceArray,
    Specification
} from "@/pages/ServiceInstanceList/components/CreateServiceInstance/ServiceMetadataInterface";
import {ProForm, ProFormContext, ProFormDigit, ProFormItem, ProFormSelect, ProFormText} from "@ant-design/pro-form";
import {validatePassword} from "@/pages/ServiceInstanceList/components/PasswordFormItem";
import {CustomParameters, defaultSpecification} from "@/specificationConfig";


export const SpecificationFormItem: React.FC = ({}) => {
    const [selectedCard, setSelectedCard] = useState<string | null>(null);
    const [elements, setElements] = useState<JSX.Element[] | undefined>(undefined);
    const form = useContext(ProFormContext);
    const [selectedMonths, setSelectedMonths] = useState<number>(1);
    const [currentPrice, setCurrentPrice] = useState<number | null>(null);
    const [specifications, setSpecifications] = useState<Specification[]>([]);
    const [templateName, setTemplateName] = useState<String | undefined>(undefined);
    const specificationParameterList:string[] = [];

    const handleSpecificationChange = (cardTitle: string | null, months: number) => {
        const value = cardTitle ? {specificationName: cardTitle, payPeriod: months} : null;
        console.log(value);
        const currentValues = form.formRef?.current.getFieldsValue();
        if (form) {
            form.formRef?.current?.setFieldsValue({...currentValues, specification: value, templateName: templateName});
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
        if (parsedType === 'boolean') {
            return (
                <ProFormSelect initialValue={defaultValue} valueEnum={{true: '是', false: '否'}}
                               label={label} key={key} name={key} rules={[{required: true}]}/>
            );
        } else if (parsedType === 'string') {
            const allowedValues = parsedValue.AllowedValues ?? [];
            if (allowedValues.length > 0) {
                const result = allowedValues.reduce((obj, value) => {
                    obj[value] = value;
                    return obj;
                }, {} as { [key: string]: string });
                return (
                    <ProFormSelect initialValue={defaultValue} label={label} key={key} name={key} valueEnum={result}
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
            return (<ProFormDigit initialValue={defaultValue} label={label} key={key} name={key} rules={rules} min={minValue} max={maxValue}/>);
        } else {
            return (<ProFormText initialValue={defaultValue} label={label} key={key} name={key} rules={rules}/>);
        }
    }

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await getServiceMetadata({});
                console.log(response);
                if (response.data !== undefined) {
                    if (response.data.templateName !== undefined) {
                        setTemplateName(response.data.templateName);
                    }
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
                        mappedSpecifications[0].OrderList.map((parameterName: string)=>{
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
                        const elements: JSX.Element[] = [];
                        Object.keys(CustomParameters).forEach(key => {
                            const element = createFormItem(key, CustomParameters[key]);
                            elements.push(element);
                        })
                        console.log(parameterTypeList);
                        console.log(parameterGroups);
                        if (parameterTypeList) {
                            if (parameterGroups !== undefined && parameterGroups["ALIYUN::ROS::Interface"] !== undefined && parameterGroups["ALIYUN::ROS::Interface"].ParameterGroups !== undefined) {
                                const group = parameterGroups["ALIYUN::ROS::Interface"].ParameterGroups;
                                group.forEach(parameterGroup => {
                                    for (const parameterName of parameterGroup.Parameters) {
                                        let parameterTypeListElement = parameterTypeList[parameterName];
                                        if (parameterTypeListElement !== undefined && !specificationParameterList.includes(parameterName)) {
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
                console.error('Failed to fetch service metadata:', error);
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
                console.error("Failed to fetch service cost:", error);
            }
        };

        fetchServiceCost();
    }, [selectedCard, selectedMonths]);


    const numColumns = specifications.length;
    const colSpan = 24 / numColumns;
    return (
        <ProForm.Group>
            <ProFormItem>
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

                    <div style={{textAlign: "right", padding: "16px"}}>
                        当前价格: <span
                        style={{color: "red"}}>{currentPrice ? currentPrice.toFixed(2) : "加载中..."}</span>
                    </div>
                    <div>
                        <PayPeriodFormItem onChange={handleOptionChange}/>
                    </div>
                    <div>
                        <ProCard type={"inner"} title={"配置参数"} bordered headerBordered hoverable>
                            {elements}
                        </ProCard>
                    </div>
                </ProCard>
            </ProFormItem>

        </ProForm.Group>
    )
}
