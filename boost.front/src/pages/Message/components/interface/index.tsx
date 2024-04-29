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
    ORDER_CANCELLATION_REFUND = "订单退款，订单编号：%s，金额：%s元。",
    SERVICE_INSTANCE_DELETION_REFUND = "服务实例删除，服务实例 ID：%s，订单编号：%s，当前订单退款金额：%s元。",
    SERVICE_INSTANCE_CREATION_FAILURE_REFUND = "服务实例创建失败，订单编号：%s，当前订单退款金额：%s元。",
    PAYMENT_SUCCESS = "订单编号：%s 支付成功，金额：%s元。",
}

export function getMessageByTemplate(reason: string, ...args: (string | undefined)[]): string {
    if (!(reason in MessageTemplate)) {
        throw new Error(`未知的消息模版：${reason}`);
    }
    return MessageTemplate[reason as keyof typeof MessageTemplate].replace(/%s/g, () => args.shift() || '');
}