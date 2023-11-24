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

import {Request, Response} from "express";

const listServiceInstances = (req: Request, res: Response) => {
    res.json({
        'data': [
            {
                'serviceInstanceId': 'si-xxx',
                'serviceInstanceName': 'mytest',
                'serviceName': 'service-xxx',
                'status': 'Deploying',
                'createTime': '2023-07-14 10:49:22',
                'updateTime': '2023-07-14 10:49:22',
                'parameters': '{"UserName": "root", "Password": "****"}',
            }, {
                'serviceInstanceId': 'si-xxx-1',
                'serviceInstanceName': 'mytest',
                'serviceName': 'service-xxx',
                'status': 'Deployed',
                'createTime': '2023-07-14 10:49:22',
                'updateTime': '2023-07-14 10:49:22',
                'parameters': '{"UserName": "root", "Password": "****"}',
            }, {
                'serviceInstanceId': 'si-xxx-2',
                'serviceInstanceName': 'mytest',
                'serviceName': 'service-xxx',
                'status': 'Deleting',
                'createTime': '2023-07-14 10:49:22',
                'updateTime': '2023-07-14 10:49:22',
                'parameters': '{"UserName": "root", "Password": "****"}',
            }, {
                'serviceInstanceId': 'si-xxx-3',
                'serviceInstanceName': 'mytest',
                'serviceName': 'service-xxx',
                'status': 'DeployedFailed',
                'createTime': '2023-07-14 10:49:22',
                'updateTime': '2023-07-14 10:49:22',
                'parameters': '{"UserName": "root", "Password": "****"}',
            }, {
                'serviceInstanceId': 'si-xxx-4',
                'serviceInstanceName': 'mytest',
                'serviceName': 'service-xxx',
                'status': 'DeletedFailed',
                'createTime': '2023-07-14 10:49:22',
                'updateTime': '2023-07-14 10:49:22',
                'parameters': '{"UserName": "root", "Password": "****"}',
            }]


    });
};

const getServiceInstance = (req: Request, res: Response) => {
    res.json({
        'data': {
            'serviceInstanceId': 'si-xxx',
            'serviceInstanceName': 'mytest',
            'status': 'Deployed',
            'createTime': '2023-02-03 12:00:00',
            'updateTime': '2023-02-03 12:00:00',
            'parameters': '{"hello": "test"}',
            'outputs': '{"mytest": "test", "aaa": "bbb"}',
            'service': {
                'serviceId': "service-xxx",
                'name': "计算巢测试服务",
                'image': "https://service-info-public.oss-cn-hangzhou.aliyuncs.com/1563457855438522/service-image/b5571ccd-51a5-405f-8152-9ed7e41a7111.png",
                'description': "描述测试"
            }
        }
    });
}
const serviceCost = (req: Request, res: Response) => {
    res.json({'data': 777});
}

