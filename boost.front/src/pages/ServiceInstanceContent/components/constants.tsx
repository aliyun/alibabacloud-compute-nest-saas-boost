import {CLOUD_MARKET_ORDER_URL} from "@/constants";
import moment from "moment";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
export function navigateToCloudMarketplaceOrderDetails(): void {
    window.open(CLOUD_MARKET_ORDER_URL, '_blank');
    window.location.reload();
}

export const processServiceInstanceData = (data: API.ServiceInstanceModel) => {
    let outputs = {};
    let parameters = {};

    if (data !== null) {
        if (data?.outputs !== null) {
            outputs = JSON.parse(data?.outputs as string);
        }
        if (data?.parameters !== null) {
            parameters = JSON.parse(data?.parameters as string);
        }
    }
    return {outputs, parameters};
};

export const convertToLocaleTime = (utcTime: string | undefined): string => {
    dayjs.extend(utc);

    if (utcTime === undefined) {
        return '';
    }
    return moment.utc(utcTime).local().format('YYYY-MM-DD HH:mm:ss');
};

export type UnitMappingType = {
    [key: string]: string;
};

export const unitMapping: UnitMappingType = {
    month: '月',
    day: '日',
    year: '年',
};