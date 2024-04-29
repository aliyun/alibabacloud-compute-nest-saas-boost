import React, {useEffect} from 'react';
import ProCard from '@ant-design/pro-card';
import {Avatar, Col, message, Modal, Row} from 'antd';
import styles from "@/pages/Service/component/css/service.module.css";
import {useSelector} from "react-redux";
import {RootState} from "@/store/state";
import WechatPayModal from "@/pages/PaymentMethod/WechatPay";
import AlipayModal from "@/pages/PaymentMethod/Alipay";
import { useNavigate } from 'react-router-dom';
import {centsToYuan} from "@/util/moneyUtil";
import {getOrder} from "@/services/backend/order";

interface PaymentModalProps {
    qrCodeURL: string;
    orderAmount: number;
    orderNumber: string;
    alipayAllConfigured: boolean;
    wechatPayAllConfigured: boolean;
    onClose: () => void;
    visible: boolean;
    activePaymentMethodKey: string;
    onPaymentMethodKeyChange: (key: string) => Promise<void>;
}

export const PaymentModal: React.FC<PaymentModalProps> = ({    qrCodeURL,
                                                       orderAmount,
                                                       orderNumber,
                                                       alipayAllConfigured,
                                                       wechatPayAllConfigured,
                                                       onClose,
                                                       visible,
                                                       activePaymentMethodKey,
                                                       onPaymentMethodKeyChange
                                                       }) => {

    const providerInfo = useSelector((state: RootState) => ({
        name: state.providerInfo.providerName,
        link: state.providerInfo.providerOfficialLink,
        description: state.providerInfo.providerDescription,
        logoUrl: state.providerInfo.providerLogoUrl,
    }));

    const navigate = useNavigate();

    const goToServiceInstance = () => {
        navigate('/serviceInstance');
    };

    useEffect(() => {
        const interval = setInterval(async () => {
            try {
                const orderResult = await getOrder({orderId: orderNumber});
                if(orderResult.code == "200" && orderResult.data?.tradeStatus.toString() === "TRADE_SUCCESS") {
                    message.success('支付成功，即将跳转...');
                    clearInterval(interval);
                    goToServiceInstance();
                }
            } catch (error) {
                message.error('检查支付状态时出错');
                clearInterval(interval);
            }
        }, 3000);

        return () => clearInterval(interval);
    }, []);

    return (
        <Modal
            title={
            <div>
                <Row align="middle">
                    <Col>
                        <Avatar size={36} src={providerInfo.logoUrl? providerInfo.logoUrl:'1'} shape="circle" className={styles.supplierImage}/>
                    </Col>
                    <Col>
                        <span style={{ fontSize: '18px', fontWeight: 'bold' }}>收银台</span>
                    </Col>
                </Row>
            </div>
        }
            open={visible}
            onCancel={onClose}
            footer={null}
            closable={true}
            centered
            style={{ textAlign: 'center' }}
        >
            <Row align="middle">
                <span style={{ fontSize: '16px', fontWeight: 'bold' }}>订单号: {orderNumber}</span>
            </Row>
            <Row align="middle">
                <span style={{ fontSize: '16px', fontWeight: 'bold' }}>订单金额: ¥{(orderAmount / 100).toFixed(2)}</span>
            </Row>

            {(alipayAllConfigured && wechatPayAllConfigured) ? (
                <ProCard
                    tabs={{
                        type: 'card',
                        activeKey: activePaymentMethodKey,
                        onChange: (key) => onPaymentMethodKeyChange(key),
                    }}
                >
                    <ProCard.TabPane key="ALIPAY" tab={<span style={{ fontSize: '16px', fontWeight: 'bold' }}>支付宝</span>}>
                        <AlipayModal qrCodeURL={qrCodeURL}/>
                    </ProCard.TabPane>
                    <ProCard.TabPane key="WECHATPAY" tab={<span style={{ fontSize: '16px', fontWeight: 'bold' }}>微信支付</span>}>
                        <WechatPayModal qrCodeURL={qrCodeURL}/>
                    </ProCard.TabPane>
                </ProCard>
            ) : (<ProCard>
                    {activePaymentMethodKey === 'ALIPAY'? <AlipayModal qrCodeURL={qrCodeURL}/>:null}
                    {activePaymentMethodKey === 'WECHATPAY'? <WechatPayModal qrCodeURL={qrCodeURL}/>:null}
                </ProCard>
            )}
        </Modal>
    );
};

interface RenewalModalProps {
    qrCodeURL: string;
    orderAmount: number;
    onClose: () => void;
    visible: boolean;
    activePaymentMethodKey: string;
}

export const RenewalModal: React.FC<RenewalModalProps> = ({    qrCodeURL,
                                                       orderAmount,
                                                       onClose,
                                                       visible,
                                                       activePaymentMethodKey
                                                   }) => {

    const providerInfo = useSelector((state: RootState) => ({
        name: state.providerInfo.providerName,
        link: state.providerInfo.providerOfficialLink,
        description: state.providerInfo.providerDescription,
        logoUrl: state.providerInfo.providerLogoUrl,
    }));

    return (
        <Modal
            title={
                <div>
                    <Row align="middle">
                        <Col>
                            <Avatar size={36} src={providerInfo.logoUrl? providerInfo.logoUrl:'1'} shape="circle" className={styles.supplierImage}/>
                        </Col>
                        <Col>
                            <span style={{ fontSize: '18px', fontWeight: 'bold' }}>收银台</span>
                        </Col>
                    </Row>
                </div>
            }
            open={visible}
            onCancel={onClose}
            footer={null}
            closable={true}
            centered
            style={{ textAlign: 'center' }}
        >
            <Row align="middle">
                <span style={{ fontSize: '16px', fontWeight: 'bold' }}>订单金额: ¥{centsToYuan(orderAmount)}</span>
            </Row>
            <ProCard>
                {activePaymentMethodKey === 'ALIPAY'? <AlipayModal qrCodeURL={qrCodeURL}/>:null}
                {activePaymentMethodKey === 'WECHATPAY'? <WechatPayModal qrCodeURL={qrCodeURL}/>:null}
            </ProCard>
        </Modal>
    );
};


