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

const listMetricMetaDatas = (req: Request, res: Response) => {
  res.json({'data': [
    {
      'metricName': 'Groupvm.CPUUtilization',
      'metricDescription': 'CPU_Utilization(%)',
      'unit': '%',
      'statistics': [
        'Average',
        'Maximum',
        'Minimum'
      ]
    },
    {
      "metricName": "Groupvm.MemoryUtilization",
      "metricDescription": "MemoryUtilization(%)",
      "unit": "%",
      "statistics": [
        "Average",
        "Maximum",
        "Minimum"
      ]
    },
    {
      "metricName": "Groupvm.LoadAverage",
      "metricDescription": "AverageCost",
      "unit": "cost",
      "statistics": [
        "Average",
        "Maximum",
        "Minimum"
      ]
    }
  ],
      'count': 3},

  );
};

const listMetrics = (req: Request, res: Response) => {
    res.json({'data': {
    'dataPoints': '[{\"timestamp\":1693350615000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.0,\"Maximum\":1.51,\"Average\":0.67},{\"timestamp\":1693350630000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.5,\"Maximum\":3.5,\"Average\":0.87},{\"timestamp\":1693350645000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.0,\"Maximum\":1.01,\"Average\":0.6},{\"timestamp\":1693350660000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.0,\"Maximum\":1.01,\"Average\":0.67},{\"timestamp\":1693350675000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.0,\"Maximum\":1.51,\"Average\":0.7},{\"timestamp\":1693350690000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.0,\"Maximum\":1.5,\"Average\":0.7},{\"timestamp\":1693350705000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.0,\"Maximum\":1.5,\"Average\":0.67},{\"timestamp\":1693350720000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.0,\"Maximum\":1.49,\"Average\":0.63},{\"timestamp\":1693350735000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.0,\"Maximum\":1.5,\"Average\":0.6},{\"timestamp\":1693350750000,\"groupId\":\"237353946\",\"userId\":\"1563457855438522\",\"Minimum\":0.5,\"Maximum\":1.5,\"Average\":0.67}]'}
    });
}

export default {
  'GET /api/listMetrics': listMetrics,
  'GET /api/listMetricMetaDatas': listMetricMetaDatas
};