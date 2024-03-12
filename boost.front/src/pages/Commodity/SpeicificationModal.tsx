import React, {useEffect, useRef, useState} from 'react';
import {message, Modal, Tooltip} from 'antd';
import {
    createCommoditySpecification,
    deleteCommoditySpecification,
    listAllSpecifications,
    updateCommoditySpecification
} from "@/services/backend/specification";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import {specificationColumns, SpecificationForm, SpecificationModalProps} from "@/pages/Commodity/common";
import {ActionType} from "@ant-design/pro-table/lib";
import {PlusOutlined} from "@ant-design/icons";

const SpecificationModal: React.FC<SpecificationModalProps> = ({commodity, visible, onClose}) => {
    const [specifications, setSpecifications] = useState<API.CommoditySpecificationDTO[]>([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [currentSpecification, setCurrentSpecification] = useState<API.CommoditySpecificationDTO | undefined>(undefined);
    const actionRef = useRef<ActionType>();

    useEffect(() => {
        if (visible) {
            fetchSpecifications();
        }
    }, [visible]);

    const fetchSpecifications = async () => {
        const response = await listAllSpecifications({commodityCode: commodity.commodityCode});
        setSpecifications(response.data || []);
        actionRef.current?.reload();
    };

    const handleSaveSpecification = async (values: API.CreateCommoditySpecificationParam | API.UpdateCommoditySpecificationParam) => {
        try {
            let response;
            if (currentSpecification) {
                response = await updateCommoditySpecification({
                    ...values,
                    commodityCode: commodity.commodityCode,
                    specificationName: currentSpecification.specificationName,
                });
            } else {
                response = await createCommoditySpecification({
                    payPeriods: values.payPeriods,
                    payPeriodUnit: values.payPeriodUnit,
                    unitPrice: values.unitPrice,
                    specificationName: values.specificationName?.trim(),
                    commodityCode: commodity.commodityCode,
                    currency: "CNY",
                });
            }

            if (response.code === "200") {
                message.success(`${currentSpecification ? 'Specification updated' : 'Specification added'} successfully`);
                setCurrentSpecification(undefined);
            } else {
                message.error(`Failed to ${currentSpecification ? 'update' : 'add'} specification`);
            }
        } catch (error) {
            console.error(error);
            message.error(`Failed to ${currentSpecification ? 'update' : 'add'} specification`);
        }
        setTimeout(() => {
            actionRef.current?.reload();
        }, 1500);
        setIsModalVisible(false);
        setCurrentSpecification(undefined);
    };

    const handleDelete = async (specificationName: string) => {
        try {
            await deleteCommoditySpecification({commodityCode: commodity.commodityCode, specificationName});
            // fetchSpecifications();
            message.success('套餐删除成功');
            setTimeout(() => {
                actionRef.current?.reload();
            }, 1000);
        } catch (error) {
            message.error('套餐删除失败');
        }
    };

    const actionColumn: ProColumns<API.CommoditySpecificationDTO> = {
        title: 'Action',
        dataIndex: 'action',
        valueType: 'option',
        render: (text, record, _, action) => [
            <a key="edit" onClick={() => {
                setCurrentSpecification(record);
                console.log(record);
                setIsModalVisible(true);
            }}>编辑</a>,

            <a key="delete" onClick={() => {
                if (record.specificationName) {
                    handleDelete(record.specificationName);
                }
            }} style={{color: 'red'}}>删除</a>,

        ],
    };

    const columns: ProColumns<API.CommoditySpecificationDTO>[] = [
        ...specificationColumns,
        actionColumn,
    ];

    return (

        <Modal
            title={"套餐管理"}
            open={visible}
            onCancel={() => {
                setCurrentSpecification(undefined);
                onClose();
            }}
            footer={null}
        >
            <Modal
                title={currentSpecification ? "编辑套餐" : "新建套餐"}
                open={isModalVisible}
                onCancel={() => setIsModalVisible(false)}
                footer={null}
            >
                <SpecificationForm
                    key={currentSpecification ? currentSpecification.specificationName : 'new'}
                    initialValues={{
                        ...currentSpecification,
                        payPeriods: currentSpecification?.payPeriods ? JSON.parse(currentSpecification.payPeriods) : [],
                    }}
                    onSubmit={handleSaveSpecification}
                />
            </Modal>
            <ProTable
                actionRef={actionRef}
                columns={columns}
                rowKey="specificationName"
                search={false}
                options={{
                    search: false,
                    density: false,
                    fullScreen: false,
                    reload: true,
                    setting: false
                }}
                toolBarRender={() => [
                    <Tooltip key="add" title="新增套餐">
                        <a
                            key="add"
                            type={"link"}
                            onClick={() => {
                                setCurrentSpecification(undefined);
                                setIsModalVisible(true);
                            }}
                            style={{color: 'inherit'}}
                        >
                            <PlusOutlined/>
                            <span>新增</span>
                        </a>
                    </Tooltip>
                ]}
                request={async (params, sorter, filter) => {
                    const response = await listAllSpecifications({commodityCode: commodity.commodityCode});
                    return {
                        data: response.data,
                        success: true,
                        total: response.data != undefined ? response.data.length : 0,
                    };
                }}
            />
        </Modal>

    );
};

export default SpecificationModal;
