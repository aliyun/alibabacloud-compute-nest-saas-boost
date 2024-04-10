import React, {useEffect, useState} from 'react';
import {listOrders} from "@/services/backend/order";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import moment from "moment";
import {PageContainer} from "@ant-design/pro-layout";
import ProCard from "@ant-design/pro-card";
import {Button, Timeline, Typography} from "antd";
import styles from "./components/css/message.module.css"
import {TIME_FORMAT} from "@/constants";
import {getMessageByTemplate, MessageItem, MessageTemplate, RefundDetail} from "@/pages/Message/components/interface";
import {FormattedMessage} from "@@/exports";
import {centsToYuan} from "@/util/moneyUtil";
dayjs.extend(utc);

const Message:React.FC = () => {
    const [expanded, setExpanded] = useState(false);
    const [message, setMessage] = useState<MessageItem[]>([]);
    const PAGE_SIZE = 10;
    const displayItems = expanded ? message : message.slice(0, PAGE_SIZE);
    const parseRefundDetail = (refundDetail: string): RefundDetail => {
        try {
            return JSON.parse(refundDetail);
        } catch (error) {
            console.error("Error parsing refundDetail JSON:", error);
            // 返回一个默认值或处理错误
            return { refundReason: "", message: "" };
        }
    };

    useEffect(()=>{
        const getOrders = async () => {
            let response:API.ListResultOrderDTO_ = await listOrders({
                tradeStatus:["TRADE_SUCCESS", 'REFUNDED','REFUNDING'],
                startTime:moment().subtract(3, 'month').format(TIME_FORMAT).toString(),
                endTime:moment().format(TIME_FORMAT).toString()
            });
            let tempMessages:MessageItem[] = [];
            if(response.data){
                response.data.map((item)=>{
                    console.log(MessageTemplate.PAYMENT_SUCCESS);
                    if (item.gmtPayment != undefined) {
                        const successMessage = getMessageByTemplate("PAYMENT_SUCCESS", item.orderId, item.totalAmount ? centsToYuan(item.totalAmount.toString()) : "");
                        tempMessages.push({
                            text: successMessage,
                            time: item.gmtPayment
                        } as MessageItem);
                    }
                    if (item.refundDetail != undefined) {
                        const refundDetail = parseRefundDetail(item.refundDetail);
                        let refundMessage = "";
                        if (refundDetail.refundReason == "SERVICE_INSTANCE_DELETION_REFUND") {
                            refundMessage = getMessageByTemplate(refundDetail.refundReason,item.serviceInstanceId, item.orderId, item.refundAmount ? centsToYuan(item.refundAmount.toString()) : "");
                        } else {
                            refundMessage = getMessageByTemplate(refundDetail.refundReason, item.orderId, item.refundAmount ? centsToYuan(item.refundAmount.toString()) : "");
                        }
                        tempMessages.push({
                            text: refundMessage,
                            time: item.refundDate
                        } as MessageItem);
                    }
                });
                tempMessages.sort((a, b) => moment(b.time).valueOf() - moment(a.time).valueOf());
                setMessage(tempMessages);
            }
        };
        getOrders();
    }, []);

    const toggleExpanded = () => {
        setExpanded(!expanded);
    };

    return (
        <PageContainer title={<FormattedMessage id='menu.messages' defaultMessage='消息'/>}>
            <ProCard   bodyStyle={{
                padding: '24px',
                paddingBottom: '0px',
                marginBottom: '0px',
            }}>
                <Timeline>
                    {displayItems.map((item, index) => (
                        <Timeline.Item key={index}>
                            <Typography.Text className={styles.message}>
                                {item.text}{' '}
                                <Typography.Text type="secondary">
                                    {moment(item.time).toString()}
                                </Typography.Text>
                            </Typography.Text>
                        </Timeline.Item>
                    ))}
                </Timeline>
                {message.length > PAGE_SIZE && (
                    <Button
                        type="link"
                        onClick={toggleExpanded}
                        style={{ marginTop: 16, marginLeft: 'calc(50% - 50px)' }} // 调整按钮位置
                    >
                        {expanded ? <FormattedMessage id='button.collapse' defaultMessage='收起'/> : <FormattedMessage id='button.view-more' defaultMessage='查看更多'/>}
                    </Button>
                )}
            </ProCard>
        </PageContainer>
    );
};
export default Message;