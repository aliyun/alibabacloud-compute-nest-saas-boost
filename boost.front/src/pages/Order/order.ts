import moment from "moment/moment";
import dayjs from "dayjs";
import {listOrders} from "@/services/backend/order";
import {TradeStatusEnum} from "@/pages/Order/common";
import {PayChannelEnum, ProductNameEnum} from "@/constants";
import utc from "dayjs/plugin/utc";

dayjs.extend(utc);

export interface OrderFilter {
    tradeStatus?: 'TRADE_CLOSED' | 'TRADE_SUCCESS' | 'WAIT_BUYER_PAY' | 'TRADE_FINISHED' | 'REFUNDED' | 'REFUNDING';
    gmtCreate?: string;
    type?: string;
    serviceInstanceId?: string;
    orderId?: string;
}

export const fetchData = async (currentPage: number, show: boolean, pageSize:number, nextTokens:(string | undefined)[], filterValues:OrderFilter | undefined):Promise<[number, any[], (string | undefined)[]]> => {
    const params: API.ListOrdersParam = {
        maxResults: pageSize,
        nextToken: nextTokens[currentPage - 1],
    };
    if (filterValues != undefined && filterValues.tradeStatus != undefined) {
        params.tradeStatus = [filterValues.tradeStatus];
    }
    if (filterValues != undefined && filterValues.serviceInstanceId != undefined) {
        params.serviceInstanceId = filterValues.serviceInstanceId;
    }
    if (filterValues != undefined && filterValues.gmtCreate != null) {
        params.startTime = moment(filterValues.gmtCreate).utc().format('YYYY-MM-DDTHH:mm:ss[Z]');
        const currentTime = dayjs();
        params.endTime = currentTime.utc().format('YYYY-MM-DDTHH:mm:ss[Z]');
    } else {
        const currentTime = dayjs();
        const utcTime = currentTime.utc().format('YYYY-MM-DDTHH:mm:ss[Z]');
        params.startTime = currentTime.utc().subtract(1, 'year').format('YYYY-MM-DDTHH:mm:ss[Z]');
        params.endTime = utcTime;
    }
    const result: API.ListResultOrderDTO_ = await listOrders(params);
    if (result.data !== undefined) {
        nextTokens[currentPage] = result.nextToken;
        const transformedData = result.data?.map((item: API.OrderDTO) => {
            const localTime = item.gmtCreate ? moment.utc(item.gmtCreate).local().format('YYYY-MM-DD HH:mm:ss') : '';
            return {
                ...item,
                gmtCreate: localTime,
                tradeStatus: TradeStatusEnum[item.tradeStatus as keyof typeof TradeStatusEnum],
                productName: ProductNameEnum[item.productName as keyof typeof ProductNameEnum],
                type: PayChannelEnum[item.type as keyof typeof PayChannelEnum],
            };
        }) || [];
        return [result.count || 0, transformedData ? transformedData : [], nextTokens];
    }
    return [0, [], nextTokens];
};