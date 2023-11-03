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

import {RuleObject} from "antd/lib/form";
import {Form, Input} from 'antd';
import React from "react";
import ProCard from "@ant-design/pro-card";

export const validatePassword: RuleObject = {
    validator(_, value) {
        const password = value.trim(); // 删除首尾空格
        const uppercaseRegex = /[A-Z]/;
        const lowercaseRegex = /[a-z]/;
        const digitRegex = /\d/;
        const specialCharRegex = /[()\-`~!@#$%^&*_+=|{}[\]:;'<>,.?/]/;

        const conditions = [
            uppercaseRegex.test(password),
            lowercaseRegex.test(password),
            digitRegex.test(password),
            specialCharRegex.test(password),
        ];
        const validConditionsCount = conditions.filter(condition => condition).length;
        if (validConditionsCount < 3 || password.length < 8 || password.length > 30 || value.includes(' ')) {
            return Promise.reject('密码必须包含大写字母、小写字母、数字和特殊字符中的其中三个，且长度为8-30，不能包含空格');
        }
        return Promise.resolve();
    },
};

const PasswordFormItem: React.FC = () => {
    return (
        <ProCard title="参数" bordered headerBordered={false} gutter={8} hoverable>
            <Form.Item
                label="实例密码"
                name="instancePassword"
                rules={[
                    {required: true, message: '请输入密码'},
                    validatePassword,
                ]}
            >
                <Input.Password/>
            </Form.Item>
        </ProCard>
    );
};

export default PasswordFormItem;
