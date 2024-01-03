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

import React from 'react';
import {DatePicker, Space} from 'antd';
import type {RangePickerProps} from 'antd/es/date-picker';
import {Dayjs} from "dayjs";

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

const TimeItem: React.FC<TimeItemProps> = (props) => {

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

export default TimeItem;
