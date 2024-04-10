/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 200 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-200

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
import { Request, Response } from "express";

const listConfigParametersMock = (req: Request, res: Response) => {
    const mockData = [
        {
            "Type": "String",
            "Name": "providerName",
            "Id": "p-14ed150fdcd048xxxxxx"
        },
        {
            "Type": "Secret",
            "Name": "alipayPublicKey",
            "Id": "xxxxxx111"
        }
    ];

    res.json({
        data: mockData,
    });
};

const updateConfigParameterMock = (req: Request, res: Response) => {
    const updatedConfigParam: API.BaseResultVoid_ = {
        code: '200',
        message: "Config parameter successfully updated",
        requestId: '123',
    };

    res.json(updatedConfigParam);
};

export default {
    'GET /api/listConfigParameters': listConfigParametersMock,
    'POST /api/updateConfigParameter': updateConfigParameterMock,
};