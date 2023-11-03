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

import React, {useState} from 'react';
import {DatePicker, Space} from 'antd';
import type {DatePickerProps, RangePickerProps} from 'antd/es/date-picker';
import dayjs, {Dayjs} from "dayjs";
import {index} from "@umijs/utils/compiled/cheerio/lib/api/traversing";

const {RangePicker} = DatePicker;

interface TimeItemProps {
    defaultStartValue: Dayjs;
    defaultEndValue: Dayjs;
    onOk: (value: RangePickerProps['value']) => void;
    onChange: (
        value: RangePickerProps['value'],
        dateString: [string, string] | string,
    ) => void;
}

const onOk = (value: RangePickerProps['value']) => {
    console.log('onOk: ', value);
};

const TimeItem: React.FC<TimeItemProps> = (props) => {
    // const onChange = (
    //     value: RangePickerProps['value'],
    //     dateString: [string, string] | string,
    // ) => {
    //     console.log('Selected Time: ', value);
    //     console.log('Formatted Selected Time: ', dateString);
    //     console.log(dayjs().format());
    //     props
    // };

    return (
        <Space direction="vertical" size={12}>
            <RangePicker
                showTime={{format: 'HH:mm'}}
                format="YYYY-MM-DD HH:mm"
                onChange={props.onChange}
                onOk={props.onOk}
                defaultValue={[props.defaultStartValue, props.defaultEndValue]}
            />
        </Space>
    )
};
//defaultValue={[dayjs('2022-01-01'), dayjs('2022-01-31')]}
//[dayjs().subtract(1, "hour"), dayjs()]
export default TimeItem;


//
//
// let timeValue: RangePickerProps['value'] = null;
//     const [startEndTime, setStartEndTime] = useState<[string, string]>(['', '']);
//     const handleChange = (value?: RangePickerProps['value']) => {
//         timeValue = value;
//         console.log('timeValue: ', timeValue);
//     };
//
//     const onChange = (
//         value?: RangePickerProps['value'],
//         dateString?: [string, string] | string,
//     ) => {
//         console.log('Selected Time: ', value);
//         console.log('Formatted Selected Time: ', dateString);
//         console.log(dateString && dayjs(dateString[0]).valueOf().toString());
//         if (dateString != null) {
//             let time: [string, string] = [dayjs(dateString[0]).valueOf().toString(), dayjs(dateString[1]).valueOf().toString()];
//             console.log('nowtime'+time);
//             setStartEndTime(time);
//             console.log(startEndTime);
//         }
//     };
//
//
//
//
//     <div>
//             <TimeItem
//                 defaultEndValue={dayjs().subtract(1, "hour")} defaultStartValue={dayjs()}
//                 onChange={onChange}
//                 onOk={handleChange}
//             />
//         </div>
