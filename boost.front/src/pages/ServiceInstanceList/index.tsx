/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

import {PageContainer,} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {listServiceInstances} from "@/services/backend/serviceInstance";
import {getListColumns} from "@/pages/ServiceInstance/common";
import CreateModal from "@/pages/ServiceInstanceList/components/form/AlipayForm";
import {ServiceInstanceTableInterface} from "@/pages/ServiceInstanceList/components/interface/ServiceInstanceTableInterface";
import {Pagination} from "antd";
import {handleGoToPage} from "@/nextTokenUtil";

const ServiceInstanceList: React.FC = () => {
    const [serviceInstances, setServiceInstances] = useState<API.ServiceInstanceModel[]>([]);
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [total, setTotal] = useState<number>(0);
    const pageSize = 10;
    const [updateUserPasswordOpen, handleUpdateUserPasswordOpen] = useState<boolean>(false);
    const [createModalVisible, setCreateModalVisible] = useState(false);
    const [currentRow, setCurrentRow] = useState<API.ServiceInstanceModel>();
    const [filterValues, setFilterValues] = useState<{
        status?: string;
        serviceInstanceId?: string;
        serviceInstanceName?: string;
    }>({});
    const [shouldFetchData, setShouldFetchData] = useState(false);
    const [nextTokens, setNextTokens] = useState<(string | undefined)[]>([undefined]);

    const fetchData = async (currentPage: number, show: boolean) => {
        let serviceInstances: API.ServiceInstanceModel[] = [];
        const response: API.ListResultServiceInstanceModel_ = await listServiceInstances({
            maxResults: pageSize,
            nextToken: nextTokens[currentPage - 1],
            ...filterValues,
        });
        console.log(response.data);
        response.data?.map((item: API.ServiceInstanceModel) => {
            serviceInstances.push(item);
        })
        if (response.data !== undefined) {
            nextTokens[currentPage] = response.nextToken;
        }
        setTotal(serviceInstances.length);
        if (show) {
            //@ts-ignored
            setServiceInstances(serviceInstances);
            setShouldFetchData(false);
        }

    };

    useEffect(() => {
        fetchData(currentPage, true);
    }, [currentPage, shouldFetchData]);

    return (
        <PageContainer>
            <ServiceInstanceTableInterface
                search={true}
                serviceInstances={serviceInstances}
                columns={getListColumns()}
                onUpdateUserPassword={(record: API.ServiceInstanceModel) => {
                    handleUpdateUserPasswordOpen(true);
                    setCurrentRow(record);
                }}
                onCreate={() => {setCreateModalVisible(true);
                }}
                onRefresh={() => setShouldFetchData(true)}
                onSubmit={(values) => {
                    setFilterValues(values);
                    setShouldFetchData(true);
                }}
                options={{
                    search: false,
                    density: false,
                    fullScreen: false,
                    reload: false,
                    setting: false,
                }}
            />

            <CreateModal
                createModalVisible={createModalVisible}
                setCreateModalVisible={setCreateModalVisible}
            />

            <Pagination
                style={{marginTop: '16px', textAlign: 'right'}}
                current={currentPage}
                pageSize={pageSize}
                total={total}
                onChange={(page, pageSize) => handleGoToPage(page, currentPage, total, fetchData, setCurrentPage)}
                prevIcon={<span
                    onClick={() => handleGoToPage(currentPage + 1, currentPage, total, fetchData, setCurrentPage)}>上一页</span>}
                nextIcon={<span
                    onClick={() => handleGoToPage(currentPage - 1, currentPage, total, fetchData, setCurrentPage)}>下一页</span>}
                showSizeChanger={false}
            />
        </PageContainer>
    );
};
export default ServiceInstanceList;
