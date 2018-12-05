import React, {Component} from 'react'
import Table from 'antd/lib/table'
import Popconfirm from 'antd/lib/popconfirm'
import {Modal,Input} from  'antd'
import StarsFrame from '../../Component/Frame/StarsFrame'
import message from "antd/lib/message"
import "./home.css"
import {textButton} from "../../util/util"
import {apiGetAllTagList,apiDeleteTagByKey,apiCreateNewTag} from "../../api/api"
import {jump2page,pageList} from "../../util/PageJumpUtil"

class App extends Component {

    state ={

        visible:false,
        addNewTagSwitch:false,
        tagList:[],
        newTagName:"",
        newTagKey:""
    };

    componentDidMount() {

        this.queryForAllTag();
    }

    queryForAllTag(){

        console.log("requet:");
        var _this = this;
        apiGetAllTagList(function(ret){

            console.log(">>",ret["data"],ret);
            _this.setState({tagList:ret.data});

        })

    }

    renderDelteTag(item){
        var _this =this;
        return <Popconfirm title={"确认删除数据集"+item.poolName+"?"}
                             onConfirm={()=>{

                                 apiDeleteTagByKey(item.poolKey,function(ret){

                                     message.success("删除成功");

                                     _this.queryForAllTag();

                                 })
                                }
                             }
                             onCancel={()=>{
                             }
                             }
                             okText="Yes"
                             cancelText="No">
                     {
                         <a href="#">删除</a>
                     }
                 </Popconfirm>

    }

    columns () {

        return [{
            title: '数据集名称',
            // dataIndex: 'poolName' // String-based value accessors!
            render:item=><a onClick={()=>{

                jump2page(pageList.tagDetail,{imgPoolKey:item.poolKey})
            }
            }>{item.poolName}</a>
        }, {
            title: '图片数量',
            dataIndex:"imgCount"
        }
        , {
                title: '操作',
                render: item=><div>{
                    this.renderDelteTag(item)
                }</div> // Custom cell components!
            }
        ]

    }


    render() {

        return (
            <StarsFrame markKey='dataset'>
                <div className="home-tool-bar">
                    {
                        textButton("新增数据集", () => {

                            this.setState({visible: true});
                        })
                    }

                    {/*{*/}
                        {/*textButton("去标注", () => {*/}

                            {/*jump2page(pageList.sketchStudio,{});*/}
                        {/*})*/}
                    {/*}*/}
                </div>

                <Table style={{width:'100%'}}
                    dataSource={this.state.tagList}
                    columns={this.columns()}
                />

                <Modal
                    title="新增数据集"
                    visible={this.state.visible}
                    onOk={this.handleOk.bind(this)}
                    onCancel={this.handleCancel.bind(this)}
                >

                    <Input value={this.state.newTagName}
                           placeholder="数据集名称"
                           onChange={(value)=>{

                               this.setState({newTagName:value.target.value})

                           }}/>

                    <Input value={this.state.newTagKey}
                           style={{marginTop:10}}
                           placeholder="数据集key"
                           onChange={(value)=>{

                               // console.log(value.target.value);
                               this.setState({newTagKey:value.target.value})

                           }}/>

                </Modal>

            </StarsFrame>
        )
    }

    handleOk(){

        console.log("create new tag:",this.state.newTagName,this.state.newTagKey)

        var _this = this;
        apiCreateNewTag(this.state.newTagName,this.state.newTagKey,function(ret){

            message.success("创建成功");
            _this.queryForAllTag();
        })

        this.handleCancel();
    }

    handleCancel(){

        this.setState({visible:false})
    }
}

export default App;