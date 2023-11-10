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

export interface Specification {
    Name: string;
    Parameters: { [key: string]: string[] };
    OrderList: string[];
    Type: string;
    Description: string;
}


export interface ParameterTypeInterfaceArray extends Record<string, ParameterTypeInterface> {}

export interface ParameterTypeInterface {
    Type?: string;
    NoEcho?: boolean;
    Label?: {
        en?: string;
        'zh-cn'?: string;
    };
    AllowedPattern?: string;
    MaxValue?: number;
    MinValue?: number;
    Default?: any;
    Description?: {
        'zh-cn': string;
        en: string;
    };
    AssociationProperty?: string;
    AssociationPropertyMetadata?: {
        Visible?: {
            Condition?: {
                'Fn::Equals': [any, any];
            };
        };
    };
    AllowedValues?: string[];
}

export interface ParameterGroupsInterface {
    "ALIYUN::ROS::Interface"
        : {
        TemplateTags: string[];
        ParameterGroups: {
            Parameters: string[];
            Label: {
                default: {
                    en: string;
                    'zh-cn': string;
                };
            };
        }[];
    };
}

