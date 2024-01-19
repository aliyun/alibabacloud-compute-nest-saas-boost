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

import {Divider, List, Modal, Radio} from "antd";
import ProCard from "@ant-design/pro-card";
import React, {useContext, useEffect, useState} from "react";
import {ProForm, ProFormContext, ProFormDigit, ProFormSelect, ProFormText} from "@ant-design/pro-form";
import {CustomParameters, defaultSpecification} from "@/pages/PageCustomConfig";
import {getServiceCost, getServiceMetadata} from "@/services/backend/serviceManager";
import {createFormItem} from "@/util/FormItemUtil";
import PayTypeFormItem from "@/pages/Service/component/PayTypeFormItem";
import {
    ParameterGroupsInterface,
    ParameterTypeInterfaceArray,
    Specification
} from "@/pages/Service/component/interface";
import {ALIYUN_REGIONS} from "@/constants";
import styles from "./css/service.module.css";
import {DEFAULT_PAY_PERIOD_UNIT, showErrorModal} from "@/global";

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

    const handleSpecificationCardClick = (cardTitle: string) => {
        form.formRef?.current.setFieldValue("SpecificationName", cardTitle);
        setSelectedCard(cardTitle);
        console.log(specificationParameterList);
        specificationParameterList.forEach((item) => {
            form.formRef?.current.setFieldValue(item, undefined);
        });
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
                        form.formRef?.current.setFieldValue("templateName", templateName);
                    }
                    //Initialize the allowed deployment regions first.
                    let allowedRegionIds: string[];
                    if (serviceMetadata.allowedRegions !== undefined && serviceMetadata.allowedRegions !== "[]") {
                        allowedRegionIds = JSON.parse(serviceMetadata.allowedRegions);
                    } else {
                        allowedRegionIds = ALIYUN_REGIONS;
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
                if (!errorModalVisible) {
                    showErrorModal('获取服务失败', '当前套餐和价格均为默认价格。请访问文档修改配置：');
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
                        payPeriodUnit: DEFAULT_PAY_PERIOD_UNIT,
                    } as API.getServiceCostParams);
                    setCurrentPrice(response.data || null);
                    return;
                }
                setCurrentPrice(null);
            } catch (error) {
                showErrorModal('套餐名不匹配', '套餐名不匹配，请修改后重新运行流水线：');
                setCurrentPrice(100);
            }
        };

        fetchServiceCost();
    }, [selectedCard, selectedMonths]);

    return (
        <ProForm.Item>
            <ProForm.Item name="SpecificationName" key="SpecificationName">
                <div className={styles.specificationTitle}>{"选择套餐"}</div>
                <Radio.Group>
                    <List
                        className={styles.list}
                        itemLayout="horizontal"
                        dataSource={specifications}
                        renderItem={(item) => (
                            <ProCard bordered={true} hoverable className={styles.card} onClick={() => handleSpecificationCardClick(item.Name)}>

                                <Radio value={item.Name} className={styles.myRadio}>
                                    <div>
                                        <div className={styles.listItemMetaTitle}>{item.Name}</div>
                                    </div>

                                </Radio>
                                <div className={styles.listItemMetaDescription}>{item.Description}</div>
                            </ProCard>
                        )}
                    />
                </Radio.Group>
            </ProForm.Item>
            <Divider className={styles.msrectangleshape}/>
            <div className={styles.specificationTitle}>{"配置参数"}</div>
            <ProFormDigit
                label="包月时间"
                name="PayPeriod"
                key={"PayPeriod"}
                min={1}
                initialValue={1}
                fieldProps={{precision: 0, defaultValue: 1, onChange: (value) => {
                    if(value){
                        setSelectedMonths(value);
                    }}}}
                required={true}

            />
            <ProFormSelect key={"RegionId"} name={"RegionId"} label={"部署地域"}
                           className={styles.inputConfig}
                           valueEnum={regionIds}
                           rules={[{required: true, message: '请选择部署地域'}]} fieldProps={{
                onChange: (e) => {
                    setDeployedRegionId(e);
                    form.formRef?.current.setFieldValue("ZoneId", undefined);
                }
            }}>
            </ProFormSelect>
            {elements}
            <div className={styles.currentPrice}>
                当前价格:
                <span className={styles.priceValue}>
                    {currentPrice ? `     ¥${currentPrice.toFixed(2)}` : " 加载中..."}
                </span>
            </div>
            <Divider className={styles.msrectangleshape}/>
            <div className={styles.specificationTitle}>{"支付方式"}</div>
            <PayTypeFormItem/>
            <ProFormText name="templateName" key="templateName" initialValue={templateName} fieldProps={{defaultValue: templateName, value: templateName}} hidden={true}></ProFormText>
        </ProForm.Item>
    )
}
