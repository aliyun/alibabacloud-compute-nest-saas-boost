import React from "react";
import { ProFormRadio } from "@ant-design/pro-form";
import { Space } from "antd";
import { PayChannelEnum } from "@/constants";
import { AlipayCircleOutlined, WechatOutlined } from "@ant-design/icons";
import { FormattedMessage } from "@@/exports";
import {useSelector} from "react-redux";
import {RootState} from "@/store/state";

const PayTypeFormItem: React.FC = () => {
    const payTypeEntries = Object.entries(PayChannelEnum);
    const alipayConfigured = useSelector((state: RootState) => state.paymentMethod.alipayConfigured);
    const wechatPayConfigured = useSelector((state: RootState) => state.paymentMethod.wechatPayConfigured);
    return (
        <ProFormRadio.Group
            label={<FormattedMessage id="label.payment-method" defaultMessage="支付方式" />}
            name="payChannel"
            initialValue={alipayConfigured? "ALIPAY" : "WECHATPAY"}
            rules={[{
                required: true,
                message: <FormattedMessage id="message.select-payment-method" defaultMessage="请选择支付方式" />
            }]}
            options={payTypeEntries.map(([key, value]) => ({
                label: (
                    <Space>
                        {alipayConfigured? key === 'ALIPAY' && <AlipayCircleOutlined style={{ color: '#009fe8' }} /> : null}
                        {wechatPayConfigured? key === 'WECHATPAY' && <WechatOutlined style={{ color: '#1AAD19' }} /> : null}
                        {value}
                    </Space>
                ),
                value: key,
            }))}
        />
    );
};
export default PayTypeFormItem;
