import {DefaultFooter} from '@ant-design/pro-components';
import {useIntl} from '@umijs/max';
import React from 'react';
import {RootState} from "@/store/state";
import {useSelector} from "react-redux";

const Footer: React.FC = () => {
  const intl = useIntl();
  const providerName = useSelector((state: RootState) => state.providerInfo.providerName);
  const providerOfficialLink = useSelector((state: RootState) => state.providerInfo.providerOfficialLink);
  const defaultMessage = intl.formatMessage({
    id: 'app.copyright.produced',
    defaultMessage: providerName? providerName+'出品': '服务商待填出品',
  });

  const currentYear = new Date().getFullYear();

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
