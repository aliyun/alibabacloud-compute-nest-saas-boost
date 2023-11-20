import React, {useEffect, useState} from "react";
import moment from "moment";
import {listOrders, refundOrder} from "@/services/backend/order";
import {OrderColumns, TradeStatusEnum} from "@/pages/Order/common";
import {PayTypeEnum} from "@/pages/ServiceInstanceList/components/form/PayTypeFormItem";
import {ProductNameEnum} from "@/pages/Order";
import {PageContainer} from "@ant-design/pro-layout";
import {ProTable} from "@ant-design/pro-components";
import {Button, message, Modal, Pagination, Typography} from "antd";
import {handleGoToPage} from "@/nextTokenUtil";
import ProCard from "@ant-design/pro-card";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

interface ServiceInstanceContentProps {
    serviceInstanceId?: string;
    status?: string;
}
dayjs.extend(utc);
export const ServiceInstanceOrder: React.FC<ServiceInstanceContentProps> = (props) => {
    const [orders, setOrders] = useState<API.OrderDTO[]>([]);
    const pageSize = 10;
    const [visible, setVisible] = useState(false);
    const [refundAmount, setRefundAmount] = useState<string>("0.00");
    const [total, setTotal] = useState<number>(0);
    const [nextTokens, setNextTokens] = useState<(string | undefined)[]>([undefined]);
    const [shouldFetchData, setShouldFetchData] = useState(false);
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [orderId, setOrderId] = useState<string | null>(null);
    const [canRefundOrderIndex, setCanRefundOrderIndex] = useState<number | undefined>(undefined);
    const {Paragraph} = Typography;

    const [filterValues, setFilterValues] = useState<{
        tradeStatus?: string;
        gmtCreate?: string;
        type?: string;
    }>({});

    useEffect(() => {
        fetchData(currentPage, true);
    }, [currentPage, shouldFetchData]);

    useEffect(() => {
    }, [canRefundOrderIndex]);

    const handleConfirmRefund = async (): Promise<void> => {
        try {
            if (orderId) {
                await refundOrder({orderId: orderId, dryRun: false});
                setOrderId(null);
                message.success('退款中');
                window.location.reload();
            }
        } catch (error) {
            console.error(error);
            message.error('退款失败');
        }
        setVisible(false);
    };

    const handleButtonClick = async (record: any) => {
        try {
            const response = await refundOrder(
                {orderId: record.orderId, dryRun: true} as API.RefundOrderParam
            );
            setOrderId(record.orderId);
            const data = response?.data;
            if (data !== undefined) {
                setRefundAmount(data.toFixed(2));
                setVisible(true);
            }
        } catch (error) {
            console.error(error);
        }
    };

    const handleModalClose = () => {
        setVisible(false);
    };

    const fetchData = async (currentPage: number, show: boolean) => {
        const result: API.ListResultOrderDTO_ = await listOrders({
            maxResults: pageSize,
            nextToken: nextTokens[currentPage - 1],
            serviceInstanceId: props.serviceInstanceId
        });
        if (result.data !== undefined) {
            setTotal(result.count || 0);
            nextTokens[currentPage] = result.nextToken;
            for (let index = 0; index < result.data.length; index++) {
                let item = result.data[index];
                if (canRefundOrderIndex == undefined && item.tradeStatus == 'TRADE_SUCCESS') {
                    setCanRefundOrderIndex(index);
                    break;
                }
            }
            const transformedData = result.data?.map((item: API.OrderDTO, index) => {
                const localTime = item.gmtCreate ? moment.utc(item.gmtCreate).local().format('YYYY-MM-DD HH:mm:ss') : '';
                return {
                    ...item,
                    gmtCreate: localTime,
                    tradeStatus: TradeStatusEnum[item.tradeStatus as keyof typeof TradeStatusEnum],
                    productName: ProductNameEnum[item.productName as keyof typeof ProductNameEnum],
                    type: PayTypeEnum[item.type as keyof typeof PayTypeEnum],
                };
            }) || [];
            if (show) {
                //@ts-ignored
                setOrders(transformedData);
                setShouldFetchData(false);
            }

        }
    };
    const columns = OrderColumns.concat([
        {
            title: '',
            key: 'action',
            sorter: false,
            search: false,
            // @ts-ignore
            render: (text?: string, record?: any, index) => {
                if (canRefundOrderIndex == index && props.status == 'Success'
                ) {
                    const refundButton = (
                        <Button type="primary" onClick={() => handleButtonClick(record)}>
                            退款
                        </Button>
                    );

                    const refundModal = (
                        <Modal open={visible} onCancel={handleModalClose} footer={null}>
                            <ProCard title="退款金额">
                                <Paragraph>您当前订单可退金额为：<span
                                    style={{color: "red"}}>{refundAmount}</span></Paragraph>
                                <div style={{marginTop: 16, textAlign: 'right'}}>
                                    <Button style={{width: '100px'}} className="ant-btn ant-btn-primary" type="primary"
                                            onClick={handleConfirmRefund}>
                                        退款
                                    </Button>
                                    <Button style={{width: '100px'}} className="ant-btn ant-btn-default"
                                            onClick={handleModalClose}>取消</Button>
                                </div>
                            </ProCard>
                        </Modal>
                    );
                    return (
                        <>
                            {refundButton}
                            {refundModal}
                        </>
                    );
                }
                return null;
            },
        },
    ]);
    return (
        <PageContainer title={props.serviceInstanceId}>
            <ProTable
                onSubmit={(values) => {
                    setFilterValues(values);
                    setShouldFetchData(true);
                }}
                options={{
                    search: false,
                    density: false,
                    fullScreen: true,
                    reload: () => {
                        setShouldFetchData(true);
                    },
                    setting: false,
                }}

                search={{
                    labelWidth: 'auto',
                    defaultCollapsed: false,
                    layout: 'vertical',
                    optionRender: ({searchText, resetText}, {form}) => [
                        <Button
                            type="primary"
                            key="search"
                            onClick={() => {
                                form?.submit();
                            }}
                        >
                            {searchText}
                        </Button>,
                        <Button
                            key="reset"
                            onClick={() => {
                                form?.resetFields();
                            }}
                        >
                            {resetText}
                        </Button>,
                    ],
                }}
                columns={columns} dataSource={orders} rowKey="key" pagination={false}/>
            <Pagination
                style={{marginTop: '16px', textAlign: 'right'}}
                current={currentPage}
                pageSize={pageSize}
                total={total}
                onChange={(page, pageSize) => handleGoToPage(page, currentPage, total, fetchData, setCurrentPage)}
                prevIcon={<span
                    onClick={() => handleGoToPage(currentPage + 1, currentPage, total, fetchData, setCurrentPage)}>上一页</span>}
                nextIcon={<span
                    onClick={() => handleGoToPage(currentPage - 1, currentPage, total, fetchData, setCurrentPage)}>下一页</span>}
                showSizeChanger={false}
            />
        </PageContainer>
    );
}

