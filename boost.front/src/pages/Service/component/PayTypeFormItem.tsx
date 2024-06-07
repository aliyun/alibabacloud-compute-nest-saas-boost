import React from "react";
import { ProFormRadio } from "@ant-design/pro-form";
import { Space } from "antd";
import { PayChannelEnum } from "@/constants";
import { AlipayCircleOutlined, WechatOutlined } from "@ant-design/icons";
import { FormattedMessage } from "@@/exports";

const PayTypeFormItem: React.FC = () => {
    const payTypeEntries = Object.entries(PayChannelEnum);
    return (
        <ProFormRadio.Group
            label={<FormattedMessage id="label.payment-method" defaultMessage="支付方式" />}
            name="payChannel"
            initialValue="Alipay"
            rules={[{
                required: true,
                message: <FormattedMessage id="message.select-payment-method" defaultMessage="请选择支付方式" />
            }]}
            options={payTypeEntries.map(([key, value]) => ({
                label: (
                    <Space>
                        {key === 'ALIPAY' && <AlipayCircleOutlined style={{ color: '#009fe8' }} />}
                        {key === 'WECHATPAY' && <WechatOutlined style={{ color: '#1AAD19' }} />}
                        {value}
                    </Space>
                ),
                value: key,
            }))}
        />
    );
};
export default PayTypeFormItem;
