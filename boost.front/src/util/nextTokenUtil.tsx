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
import {ActionType} from "@ant-design/pro-table/lib";

export interface FetchResult<T> {

  data: T[];
  success: boolean;
  total: number;
}

export const handleGoToPage = async (
    page: number,
    currentPage: number,
    totalPage: number,
    ListResult: (params: { pageSize: number; current: number; [key: string]: any }) => Promise<FetchResult<any> | undefined>,
    setCurrentPage: (page: number) => void,
    actionRef: React.MutableRefObject<ActionType | undefined>,
    pageSize: number

) => {
  if (page !== currentPage && page >= 1 && page <= totalPage) {
    for (let tmpPage = currentPage + 1; tmpPage <= page; tmpPage++) {
      await ListResult({ current: tmpPage, pageSize: pageSize});
    }
    setCurrentPage(page);
    actionRef.current?.reload();
  }
};






