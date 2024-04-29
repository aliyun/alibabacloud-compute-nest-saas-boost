import React from 'react';
import ProCard from '@ant-design/pro-card';
import {Avatar, Button, Col, Modal, Row} from 'antd';
import styles from "@/pages/Service/component/css/service.module.css";
import {useSelector} from "react-redux";
import {RootState} from "@/store/state";
import WechatPayModal from "@/pages/PaymentMethod/WechatPay";
import AlipayModal from "@/pages/PaymentMethod/Alipay";
import { useNavigate } from 'react-router-dom';
import {centsToYuan} from "@/util/moneyUtil";

interface PaymentModalProps {
    qrCodeURL: string;
    orderAmount: number;
    orderNumber: string;
    onClose: () => void;
    visible: boolean;
    activePaymentMethodKey: string;
    onPaymentMethodKeyChange: (key: string) => Promise<void>;
}

export const PaymentModal: React.FC<PaymentModalProps> = ({    qrCodeURL,
                                                       orderAmount,
                                                       orderNumber,
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
            <Row justify="end" style={{ marginTop: '24px' }}>
                <Button type="primary" onClick={goToServiceInstance}>
                    已支付
                </Button>
            </Row>
        </Modal>
    );
};

interface RenewalModalProps {
    qrCodeURL: string;
    orderAmount: number;
    orderNumber: string;
    onClose: () => void;
    visible: boolean;
    activePaymentMethodKey: string;
}

export const RenewalModal: React.FC<RenewalModalProps> = ({    qrCodeURL,
                                                       orderAmount,
                                                       orderNumber,
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


