import React, {useEffect, useRef, useState} from "react";
import moment from "moment";
import {listOrders, refundOrder} from "@/services/backend/order";
import {OrderColumns, TradeStatusEnum} from "@/pages/Order/common";
import {PageContainer} from "@ant-design/pro-layout";
import {ProTable} from "@ant-design/pro-components";
import {Button, message, Modal, Pagination, Space, Typography} from "antd";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {ProFormInstance} from "@ant-design/pro-form";

import {ServiceInstanceOrderProps} from "@/pages/ServiceInstanceOrder/components/interface";
import {PayChannelEnum, TIME_FORMAT} from "@/constants";
import {getHashSearchParams} from "@/util/urlUtil";
import styles from "./components/css/order.module.css"
import {ExclamationCircleOutlined} from "@ant-design/icons";
import {ActionType} from "@ant-design/pro-table/lib";
import {FetchResult, handleGoToPage} from "@/util/nextTokenUtil";
import {handlePaySubmit} from "@/util/aliPayUtil";
import {createTransaction} from "@/services/backend/payment";
import PayTypeFormItem from "@/pages/Service/component/PayTypeFormItem";
import {centsToYuan} from "@/util/moneyUtil";
import {FormattedMessage} from "@@/exports";
import {ProFormRadio} from "@ant-design/pro-form";

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
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [refundOrderId, setRefundOrderId] = useState<string | null>(null);
    const {Paragraph} = Typography;
    const actionRef = useRef<ActionType>();
    const [payModalVisible, setPayModalVisible] = useState(false);
    const [currentOrder, setCurrentOrder] = useState<API.OrderDTO | null>(null);
    const form = useRef<ProFormInstance>();

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

    const fetchOrders = async (params: {
        pageSize: number;
        current: number;
        [key: string]: any;
    }): Promise<FetchResult<API.OrderDTO>> => {
        let param = {
            maxResults: pageSize,
            nextToken: nextTokens[currentPage - 1],
            endTimeSorterDesc: true
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
        console.log(param);
        const result: API.ListResultOrderDTO_ = await listOrders(param);

        if (result.data !== undefined) {
            setTotal(result.count || 0);
            setOrders(result.data || []);
            nextTokens[currentPage] = result.nextToken;
            const transformedData = result.data?.map((item: API.OrderDTO, index) => {
                const localTime = item.gmtCreate ? moment.utc(item.gmtCreate).local().format('YYYY-MM-DD HH:mm:ss') : '';
                return {
                    ...item,
                    gmtCreate: localTime,
                    tradeStatus: TradeStatusEnum[item.tradeStatus as keyof typeof TradeStatusEnum],
                    type: PayChannelEnum[item.payChannel as keyof typeof PayChannelEnum],
                };
            }) || [];
            return {
                //@ts-ignored
                data: transformedData,
                success: true,
                total: result.count || 0,
            };
        }
        return {
            data: [],
            success: true,
            total: 0,
        }
    }

    useEffect(() => {
        const fetchOrders = async () => {
            const result: API.ListResultOrderDTO_ = await listOrders({
                orderId: initialOrderId
            });
        };
        fetchOrders();

    }, [initialOrderId]);

    const handleConfirmRefund = async (): Promise<void> => {
        try {
            if (refundOrderId) {
                await refundOrder({orderId: refundOrderId, dryRun: false});
                setRefundOrderId(null);
                message.success(<FormattedMessage id='message.refunding' defaultMessage='退款中'/>);
                window.location.reload();
            }
        } catch (error) {
            console.error(error);
            message.error(<FormattedMessage id='message.refund-failed' defaultMessage='退款失败'/>);
        }
        setVisible(false);
    };

    const handleButtonClick = async (record: any) => {
        try {
            const response = await refundOrder(
                {orderId: record.orderId, dryRun: true} as API.RefundOrderParam
            );
            setRefundOrderId(record.orderId);
            const data = response?.data;
            if (data !== undefined) {
                let refundAmount: string | undefined = centsToYuan(data);
                setRefundAmount(refundAmount || "0.00");
                setVisible(true);
            }
        } catch (error) {
            console.error(error);
        }
    };

    const PayModal: React.FC<{
        visible: boolean;
        onCancel: () => void;
    }> = ({ visible, onCancel }) => {
        const [payChannel, setPayChannel] = useState<string>('ALIPAY');
        const [qrCodeUrl, setQrCodeUrl] = useState<string | undefined>();

        // 在用户选择支付方式时调用
        const handlePayChannelChange = async (selectedPayChannel: string) => {
            setPayChannel(selectedPayChannel);
            if (currentOrder) {
                try {
                    const transactionResponse = await createTransaction({
                        orderId: currentOrder.orderId,
                        payChannel: payChannel as PayChannelEnum.ALIPAY | PayChannelEnum.WECHATPAY,
                    });

                    if (transactionResponse.code === '200' && transactionResponse.data) {
                        setQrCodeUrl(transactionResponse.data);
                    } else {
                        message.error('无法获取二维码');
                    }
                } catch (error) {
                    message.error('创建交易失败');
                    console.error('Error when creating transaction:', error);
                }
            }
        };

        // 监听支付方式的变化
        useEffect(() => {
            if (visible) {
                handlePayChannelChange(payChannel);
            } else {
                setQrCodeUrl(undefined);
            }
        }, [visible, payChannel]);

        return (
            <Modal
                title="支付"
                open={visible}
                onCancel={onCancel}
                footer={null}
            >
                <PayTypeFormItem
                    onPayChannelChange={handlePayChannelChange}
                />
                {qrCodeUrl && <img src={qrCodeUrl} style={{ width: '100%' }} alt="Payment QR Code" />}
            </Modal>
        );
    };

    const handlePaySubmitButton = async (record: any) => {
        setPayModalVisible(true);
        setCurrentOrder(record);
    }

    const handleModalClose = () => {
        setVisible(false);
    };

    const columns = OrderColumns.concat([
        {
            title: <FormattedMessage id="pages.instanceSearchTable.titleOption" defaultMessage='操作'/>,
            key: 'action',
            sorter: false,
            search: false,
            // @ts-ignore
            render: (text?: string, record?: any, index) => {
                if (record.tradeStatus == TradeStatusEnum.WAIT_BUYER_PAY) {

                    const payButton = (<a className={styles.payButton} onClick={() => handlePaySubmitButton(record)}>
                        <FormattedMessage id='button.pay' defaultMessage='支付'/>
                    </a>);
                    return (
                        <>                        {payButton}
                        </>
                    );
                }
                if (props.serviceType == "managed" && props.serviceInstanceId != undefined && record.canRefund === true) {
                    const refundButton = (
                        <a className={styles.refundButton} onClick={() => handleButtonClick(record)}>
                            <FormattedMessage id='button.refund' defaultMessage='退款'/>
                        </a>
                    );

                    const refundModal = (
                        <Modal title={<div>
                            <ExclamationCircleOutlined style={{color: '#faad14', marginRight: 8}}/>
                            <FormattedMessage id='message.confirm-refund' defaultMessage='确定要进行退款吗？'/>
                        </div>} open={visible} onCancel={handleModalClose} footer={null}>
                            <Paragraph style={{marginLeft: '24px'}}><FormattedMessage id='message.current-order-refundable-amount' defaultMessage='您当前订单可退金额为：'/><span
                                style={{color: "red"}}>¥ {refundAmount}</span></Paragraph>
                            <div style={{marginTop: 16, textAlign: 'right'}}>
                                <Space>
                                    <Button onClick={handleModalClose}><FormattedMessage id='button.cancel' defaultMessage='取消'/></Button>
                                    <Button type="primary"
                                            onClick={handleConfirmRefund}>
                                        <FormattedMessage id='button.confirm-refund' defaultMessage='确认退款'/>
                                    </Button>
                                </Space>

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
    ])
    return (
        <PageContainer title={props.serviceInstanceId}>

            <PayModal
                visible={payModalVisible}
                onCancel={() => setPayModalVisible(false)}
            />
            <ProTable
                onSubmit={(values) => {
                    // @ts-ignore
                    setFilterValues(values);
                    actionRef.current?.reload();
                }}
                actionRef={actionRef}
                headerTitle={<FormattedMessage id='menu.list.service-instance-order-list' defaultMessage='服务实例订单列表'/>}
                options={{
                    search: false,
                    density: false,
                    fullScreen: true,
                    reload: () => {
                        actionRef.current?.reload();
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
                        </div>
                    ],
                }}
                columns={columns}
                request={fetchOrders}
                rowKey="key" pagination={false}/>
            <Pagination
                style={{marginTop: '16px', textAlign: 'right'}}
                current={currentPage}
                pageSize={pageSize}
                total={total}
                onChange={(page, pageSize) => {
                    handleGoToPage(page, currentPage, total, fetchOrders, setCurrentPage, actionRef, pageSize);
                }}
                showSizeChanger={false}
            />
        </PageContainer>
    );
}

