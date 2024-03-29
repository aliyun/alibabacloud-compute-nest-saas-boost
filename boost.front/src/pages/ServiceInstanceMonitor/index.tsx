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

import React, {useState, useEffect} from 'react';
import {listMetricMetaDatas, listMetrics} from "@/services/backend/cloudMonitor";
import TimeItem from "@/pages/ServiceInstanceMonitor/components/timeItem";
import dayjs, {Dayjs} from "dayjs";
import {RangePickerProps} from "antd/es/date-picker";
import {Space} from 'antd';
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
        //console.log('forbackend', dayjs(defaultTimeScope[1]).valueOf().toString());
        const fetchMetricMetaData = async () => {
            const response: API.ListResultMetricMetaDataModel_ = await listMetricMetaDatas();
            const listMetricMetaData = response.data as API.MetricMetaDataModel[];
            console.log(listMetricMetaData);
            setDataCount(response.count as number);
            const promises = listMetricMetaData.map(it => {
                const listMetricParams: API.listMetricsParams = {
                    metricName: it.metricName,
                    serviceInstanceId: serviceInstanceId,
                    startTime: dayjs(timeScope[0]).valueOf().toString(),
                    endTime: dayjs(timeScope[1]).valueOf().toString()
                };
                return listMetrics(listMetricParams);
            })
            Promise.all(promises).then(results => {
                const listChartItemTemp = results.map((response, index) => {
                    const metricMetaData = listMetricMetaData[index];
                    let chartItem: ChartItemProps = {
                        title: metricMetaData.metricDescription as string,
                        data: response.data?.dataPoints as string,
                        statistics: metricMetaData.statistics as string[]
                    };
                    return chartItem;
                })
                setListChartItem(listChartItemTemp);
            })
        };
        fetchMetricMetaData();
    }, [serviceInstanceId, setDataCount, timeScope]);

    console.log(listChartItem);
    return (<div>
            <Space direction="vertical" size={20} style={{display: 'flex'}}>
                {<TimeItem
                    defaultStartValue={timeScope[0]}
                    defaultEndValue={timeScope[1]}
                    onOk={handleChange}
                    onChange={onChange}/>}
                <div style={{display: 'flex'}}>{
                    Array.from({length: dataCount}, (_, index) => (
                        <div key={index} style={{width: '33.3%'}}>
                            <div style={{marginRight: "16px"}}>
                                {
                                    listChartItem[index] &&
                                    <ChartItem title={listChartItem[index].title} data={listChartItem[index]?.data}
                                               statistics={listChartItem[index].statistics}/>
                                }
                            </div>
                        </div>
                    ))
                }</div>
            </Space>
        </div>
    );
};

export default ServiceInstanceMonitor;
