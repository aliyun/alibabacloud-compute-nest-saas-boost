import React, {useEffect, useState} from "react";
import moment from "moment";
import {listOrders, refundOrder} from "@/services/backend/order";
import {OrderColumns, TradeStatusEnum} from "@/pages/Order/common";
import {PageContainer} from "@ant-design/pro-layout";
import {ProTable} from "@ant-design/pro-components";
import {Button, message, Modal, Pagination, Typography} from "antd";
import {handleGoToPage} from "@/util/nextTokenUtil";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";

import {ServiceInstanceOrderProps} from "@/pages/ServiceInstanceOrder/components/interface";
import {PayTypeEnum, ProductNameEnum, TIME_FORMAT} from "@/constants";
import {getHashSearchParams} from "@/util/urlUtil";
import styles from "./components/css/order.module.css"
import {ExclamationCircleOutlined} from "@ant-design/icons";

dayjs.extend(utc);
export const Index: React.FC<ServiceInstanceOrderProps> = (props) => {
    const searchParams = getHashSearchParams();
    const initialOrderId = searchParams.get('orderId') || undefined;
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
        tradeStatus?: | 'TRADE_CLOSED'
            | 'TRADE_SUCCESS'
            | 'WAIT_BUYER_PAY'
            | 'TRADE_FINISHED'
            | 'REFUNDED'
            | 'REFUNDING';
        gmtCreate?: string;
        orderId?: string;
    }>({orderId: initialOrderId});

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
        let param = {
            maxResults: pageSize,
            nextToken: nextTokens[currentPage - 1],
        } as API.ListOrdersParam;
        if (props.serviceInstanceId) {
            param.serviceInstanceId = props.serviceInstanceId;
        }

        if (filterValues.tradeStatus != undefined) {
            param.tradeStatus = [filterValues.tradeStatus];
        }
        console.log(filterValues);
        if (filterValues.orderId != null) {
            param.orderId = filterValues.orderId;
            console.log(filterValues);
        }
        if (filterValues.gmtCreate != null) {
            param.startTime = moment(filterValues.gmtCreate).utc().format(TIME_FORMAT);
            const currentTime = dayjs();
            param.endTime = currentTime.utc().format(TIME_FORMAT);
        } else {
            const currentTime = dayjs();
            const utcTime = currentTime.utc().format(TIME_FORMAT);
            param.startTime = currentTime.utc().subtract(1, 'year').format(TIME_FORMAT);
            param.endTime = utcTime;
        }

        const result: API.ListResultOrderDTO_ = await listOrders(param);

        if (result.data !== undefined) {
            setTotal(result.count || 0);
            nextTokens[currentPage] = result.nextToken;
            for (let index = 0; index < result.data.length; index++) {
                let item = result.data[index];
                console.log(item.tradeStatus);
                if (canRefundOrderIndex == undefined && item.tradeStatus == 'TRADE_SUCCESS') {
                    setCanRefundOrderIndex(index);
                    break;
                }
            }
            console.log(canRefundOrderIndex);
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
    const columns = props.serviceInstanceId ? OrderColumns.concat([
        {
            title: '操作',
            key: 'action',
            sorter: false,
            search: false,
            // @ts-ignore
            render: (text?: string, record?: any, index) => {
                if (canRefundOrderIndex == index
                ) {
                    const refundButton = (
                        <div className={styles.refundButton} onClick={() => handleButtonClick(record)}>
                            退款
                        </div>
                    );

                    const refundModal = (
                        <Modal title={<div>
                            <ExclamationCircleOutlined style={{color: '#faad14', marginRight: 8}}/>
                            确定要进行退款吗？
                        </div>} open={visible} onCancel={handleModalClose} footer={null}>
                            <Paragraph style={{marginLeft: '24px'}}>您当前订单可退金额为：<span
                                style={{color: "red"}}>¥ {refundAmount}</span></Paragraph>
                            <div style={{marginTop: 16, textAlign: 'right'}}>

                                <Button onClick={handleModalClose}>取消</Button>
                                <Button type="primary"
                                        onClick={handleConfirmRefund}>
                                    确认退款
                                </Button>
                            </div>
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
    ]) : OrderColumns;
    return (
        <PageContainer title={props.serviceInstanceId}>
            <ProTable
                onSubmit={(values) => {
                    setFilterValues(values);
                    setShouldFetchData(true);
                }}
                headerTitle={'服务实例订单列表'}
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
                    layout: 'horizontal',
                    span: 6,
                    optionRender: (searchConfig, formProps, dom) => [
                        <div style={{display: 'flex', gap: '8px', marginBottom: '24px', marginRight: '+220px'}}>
                            <div>{dom[0]}</div>
                            <div>{dom[1]}</div>
                            {/* 重置按钮 */}
                        </div>
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

