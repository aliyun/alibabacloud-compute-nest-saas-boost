import {DefaultFooter} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {RootState} from "@/store/state";
import {useDispatch, useSelector} from "react-redux";
import {listConfigParameters} from "@/services/backend/parameterManager";
import profileImage from "../../../public/logo.png";
import {
  setProviderDescription,
  setProviderLogoUrl,
  setProviderName,
  setProviderOfficialLink
} from "@/store/providerInfo/actions";
import {initialProviderInfoEncryptedList, initialProviderInfoNameList} from "@/pages/Parameter/common";

const Footer: React.FC = () => {
  const providerName = useSelector((state: RootState) => state.providerInfo.providerName);
  const providerOfficialLink = useSelector((state: RootState) => state.providerInfo.providerOfficialLink);
  const defaultMessage = providerName? providerName+'出品': '服务商待填出品';
  const currentYear = new Date().getFullYear();
  const dispatch = useDispatch();
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    handleRefresh();
  }, []);
  const loadConfigParameters = async (parameterNames: string[], encrypted: boolean[]) => {
    const configParameterQueryModels: API.ConfigParameterQueryModel[] = parameterNames.map((name, index) => ({
      name,
      encrypted: encrypted[index],
    }));

    const listParams: API.ListConfigParametersParam = {
      configParameterQueryModels,
    };

    const result: API.ListResultConfigParameterModel_ = await listConfigParameters(listParams);
    if (result.data && result.data.length > 0) {
      result.data.forEach((configParam) => {
        if (configParam.name && configParam.value) {
          let value = configParam.value === 'waitToConfig' ? '' : configParam.value;
          // 针对 'ProviderLogoUrl' 名称进行特殊处理
          if (configParam.name === 'ProviderLogoUrl' && configParam.value === 'waitToConfig') {
            value = profileImage;
          }
          if (configParam.name === 'ProviderName') {
            dispatch(setProviderName(value));
          } else if (configParam.name === 'ProviderOfficialLink') {
            dispatch(setProviderOfficialLink(value));
          } else if (configParam.name === 'ProviderDescription') {
            dispatch(setProviderDescription(value));
          } else if (configParam.name === 'ProviderLogoUrl') {
            dispatch(setProviderLogoUrl(value));
          }
        }
      });
    }
  };

  const handleRefresh = async () => {
    setRefreshing(true);
    await loadConfigParameters(initialProviderInfoNameList, initialProviderInfoEncryptedList);
    setRefreshing(false);
  };

  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'supplier',
          title: providerName ? providerName : '服务商名待填',
          href: providerOfficialLink ? providerOfficialLink : '服务商官网链接待填',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
