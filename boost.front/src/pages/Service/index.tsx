import React, {useState} from 'react';
import ProCard from '@ant-design/pro-card';
import CreateModal from "@/pages/Service/component/PayForm";
import {Avatar, Button, Col, Row, Space, Typography} from "antd";
import styles from "./component/css/service.module.css";
import {PageContainer} from "@ant-design/pro-layout";
import {
    companyDescription,
    companyTitle,
    companyWebsiteUrl,
    contactEmail,
    featuredServiceDescription1,
    featuredServiceDescription2,
    featuredServiceTitle
} from './common';
import profileImage from '../../../public/logo.png'
import serviceImage from '../../../public/saas-boost.svg'
import {GlobalOutlined, MailOutlined} from "@ant-design/icons";

const { Paragraph } = Typography;
const ServicePage: React.FC = () => {
    const [createModalVisible, setCreateModalVisible] = useState(false);
    return (
        <PageContainer title={"精选服务"}>
            <ProCard bordered={true} className={styles.supplierProCard}>
                <div>
                    <Row align="middle">
                        <Col>
                            <Avatar size={64} src={profileImage} shape="circle" className={styles.supplierImage}/>
                        </Col>
                        <Col flex="auto" className={styles.imageTitleGap}>
                            <div className={styles.supplierTitle}>{companyTitle}</div>

                            <a href={companyWebsiteUrl} target="_blank" rel="noopener noreferrer"
                               className={styles.supplierDetails}><GlobalOutlined className={styles.globalOutlined} />{companyWebsiteUrl}</a>

                            <a href={`mailto:${contactEmail}`} className={styles.supplierDetails}><MailOutlined className={styles.mailOutlined}/>{contactEmail}</a>
                        </Col>
                    </Row>
                    <Paragraph/>
                    <Paragraph className={styles.supplierDescription}>{companyDescription}</Paragraph>
                    <Paragraph/>
                </div>
            </ProCard>
            <ProCard bordered={true} title={"服务介绍"} headerBordered={true}>
                <div>
                    <Row align="middle">
                        <Col>
                            <Avatar size={64} src={serviceImage} shape="circle" className={styles.supplierImage}/>
                        </Col>
                        <Col flex="auto" className={styles.imageTitleGap} >
                            <div className={styles.serviceName}>{featuredServiceTitle}</div>
                        </Col>
                    </Row>
                    <Space direction="vertical" size="middle">
                        <Paragraph/>
                        <Paragraph className={styles.serviceDescription}>{featuredServiceDescription1}</Paragraph>
                        <Paragraph className={styles.serviceDescription}>{featuredServiceDescription2}</Paragraph>
                        <Paragraph/>
                    </Space>
                </div>
                <Button type="primary" onClick={() => {
                    setCreateModalVisible(true)
                }} key="create">
                    点击购买
                </Button>
                <CreateModal
                    createModalVisible={createModalVisible}
                    setCreateModalVisible={setCreateModalVisible}
                    handleCreateSubmit={() => {
                        return;
                    }}
                />
            </ProCard>
        </PageContainer>
    );
};

export default ServicePage;
