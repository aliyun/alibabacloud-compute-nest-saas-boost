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

import React, {useEffect, useState} from 'react';
import {listMetricMetaDatas, listMetrics} from "@/services/backend/cloudMonitor";
import TimeItem from "@/pages/ServiceInstanceMonitor/components/timeItem";
import dayjs, {Dayjs} from "dayjs";
import {RangePickerProps} from "antd/es/date-picker";
import {Space, Spin, Typography} from 'antd';
import ChartItem from "@/pages/ServiceInstanceMonitor/components/chartItem";
import {FormattedMessage} from "@@/exports";

interface ServiceInstanceMonitorProps {
    serviceInstanceId?: string;
}

interface ChartItemProps {
    title: string;
    data: string;
    statistics: string[];
}

const ServiceInstanceMonitor: React.FC<ServiceInstanceMonitorProps> = (props) => {
    const {serviceInstanceId} = props;
    const [dataCount, setDataCount] = useState<number>(0);
    const [listChartItem, setListChartItem] = useState<ChartItemProps[]>([]);
    //const defaultTimeScope = [dayjs().subtract(1, "hour"), dayjs()];
    const [timeScope, setTimeScope] = useState<[Dayjs, Dayjs]>([dayjs().subtract(1, "hour"), dayjs()]);
    const [monitorAvailable, setMonitorAvailable] = useState(true);
    const [isLoading, setIsLoading] = useState(true); // 默认为 true 表示正在加载

    const onChange = (
        value?: RangePickerProps['value'],
        dateString?: [string, string] | string,
    ) => {
        console.log('Selected Time: ', value);
        console.log('Formatted Selected Time: ', dateString);
        if (dateString && (dateString[0] != '')) {
            setTimeScope([dayjs(dateString[0]), dayjs(dateString[1])]);
        }
    }

    const handleChange = (value?: RangePickerProps['value']) => {
        value && console.log('timeValue: ', value[0] ?? '');
    };

    const LoadingIndicator = () => {
        return (
            <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Spin size="large" tip="加载中..." />
            </div>
        );
    };

    useEffect(() => {
        const fetchMetricMetaData = async () => {
            try {
                setIsLoading(true);
                const response = await listMetricMetaDatas();
                const listMetricMetaData = response.data as API.MetricMetaDataModel[];
                setDataCount(response.count as number);

                const promises = listMetricMetaData.map(it => {
                    const listMetricParams: API.listMetricsParams = {
                        metricName: it.metricName,
                        serviceInstanceId: serviceInstanceId,
                        startTime: dayjs(timeScope[0]).valueOf().toString(),
                        endTime: dayjs(timeScope[1]).valueOf().toString(),
                    };
                    return listMetrics(listMetricParams).catch(() => null); // 捕获并忽略错误
                });

                const results = await Promise.all(promises);
                const validResults = results.filter(result => result !== null);

                if (validResults.length === 0) {
                    setMonitorAvailable(false);
                } else {
                    const listChartItemTemp = validResults.map((response, index) => {
                        const metricMetaData = listMetricMetaData[index];
                        let chartItem: ChartItemProps = {
                            title: metricMetaData.metricDescription as string,
                            data: response?.data?.dataPoints as string,
                            statistics: metricMetaData.statistics as string[],
                        };

                        return chartItem;
                    });
                    setListChartItem(listChartItemTemp);
                    setIsLoading(false);
                }
            } catch (error) {
                setIsLoading(false);
                setMonitorAvailable(false);
                console.error('Error fetching metric metadata:', error);
            }
        };
        fetchMetricMetaData();
    }, [serviceInstanceId, timeScope]);

    const hasData = listChartItem.every(item => !item.data || item.data === '');
    return (
        <div>
            {isLoading ? (
                    <LoadingIndicator />
            ) :
            <Space direction="vertical" size={20} style={{display: 'flex'}}>

                {!hasData ? (
                    <>
                        <TimeItem
                            defaultStartValue={timeScope[0]}
                            defaultEndValue={timeScope[1]}
                            onOk={onChange}
                            onChange={onChange}
                        />
                        <div style={{display: 'flex'}}>
                            {listChartItem.map((item, index) => (
                                <div key={index} style={{width: '33.3%'}}>
                                    <div style={{marginRight: "16px"}}>
                                        <ChartItem title={item.title} data={item.data} statistics={item.statistics}/>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </>
                ) : (
                    <Typography.Paragraph type="secondary">
                        <FormattedMessage id='message.monitoring-not-supported' defaultMessage="当前服务实例监控暂时不支持。"/>
                    </Typography.Paragraph>
                )}
            </Space>}
        </div>
    );
};

export default ServiceInstanceMonitor;
