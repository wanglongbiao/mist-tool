import React, { useState, Component } from 'react';
import { render } from 'react-dom';
import { ConfigProvider, DatePicker, Layout, message, PageHeader, Menu, Breadcrumb } from 'antd';
import { UserOutlined, LaptopOutlined, NotificationOutlined } from '@ant-design/icons';
import zhCN from 'antd/lib/locale/zh_CN';
import moment from 'moment';
import 'moment/locale/zh-cn';
import 'antd/dist/antd.css';
import './index.css';
import OlMap from './components/OlMap';

const { Header, Content, Sider } = Layout;
const { SubMenu } = Menu;
export default class App extends Component {
  render() {
    return (
      <div>
        <Layout>
          <Header className="header">
            <div className="logo" />
            <Menu theme="dark" mode="horizontal" defaultSelectedKeys={['1']}>
              <Menu.Item key="1">首页</Menu.Item>
              <Menu.Item key="2">文章</Menu.Item>
              <Menu.Item key="3">关于</Menu.Item>
            </Menu>
          </Header>
          <Layout>
            <Sider width={200} className="site-layout-background">
              <Menu
                mode="inline"
                defaultSelectedKeys={['1']}
                defaultOpenKeys={['sub1']}
                style={{ height: '100%', borderRight: 0 }}
              >
                <SubMenu key="sub1" icon={<UserOutlined />} title="地图工具">
                  <Menu.Item key="1">OpenLayers 工具</Menu.Item>
                  <Menu.Item key="2">option2</Menu.Item>
                  <Menu.Item key="3">option3</Menu.Item>
                  <Menu.Item key="4">option4</Menu.Item>
                </SubMenu>
                <SubMenu key="sub2" icon={<LaptopOutlined />} title="subnav 2">
                  <Menu.Item key="5">option5</Menu.Item>
                  <Menu.Item key="6">option6</Menu.Item>
                  <Menu.Item key="7">option7</Menu.Item>
                  <Menu.Item key="8">option8</Menu.Item>
                </SubMenu>
              </Menu>
            </Sider>
            <Layout style={{ padding: '0 24px 24px' }}>
              <Breadcrumb style={{ margin: '16px 0' }}>
                <Breadcrumb.Item>Home</Breadcrumb.Item>
                <Breadcrumb.Item>List</Breadcrumb.Item>
                <Breadcrumb.Item>App</Breadcrumb.Item>
              </Breadcrumb>
              <Content
                className="site-layout-background"
                style={{
                  padding: 24,
                  margin: 0,
                  minHeight: 280,
                }}
              >
                <div>
                  <OlMap />
                </div>
              </Content>
            </Layout>
          </Layout>
        </Layout>
      </div>
    )
  }
}
