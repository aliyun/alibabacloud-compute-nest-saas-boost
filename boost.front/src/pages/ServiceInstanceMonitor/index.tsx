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
import {Space, Typography} from 'antd';
import ChartItem from "@/pages/ServiceInstanceMonitor/components/chartItem";

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

    useEffect(() => {
        const fetchMetricMetaData = async () => {
            try {
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

                // 过滤掉任何因错误而返回 null 的结果
                const validResults = results.filter(result => result !== null);

                if (validResults.length === 0) {
                    // 如果没有有效的结果，设置监控不可用
                    setMonitorAvailable(false);
                } else {
                    // 否则，处理并设置图表数据
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
                }
            } catch (error) {
                // 如果在获取监控元数据时出错，同样设置监控不可用
                setMonitorAvailable(false);
                console.error('Error fetching metric metadata:', error);
            }
        };
        fetchMetricMetaData();
    }, [serviceInstanceId, timeScope]);

    const hasData = listChartItem.every(item => !item.data || item.data === '');
    return (
        <div>
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
                        当前服务实例监控暂时不支持。
                    </Typography.Paragraph>
                )}
            </Space>
        </div>
    );
};

export default ServiceInstanceMonitor;
