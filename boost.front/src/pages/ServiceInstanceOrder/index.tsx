import React, {useEffect, useRef, useState} from "react";
import moment from "moment";
import {listOrders, refundOrder} from "@/services/backend/order";
import {OrderColumns, TradeStatusEnum} from "@/pages/Order/common";
import {PageContainer} from "@ant-design/pro-layout";
import {ProTable} from "@ant-design/pro-components";
import {Button, message, Modal, Pagination, Typography} from "antd";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {ModalForm, ProFormInstance} from "@ant-design/pro-form";

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
    const [canRefundOrderIndex, setCanRefundOrderIndex] = useState<number | undefined>(undefined);
    const {Paragraph} = Typography;
    const actionRef = useRef<ActionType>();
    const [isPayModalVisible, setPayModalVisible] = useState(false);
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

    useEffect(() => {
    }, [canRefundOrderIndex]);

    const handleConfirmRefund = async (): Promise<void> => {
        try {
            if (refundOrderId) {
                await refundOrder({orderId: refundOrderId, dryRun: false});
                setRefundOrderId(null);
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


    const PayModalForm: React.FC<{
        visible: boolean;
        onVisibleChange: (visible: boolean) => void;
        onFinish: (values: any) => Promise<void>;
    }> = ({visible, onVisibleChange, onFinish}) => {
        return (
            <ModalForm
                title="选择支付方式"
                open={visible}
                onOpenChange={onVisibleChange}
                onFinish={onFinish}
                formRef={form}
                layout={'horizontal'}
                width={400}
            >
                <PayTypeFormItem/>
            </ModalForm>
        );
    };

    const handlePaySubmitButton = async (record: any) => {
        setPayModalVisible(true);
        setCurrentOrder(record);
    }

    const handleCreateTransaction = async () => {

        if (currentOrder) {
            try {
                // console.log(form.current?.getFieldsValue());
                console.log(currentOrder);
                const {payChannel} = await form.current?.getFieldsValue();
                console.log(payChannel);
                const transactionResult: API.BaseResultString_ = await createTransaction({
                    orderId: currentOrder.orderId,
                    payChannel: payChannel

                });
                if (transactionResult.code == "200" && transactionResult.data != undefined) {
                    await handlePaySubmit(transactionResult.data);
                }

            } catch (error) {
                console.error(error);
                message.error('交易创建失败');
            }
        }
        setPayModalVisible(false);
    };


    const handleModalClose = () => {
        setVisible(false);
    };

    const columns = OrderColumns.concat([
        {
            title: '操作',
            key: 'action',
            sorter: false,
            search: false,
            // @ts-ignore
            render: (text?: string, record?: any, index) => {
                if (record.tradeStatus == TradeStatusEnum.WAIT_BUYER_PAY) {

                    const payButton = (<a className={styles.payButton} onClick={() => handlePaySubmitButton(record)}>
                        支付
                    </a>);
                    return (
                        <>                        {payButton}
                        </>
                    );
                }
                if (props.serviceType == "managed" && props.serviceInstanceId != undefined && canRefundOrderIndex == index) {
                    const refundButton = (
                        <a className={styles.refundButton} onClick={() => handleButtonClick(record)}>
                            退款
                        </a>
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
    ])
    return (
        <PageContainer title={props.serviceInstanceId}>

            <PayModalForm
                visible={isPayModalVisible}
                onVisibleChange={setPayModalVisible}
                onFinish={handleCreateTransaction}
            />
            <ProTable
                onSubmit={(values) => {
                    // @ts-ignore
                    setFilterValues(values);
                    actionRef.current?.reload();
                }}
                actionRef={actionRef}
                headerTitle={'服务实例订单列表'}
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

