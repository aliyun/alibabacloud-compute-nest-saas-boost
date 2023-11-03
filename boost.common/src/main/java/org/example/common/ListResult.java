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

package org.example.common;

import java.io.Serializable;
import java.util.List;

public class ListResult<T> extends BaseResult implements Serializable {

    private static final long serialVersionUID = 729779190711627058L;

    private List<T> data;

    private Long count;

    private String nextToken;

    public ListResult() {
    }

    @Override
    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getNextToken() {
        return nextToken;
    }

    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    public static <T> ListResult<T> genSuccessListResult(List<T> data, long count) {
        ListResult<T> listResult = new ListResult<T>();
        listResult.setData(data);
        listResult.setCount(count);
        return listResult;
    }

    public static <T> ListResult<T> genSuccessListResult(List<T> data, long count, String nextToken) {
        ListResult<T> listResult = new ListResult<T>();
        listResult.setData(data);
        listResult.setCount(count);
        listResult.setNextToken(nextToken);
        return listResult;
    }
}
