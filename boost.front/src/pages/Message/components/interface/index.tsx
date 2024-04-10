import {renderToString} from "react-dom/server";
import {FormattedMessage} from "@@/exports";

export interface TimelineItem {
    message: string;
    timestamp: number; // 时间戳
}

export interface MessageItem {
    text: string;
    time: string;
}

export interface RefundDetail {
    refundReason: string;
    message: string;
}

export enum MessageTemplate {
    ORDER_CANCELLATION_REFUND = 'message.order-cancellation-refund',
    SERVICE_INSTANCE_DELETION_REFUND = 'message.service-instance-deletion-refund',
    SERVICE_INSTANCE_CREATION_FAILURE_REFUND = 'message.service-instance-creation-failure-refund',
    PAYMENT_SUCCESS = 'message.payment-success',
}

export function getMessageByTemplate(reason: string, ...args: (string | undefined)[]): string {
    if (!(reason in MessageTemplate)) {
        throw new Error(`<FormattedMessage id='message.unknown-message-template' defaultMessage='未知的消息模版：'/>${reason}`);
    }
    return renderToString(<FormattedMessage id={MessageTemplate[reason as keyof typeof MessageTemplate]} defaultMessage='未知的消息模版：'/>).replace(/%s/g, () => args.shift() || '');
}