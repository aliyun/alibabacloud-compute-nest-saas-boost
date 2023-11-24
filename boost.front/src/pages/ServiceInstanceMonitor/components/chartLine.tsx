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

import React from "react";
import {Line} from "@ant-design/plots";

interface ChartData {
    time: string;
    value: number;
    category: string;
}

interface DataProps {
    //Datapoints的json串
    data: string;
    statistics: string[];
}

function parseDataPoints(dataPointsStr: string): object[] {
    try {
        const dataPoints = JSON.parse(dataPointsStr);
        return dataPoints;
    } catch (error) {
        console.error('Error parsing datapoints:', error);
        return [];
    }
}

function timestampToCommon(timestamp: number): string {
    //timestamp unit:ms
    const datems = new Date(timestamp);
    const year = datems.getFullYear();
    const month = ('0' + (datems.getMonth() + 1)).slice(-2); // 月份是从0开始计数的，所以需要加1
    const day = ('0' + datems.getDate()).slice(-2);
    const hours = ('0' + datems.getHours()).slice(-2);
    const minutes = ('0' + datems.getMinutes()).slice(-2);
    const seconds = ('0' + datems.getSeconds()).slice(-2);

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

//创建ChartData变量
function newChartData(dataPointMap: Map<string, any>, property: string): ChartData {
    return {
        time: timestampToCommon(dataPointMap.get("timestamp")),
        value: dataPointMap.get(property),
        category: property
    };
}

function formatFrontData(props: DataProps): ChartData[] {
    const dataPoints = parseDataPoints(props.data);
    const statistics = props.statistics;
    let frontEndData: ChartData[] = [];
    if (dataPoints.length == 0) {
        //解析什么都没有就什么都不做
    } else {
        dataPoints.forEach((dataPoint) => {
            const dataPointMap = new Map(Object.entries(dataPoint));
            statistics.forEach((property) => {
                frontEndData.push(newChartData(dataPointMap, property));
            })
        });
    }
    return frontEndData;
}


const ChartData: React.FC<DataProps> = (props) => {
    //frontEndData符合前端格式的数据
    const data: ChartData[] = formatFrontData(props);
    const COLOR_PLATE_10 = [
        '#5B8FF9',
        '#5AD8A6',
        '#DA7F3EFF',
        '#9270CA',
        '#5D7092',
        '#F6BD16',
        '#E8684A',
        '#6DC8EC',
        '#269A99',
        '#FF99C3',
    ];

    const config = {
        data,
        xField: 'time',
        yField: 'value',
        seriesField: 'category',
        color: COLOR_PLATE_10,
        xAxis: {
            label: {
                //去掉年月日，只保留时分
                formatter: (time: string) => time.split(' ')[1].slice(0,5)
            }
        }
    };
    return (<Line style={{width: '95%', margin: '0 auto'}} {...config} />);
};

export default ChartData;
