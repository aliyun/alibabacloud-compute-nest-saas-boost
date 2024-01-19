import {Col, Divider, Row} from "antd";
import ProCard from "@ant-design/pro-card";
import styles from ".//component/css/doc.module.css";
import React from "react";
import {additionalLinks, links} from "@/pages/Doc/common";
import {PageContainer} from "@ant-design/pro-layout";

const DocPage : React.FC = ()=> {

    return (
        <><PageContainer title={"文档"}>
            {links.map((link, index) => (
                <ProCard key={index} bordered headerBordered={true} className={styles.proCard}>
                    <Row justify="start" align="middle">
                        <Col>{link.title}
                            <Divider type={"vertical"}  className={styles.dividerLine}/>
                        </Col>
                        <Col>
                            <a href={link.href} target="_blank" rel="noopener noreferrer" className={styles.introduction}>
                                {link.name}
                            </a>
                        </Col>
                    </Row>
                </ProCard>
            ))}
            <ProCard bordered headerBordered={true} className={styles.additionalProCard}>
                <Row justify="start" align="middle">
                    <Col>
                        产品购买
                    </Col>
                    <Col>
                        <Divider type="vertical" className={styles.dividerLine}/>
                    </Col>
                    <Col>
                        <a href={additionalLinks[0].href} target="_blank" rel="noopener noreferrer"
                           style={{ marginLeft: '60px' }}>{additionalLinks[0].name}</a>
                        {' '}
                        <Divider type="vertical" className={styles.hrefDividerLine}/>
                    </Col>
                    <Col>
                        <a href={additionalLinks[1].href} target="_blank" rel="noopener noreferrer">{additionalLinks[1].name}</a>
                    </Col>
                </Row>
                <Row justify="start" align="middle" style={{ marginTop: '16px' }}>
                    <Col offset={1}>
                        <a href={additionalLinks[2].href} target="_blank" rel="noopener noreferrer"
                           style={{ marginLeft: '140px' }}>{additionalLinks[2].name}</a>
                    </Col>
                </Row>
            </ProCard>
        </PageContainer>
        </>
    );
}

export default DocPage;