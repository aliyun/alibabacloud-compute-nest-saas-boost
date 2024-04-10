import {DefaultFooter} from '@ant-design/pro-components';
import {useIntl} from '@umijs/max';
import React from 'react';

const Footer: React.FC = () => {
  const intl = useIntl();
  const defaultMessage = intl.formatMessage({
    id: 'app.copyright.produced',
    defaultMessage: '服务商待填',
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
          title: '服务商待填',
          href: '服务商待填',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
