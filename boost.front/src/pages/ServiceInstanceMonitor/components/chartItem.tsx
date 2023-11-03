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

import styles from "@/pages/ServiceInstanceMonitor/headTitle.module.css";
import React from "react";
import ChartData from "@/pages/ServiceInstanceMonitor/components/chartLine";

interface ChartItemProps {
    title : string;
    data : string;
    statistics: string[];
}

const ChartItem: React.FC<ChartItemProps> = ({
    title,
    data,
    statistics
}) => {
    return (
        <div style={{background: '#e9e9e9'}}>
        <h2 style={{textAlign: 'center'}} className={styles['titleStyle']}>{title}</h2>
        <div style={{background:'whitesmoke'}} className={styles.container}>
          <ChartData
          data={data}
          statistics={statistics}
          />
        </div>
      </div>
    )
};

export default ChartItem;
