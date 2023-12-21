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

export const handleGoToPage = async (page: number, currentPage:number, totalPage:number, fetchData: (page: number, flag: boolean) => Promise<void>,  setCurrentPage: (page: number) => void
) => {
  if (page > currentPage && page <= totalPage) {
    for (let tmpPage = currentPage + 1; tmpPage <= page; tmpPage++) {
      await fetchData(tmpPage, false);
    }
    setCurrentPage(page);
  } else {
    if (page >= 1) {
      setCurrentPage(page);
    }
  }
};


