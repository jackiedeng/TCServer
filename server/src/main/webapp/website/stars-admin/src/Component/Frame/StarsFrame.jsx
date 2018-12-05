import React, {Component} from 'react';
import {Breadcrumb,Menu,Layout} from 'antd'
import "./style.css"
const SubMenu = Menu.SubMenu;
const { Header, Content, Footer } = Layout;



class StartFrame extends Component {

    defaultSelect(){

        if(this.props.markKey){
            return [this.props.markKey];
        }
    }

    render() {
        return (

            <Layout className="layout">
                <Header>
                    <div className="logo">FIA管理系统</div>
                    <Menu
                        theme="dark"
                        mode="horizontal"
                        defaultSelectedKeys={this.defaultSelect()}
                        style={{ lineHeight: '64px' }}
                        onClick={(item,key,keyPath)=>{

                            // console.log('>>>',item.path,item);
                            window.open("/"+item.key,'_self');


                        }}
                    >
                        <Menu.Item key="dataset">训练数据集</Menu.Item>
                        <Menu.Item key="datatag">标注管理</Menu.Item>
                        <Menu.Item key="train">模型任务</Menu.Item>
                    </Menu>
                </Header>
                <Content style={{ padding: '0 50px' }}>
                    <Breadcrumb style={{ margin: '16px 0' }}>
                        <Breadcrumb.Item>{this.props.subTitle}</Breadcrumb.Item>
                    </Breadcrumb>
                    <div style={{ background: '#fff', padding: 24, minHeight: 280 }}>
                        <div className='main-content-layout'>
                        {
                            this.props.children
                        }
                        </div>
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center' }}>
                     S.T.A.R.S ©2018
                </Footer>
            </Layout>
        );
    }
}

export default StartFrame;