import React, {useEffect, useRef, useState} from 'react';
import {message, Modal, Pagination, Tooltip} from 'antd';
import SpecificationModal from "@/pages/Commodity/components/SpeicificationModal";
import {commodityColumns, CommodityForm} from "@/pages/Commodity/constants";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import {PlusOutlined} from "@ant-design/icons";
import {PageContainer} from "@ant-design/pro-layout";
import {ActionType} from "@ant-design/pro-table/lib";
import {FetchResult, handleGoToPage} from "@/util/nextTokenUtil";
import {yuanToCents} from "@/util/moneyUtil";
import {createCommodity, deleteCommodity, listAllCommodities, updateCommodity} from "@/services/backend/commodity";
import {FormattedMessage} from "@@/exports";
import {renderToString} from "react-dom/server";

const CommodityList: React.FC = () => {
    const [isCommodityModalVisible, setIsCommodityModalVisible] = useState(false);
    const [isSpecificationModalVisible, setIsSpecificationModalVisible] = useState(false);
    const [selectedCommodity, setSelectedCommodity] = useState<API.CommodityDTO | undefined>(undefined);
    const defaultPageSize = 10;
    const [total, setTotal] = useState<number>(0);
    const [nextTokens, setNextTokens] = useState<(string | undefined)[]>([undefined]);
    const [currentPage, setCurrentPage] = useState<number>(1);
    const actionRef = useRef<ActionType>();


    const handleDelete = async (commodityCode: string) => {
        Modal.confirm({
            title: <FormattedMessage id="message.confirm-delete-commodity" defaultMessage='确定删除商品?'/>,
            content: <FormattedMessage id="message.delete-commodity-warning" defaultMessage='删除商品将同时删除所有关联的套餐，此操作不可恢复。您确定要继续吗？'/>,
            okText: <FormattedMessage id="button.ok" defaultMessage='确定'/>,
            okType: 'danger',
            cancelText: <FormattedMessage id="button.cancel" defaultMessage='取消'/>,
            onOk: async () => {
                try {
                    await deleteCommodity({commodityCode});
                    message.success(<FormattedMessage id="message.commodity-deleted-successfully" defaultMessage='商品删除成功'/>);
                    actionRef.current?.reload();
                } catch (error) {
                    message.error(<FormattedMessage id="message.commodity-deletion-failed" defaultMessage='商品删除失败'/>);
                }
            },
        });
    };

    const fetchCommodities = async (params: {
        pageSize: number;
        current: number;
        [key: string]: any;
    }): Promise<FetchResult<API.CommodityDTO>> => {
        const param: API.listAllCommoditiesParams = {
            maxResults: params.pageSize,
            nextToken: nextTokens[params.current - 1],
        };

        try {
            const result: API.ListResultCommodityDTO_ = await listAllCommodities(param);
            if (result.data !== undefined) {
                setTotal(result.count || 0);
                nextTokens[params.current] = result.nextToken;
                const convertedData: API.CommodityDTO[] = result.data.map((commodity) => {
                    const unitPriceInYuan = commodity.unitPrice !== undefined
                        ? (Number((commodity.unitPrice / 100).toFixed(2)))
                        : undefined;
                    return {...commodity, unitPrice: unitPriceInYuan};
                });

                return {
                    data: convertedData,
                    success: true,
                    total: result.count || 0,
                };


            }
        } catch (error) {
            message.error(<FormattedMessage id="message.failed-to-fetch-commodities" defaultMessage='未能获取商品'/>);
        }
        return {
            data: [],
            success: false,
            total: 0,
        };
    };

    const handleUpdateCommodityStatus = async (commodity: API.CommodityDTO, commodityStatus: "DRAFT" | "ONLINE") => {
        commodity.commodityStatus = commodityStatus;
        await handleSaveCommodity(commodity);
    }

    const handleSaveCommodity = async (values: API.CommodityDTO) => {
        console.log(values);
        let payPeriodsParsed;
        if (typeof values.payPeriods === 'string') {
            try {
                payPeriodsParsed = JSON.parse(values.payPeriods);
            } catch (error) {
                console.error('Parsing payPeriods failed', error);
                payPeriodsParsed = [];
            }
        } else {
            payPeriodsParsed = values.payPeriods;
        }

        if (values.commodityCode) {
            try {
                await updateCommodity({
                    commodityCode: values.commodityCode?.trim(),
                    unitPrice: yuanToCents(values.unitPrice),
                    serviceId: values.serviceId?.trim(),
                    commodityName: values.commodityName?.trim(),
                    commodityStatus: values.commodityStatus,
                    description: values.description,
                    //@ts-ignore
                    payPeriods: payPeriodsParsed,
                    //@ts-ignore
                    payPeriodUnit: values.payPeriodUnit,
                    serviceVersion: values.serviceVersion?.trim(),
                });
                message.success(<FormattedMessage id="message.commodity-updated-successfully" defaultMessage='商品更新成功'/>);
            } catch (error) {
                message.error(<FormattedMessage id="message.commodity-update-failed" defaultMessage='商品更新失败'/>);
            }
        } else {
            try {
                await createCommodity({
                    commodityName: values.commodityName?.trim(),
                    unitPrice: yuanToCents(values.unitPrice),
                    chargeType: values.chargeType,
                    serviceId: values.serviceId,
                    commodityStatus: values.commodityStatus,
                    description: values.description,
                    //@ts-ignore
                    payPeriods: payPeriodsParsed,
                    //@ts-ignore
                    payPeriodUnit: values.payPeriodUnit,
                    serviceVersion: values.serviceVersion?.trim(),
                });
                message.success(<FormattedMessage id="message.commodity-created-successfully" defaultMessage='商品新建成功'/>);
            } catch (error) {
                console.log(error);
                message.error(<FormattedMessage id="message.commodity-creation-failed" defaultMessage='商品新建失败'/>);
            }
        }
        setIsCommodityModalVisible(false);
        setSelectedCommodity(undefined);
        setTimeout(() => {
            actionRef.current?.reload();
        }, 1500);
    };

    const handleSpecifications = (commodity: API.CommodityDTO) => {
        setSelectedCommodity(commodity);
        setIsSpecificationModalVisible(true);
    };

    const actionColumn: ProColumns<API.CommodityDTO> = {
        title: <FormattedMessage id="pages.instanceSearchTable.titleOption" defaultMessage='操作'/>,
        dataIndex: 'action',
        key: 'action',
        valueType: 'option',
        render: (text, record, _, action) => [

            record.commodityStatus === 'ONLINE' ?
                <a onClick={() => handleUpdateCommodityStatus(record, 'DRAFT')}><FormattedMessage id="button.offline" defaultMessage='下线'/></a>
                :
                <a onClick={() => handleUpdateCommodityStatus(record, 'ONLINE')}><FormattedMessage id="button.online" defaultMessage='上线'/></a>
            ,

            <a type="link" onClick={() => {
                setSelectedCommodity(record);
                setIsCommodityModalVisible(true);
            }}><FormattedMessage id="button.edit" defaultMessage='编辑'/></a>,

            <a type="link" onClick={() => {
                if (record.commodityCode) {
                    handleDelete(record.commodityCode);
                }
            }} style={{color: 'red'}}><FormattedMessage id="button.delete" defaultMessage='删除'/></a>,

            <a type="link" onClick={() => handleSpecifications(record)}><FormattedMessage id="button.manage-specifications" defaultMessage='管理套餐'/></a>

        ],
    };

    const columns: ProColumns<API.CommodityDTO>[] = [
        ...commodityColumns,
        actionColumn,
    ];

    // @ts-ignore
    return (
        <><PageContainer title={<FormattedMessage id="menu.commodity" defaultMessage='商品'/>}>
            <ProTable columns={columns} rowKey="commodityCode"
                      headerTitle={<FormattedMessage id="menu.commodity.commodity-management" defaultMessage="商品管理"/>}
                      actionRef={actionRef}
                      pagination={false}
                      toolBarRender={() => [
                          <Tooltip key="add" title="新增商品">
                              <a
                                  key="add"
                                  onClick={() => {
                                      setSelectedCommodity(undefined);
                                      setIsCommodityModalVisible(true);
                                  }}
                                  style={{color: 'inherit'}}
                              >
                                  <PlusOutlined/>
                                  <span> <FormattedMessage id="button.add" defaultMessage="新增"/></span>
                              </a>
                          </Tooltip>
                      ]}
                      request={async (params, sorter, filters) => {
                          const pageSize = params.pageSize ?? defaultPageSize;
                          const currentPage = params.current ?? 1;
                          return fetchCommodities({
                              pageSize,
                              current: currentPage
                          });
                      }}
                      options={{
                          search: false,
                          density: false,
                          fullScreen: false,
                          reload: true,
                          setting: false
                      }}
                      search={false}
            />

            <Modal
                title={selectedCommodity ? '编辑商品' : '新建商品'}
                open={isCommodityModalVisible}
                onCancel={() => {
                    setIsCommodityModalVisible(false);
                    setSelectedCommodity(undefined);
                }}

                footer={null}
            >
                {isCommodityModalVisible && (
                    <CommodityForm  onSubmit={handleSaveCommodity}
                                   key={selectedCommodity ? selectedCommodity.commodityCode : 'new'}
                                   commodity={{
                                       ...selectedCommodity,
                                       payPeriods: selectedCommodity?.payPeriods ? JSON.parse(selectedCommodity.payPeriods) : [],
                                   }}
                                   onCancel={() => {
                                       setIsCommodityModalVisible(false);
                                       setSelectedCommodity(undefined);
                                   }}
                    />
                )}
            </Modal>

            <Modal
                title="Manage Specifications"
                open={isSpecificationModalVisible}
                onCancel={() => setIsSpecificationModalVisible(false)}
                destroyOnClose={true}
                footer={null}
            >
                {isSpecificationModalVisible && selectedCommodity && (
                    <SpecificationModal commodity={selectedCommodity} onClose={() => {
                        setIsSpecificationModalVisible(false);
                    }} visible={isSpecificationModalVisible}
                    />
                )}
            </Modal>

            <Pagination
                style={{marginTop: '16px', textAlign: 'right'}}
                current={currentPage}
                pageSize={defaultPageSize}
                total={total}
                onChange={(page, pageSize) => {
                    handleGoToPage(page, currentPage, total, fetchCommodities, setCurrentPage, actionRef, pageSize);
                }}
                showSizeChanger={false}
            />
        </PageContainer>
        </>
    );
};

export default CommodityList;
