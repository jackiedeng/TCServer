import React, {Component} from 'react';
import StarsFrame from '../../Component/Frame/StarsFrame'
import {textButton} from "../../util/util";
import {Popconfirm,Input, Modal,Table,message} from "antd";
import {apiQueryAllMarkTag, apiNewOrUpdateMarkTag, apiDeleteTagByKey,apiDeleteMarkTag} from '../../api/api'
class App extends Component {

    state={
        visible:false,

        tagList:[],

        newTag:{
            tagKey:"",
            tagName:"",
            tagColor:"",
            ext:""
        }
    }

    componentDidMount() {

        this.queryForAllTag();
    }

    queryForAllTag(){

        apiQueryAllMarkTag((ret)=>{

            console.log("query all");
            this.setState({tagList:ret.data});
            console.log(ret);
        })
    }

    reset(){

        this.state.newTag = {
            tagKey:"",
            tagName:"",
            tagColor:"",
            ext:""
        };
    }


    colums(){

        var _this = this;

        return [{
            title: '标签名称',
            dataIndex: 'tagName',
            key: 'tagName'
        }, {
            title: '标签key',
            dataIndex: 'tagKey',
            key: 'tagKey',
        }, {
            title: '颜色',
            key: 'tagColor',
            render:item=>{return <div style={{color:'#000',backgroundColor:item.tagColor}}>{item.tagColor}</div>}
        }, {
            title: '操作',
            key: 'operation',
            render:item=>{return <div>
                <Popconfirm title={"确认删除[ "+item.tagName+" ]?"}
                                             onConfirm={()=>{

                                                 apiDeleteMarkTag(item.tagKey,()=>{

                                                     console.log("dsadsadasdas>>>>>>")
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
                    textButton("删除",()=>{})
                }
                    </Popconfirm>

                    {
                        textButton("编辑",()=>{

                            this.setState({visible:true,newTag: JSON.parse(JSON.stringify(item))})

                        })
                    }

            </div>
                }
        }];
    }

    render() {
        return (
            <StarsFrame markKey='datatag' subTitle="标签管理页">
                <div style={{marginBottom:20}}>
                    {textButton("新增标签",()=>{

                        this.reset();
                        this.setState({visible:true})

                    })}

                </div>

                <Table dataSource={this.state.tagList} columns={this.colums()} />


                <Modal
                    title="新增标签"
                    visible={this.state.visible}
                    onOk={this.handleOk.bind(this)}
                    onCancel={this.handleCancel.bind(this)}
                >

                    <Input value={this.state.newTag.tagName}
                           placeholder="标签名称"
                           onChange={(value)=>{

                               this.state.newTag.tagName = value.target.value;
                               this.setState(this.state);

                           }}/>

                    <Input value={this.state.newTag.tagKey}
                           style={{marginTop:10}}
                           placeholder="标签唯一key"
                           onChange={(value)=>{

                               this.state.newTag.tagKey = value.target.value;
                               this.setState(this.state);

                           }}/>

                    <Input value={this.state.newTag.tagColor}
                           style={{marginTop:10}}
                           placeholder="标签颜色 例如:#00dd00"
                           onChange={(value)=>{

                               this.state.newTag.tagColor = value.target.value;
                               this.setState(this.state);

                           }}/>

                    <div style={{width:200,height:20,marginTop:10,backgroundColor:this.state.newTag.tagColor}} />

                </Modal>

            </StarsFrame>
        )
    }

    handleOk() {

        apiNewOrUpdateMarkTag(this.state.newTag.tagName,
            this.state.newTag.tagKey,
            this.state.newTag.tagColor,"{}", (ret) => {

                console.log(ret);
                message.success("新建成功");
                this.queryForAllTag();

            });

        this.handleCancel();
    }


    handleCancel(){

        this.setState({visible:false})
    }
}

export default App;