const getServiceMetadata = (req: Request, res: Response) => {
    res.json({
        'data': {
            "parameterMetadata": {
                "Parameters": {
                    "WhetherCreateVpc": {
                        "Type": "Boolean",
                        "Label": {
                            "en": "WhetherCreateVpc",
                            "zh-cn": "是否新建VPC"
                        },
                        "Default": false
                    },
                    "VpcCidrBlock": {
                        "Type": "String",
                        "NoEcho": true,
                        "Label": {
                            "en": "VPC CIDR IPv4 Block",
                            "zh-cn": "专有网络IPv4网段"
                        },
                        "Description": {
                            "zh-cn": "VPC的ip地址段范围，&lt;br&gt;您可以使用以下的ip地址段或其子网:&lt;br&gt;&lt;font color='green'&gt;[10.0.0.0/8]&lt;/font&gt;&lt;br&gt;&lt;font color='green'&gt;[172.16.0.0/12]&lt;/font&gt;&lt;br&gt;&lt;font color='green'&gt;[192.168.0.0/16]&lt;/font&gt;",
                            "en": "The ip address range of the VPC in the CidrBlock form; &lt;br&gt;You can use the following ip address ranges and their subnets: &lt;br&gt;&lt;font color='green'&gt;[10.0.0.0/8]&lt;/font&gt;&lt;br&gt;&lt;font color='green'&gt;[172.16.0.0/12]&lt;/font&gt;&lt;br&gt;&lt;font color='green'&gt;[192.168.0.0/16]&lt;/font&gt;"
                        },
                        "Default": "192.168.0.0/16",
                        "AssociationProperty": "ALIYUN::VPC::VPC::CidrBlock",
                        "AssociationPropertyMetadata": {
                            "Visible": {
                                "Condition": {
                                    "Fn::Equals": [
                                        "${WhetherCreateVpc}",
                                        true
                                    ]
                                }
                            }
                        }
                    },
                    "VpcId": {
                        "Type": "String",
                        "Label": {
                            "en": "VPC ID",
                            "zh-cn": "专有网络VPC实例ID"
                        },
                        "AssociationProperty": "ALIYUN::ECS::VPC::VPCId",
                        "AssociationPropertyMetadata": {
                            "Visible": {
                                "Condition": {
                                    "Fn::Equals": [
                                        "${WhetherCreateVpc}",
                                        false
                                    ]
                                }
                            }
                        },
                        "Default": ""
                    },
                    "EcsInstanceType": {
                        "Type": "String",
                        "Label": {
                            "en": "Instance Type",
                            "zh-cn": "实例类型"
                        },
                        "AssociationProperty": "ALIYUN::ECS::Instance::InstanceType",
                        "AssociationPropertyMetadata": {
                            "InstanceChargeType": "${PayType}"
                        },
                        "AllowedValues": [
                            "ecs.g6.large", "ecs.g5.large", "ecs.g5ne.large",
                            "ecs.g6r.large"
                        ]
                    }
                },
                "Metadata": {
                    "ALIYUN::ROS::Interface": {
                        "TemplateTags": [
                            "acs:integrate:计算巢:ModelScope"
                        ],
                        "ParameterGroups": [
                            {
                                "Parameters": [
                                    "EcsInstanceType"
                                ],
                                "Label": {
                                    "default": {
                                        "en": "Instance",
                                        "zh-cn": "ECS实例配置"
                                    }
                                }
                            },
                            {
                                "Parameters": [
                                    "WhetherCreateVpc",
                                    "VpcId",
                                    "VpcCidrBlock"
                                ],
                                "Label": {
                                    "default": {
                                        "zh-cn": "选择网络配置",
                                        "en": "Choose existing Infrastructure Configuration"
                                    }
                                }
                            }
                        ]
                    }
                }

            },
            "specifications": [{
                "Name": "Entry Level Package",
                "Parameters": {"EcsInstanceType": ["ecs.g5.large", "ecs.g5ne.large"]},
                "OrderList": ["EcsInstanceType"],
                "Type": "Normal",
                "Description": "入门版(Entry Level Package)"
            }, {
                "Name": "Standard Package",
                "Parameters": {"EcsInstanceType": ["ecs.g6.large"]},
                "OrderList": ["EcsInstanceType"],
                "Type": "Normal",
                "Description": "标准版(Standard Package)"
            }, {
                "Name": "Premium Package",
                "Parameters": {"EcsInstanceType": ["ecs.g6r.large"]},
                "OrderList": ["EcsInstanceType"],
                "Type": "Normal",
                "Description": "高配版(Premium Package)"
            }],
            "templateName": "模板1"
        }


    })
    ;
}
export default {
    'GET /api/listServiceInstances': listServiceInstances,
    'GET /api/getServiceInstance': getServiceInstance,
    'GET /api/getServiceCost': serviceCost,
    'GET /api/getServiceMetadata': getServiceMetadata,
};