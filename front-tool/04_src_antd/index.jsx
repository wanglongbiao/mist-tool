import React, { useState } from 'react';
import { render } from 'react-dom';
import { ConfigProvider, DatePicker, Layout, message, PageHeader } from 'antd';
import zhCN from 'antd/lib/locale/zh_CN';
import moment from 'moment';
import 'moment/locale/zh-cn';
import 'antd/dist/antd.css';
import './index.css';

moment.locale('zh-cn');

const App = () => {
  const [date, setDate] = useState(null);
  const handleChange = value => {
    message.info(`您选择的日期是: ${value ? value.format('YYYY年MM月DD日') : '未选择'}`);
    setDate(value);
  };
  return (
    <ConfigProvider locale={zhCN}>
        <Layout>
            <PageHeader />

            
        </Layout>
      <div style={{ width: 400, margin: '100px auto' }}>
        <DatePicker onChange={handleChange} />
        <div style={{ marginTop: 16 }}>
          当前日期：{date ? date.format('YYYY年MM月DD日') : '未选择'}
        </div>
      </div>
    </ConfigProvider>
  );
};

render(<App />, document.getElementById('root'));
