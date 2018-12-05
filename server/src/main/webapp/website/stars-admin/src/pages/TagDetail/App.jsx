import React, {Component} from 'react';
import StarsFrame from '../../Component/Frame/StarsFrame'
import {textButton} from "../../util/util";
import "./detail.css"
import {apiImageFolderQuery,
    apiQueryTagDetail,
    apiQueryForImageListForFolder,
    apiImageFullUrl,
    apiAddTagToImage,
    apiRemoveTagToImage,
    apiTagContainsImageListQuery
} from "../../api/api"
import { Tag,Icon,Switch,Modal,Select,message,List,Card,Button} from 'antd';
import {jump2page,pageList} from "../../util/PageJumpUtil"
import urlPrase  from 'url-parse';
const { Meta } = Card;


class App extends Component {

    state = {

        watchImageDetailVisiable:false,
        watchImageDetailItem:{},
        imageFolderList:[],
        tagKey:"",
        tagDetail:{poolName:"",poolKey:""},
        folderKey:"none",

        imageList:[],
        index:0,
        size:50,
        total:0
    }

    componentDidMount() {


        var url = urlPrase(window.location.href,true);
        console.log('>>>>',url.query);

        this.state.tagKey = url.query.imgPoolKey;

        this.queryForImageFolder();

        this.queryForTagDetail();

        this.queryForImageListByFolderKey();

    }

    isSelectFolder(){

        return this.state.folderKey == "none"?true:false;
    }

    isItemMarked(item){
        if(item.markImgRecord){
            return true;
        }else{
            return false;
        }
    }
    queryForImageListByFolderKey(){

        var _this = this;
        if(!this.isSelectFolder()) {

            apiQueryForImageListForFolder(
                this.state.folderKey,
                this.state.index*this.state.size,
                this.state.size,
                this.state.tagKey,
                function(ret){

                    // var originList = JSON.parse(JSON.stringify(_this.state.imageList));
                    // originList = originList.concat(ret.data);

                    _this.setState({
                        imageList:_this.state.imageList.concat(ret.data),
                    total:ret.total,
                    index:_this.state.index+1
                    });

                })
        }else{

            //请求自己的图片
            apiTagContainsImageListQuery(this.state.tagKey,
                            this.state.index*this.state.size,
                            this.state.size,
                function(ret){

                console.log("own",ret);
                    _this.setState({
                        imageList:_this.state.imageList.concat(ret.data),
                        total:ret.total,
                        index:_this.state.index+1
                    });
                }
                )

        }
    }


    queryForTagDetail(){

        if(this.state.tagKey){

            var _this = this;
            apiQueryTagDetail(this.state.tagKey,function(ret){

                console.log("detail",ret);

                _this.setState({tagDetail:ret.data});

            });

        }
    }

    queryForImageFolder(){

        if(!this.state.tagKey){
            message.error("tag不能为空");
        }

        var _this = this;
        apiImageFolderQuery(function(ret){

            try {
                ret.data.splice(0, 0, {
                    folderName: "标签所有",
                    info: "none"
                });
            }catch (e){
                console.log(e);
            }

            _this.setState({imageFolderList:ret.data});

        })

    }

    renderForSelect(){

        return this.state.imageFolderList.map((item,index)=>{

            return <Select.Option value={item.info}>{item.folderName}</Select.Option>
        })

    }

    render() {

        var _this = this;

        const loadMore = (this.state.total > this.state.imageList.length)? (
            <div style={{ textAlign: 'center', marginTop: 12, height: 32, lineHeight: '32px' }}>
                <Button onClick={this.queryForImageListByFolderKey.bind(this)}>loading more</Button>
            </div>
        ) : null;

        return (
            <StarsFrame subTitle={<a onClick={()=>{window.location.reload()}}>{'数据集详情页面['+this.state.tagDetail.poolName+"/"+this.state.tagDetail.poolKey+']'}</a>}>

                <div className="home-tool-bar">
                    <Select
                        value={this.state.folderKey}
                        style={{ minWidth: 120 }} dropdownMatchSelectWidth={false}
                        onChange={(value)=>{

                            // console.log(value);
                            _this.setState({folderKey:value,
                                index:0,
                                imageList:[]},()=>{

                                _this.queryForImageListByFolderKey();
                            });

                        }}
                    >
                        {
                        this.renderForSelect()}</Select>

                    {
                        textButton(this.state.imageList.length+"/"+this.state.total,()=>{})
                    }
                </div>

                <div>

                    <List
                        grid={{justify:"start",gutter: 20, column: 5 }}
                        dataSource={this.state.imageList}
                        loadMore={loadMore}
                        hasMore={true}
                        renderItem={(item,index) => (
                            <List.Item>
                                <Card
                                    extra={
                                        this.isItemMarked(item)? <Tag color="green">已标注</Tag>:<div/>

                                    }
                                    title={index+"/"+item.imageName}>


                                    <Switch checkedChildren={<Icon type="check" />}
                                            unCheckedChildren={<Icon type="close" />}
                                            checked={item.markByTag}
                                            onChange={(value)=>{
                                                _this.onSwitchClick(value,item)
                                            }}
                                    />

                                    {
                                        // this.isSelectFolder()?<Button style={{marginLeft:5}} onClick={()=>{
                                        //
                                        //     jump2page(pageList.sketchStudio,{imgId:item.imageName})
                                        //
                                        // }
                                        // }>去标注</Button>:<div/>
                                        this.isSelectFolder()?textButton("去标注",()=>{
                                            jump2page(pageList.sketchStudio,{imgId:item.imageName})

                                        }):<div/>
                                    }

                                    <div
                                        className="detail-image-display-space"
                                        style={{marginTop:5}}
                                        onClick={
                                            ()=> {

                                                _this.setState({
                                                    watchImageDetailVisiable: true,
                                                    watchImageDetailItem: item
                                                })
                                            }
                                    }>

                                <img height={130} src={apiImageFullUrl(item.thumbnail)} />
                                    </div>



                                </Card>
                                {/*<Card*/}
                                    {/*hoverable*/}
                                    {/*style={{ width: 240 ,marginRight:10,marginTop:10}}*/}
                                    {/*cover={<img height={200} alt={item.imageName} src={apiImageFullUrl(item.thumbnail)} />}*/}
                                    {/*actions={[<Icon type="setting" />, <Icon type="edit" />, <Icon type="ellipsis" />]}*/}
                                {/*>*/}
                                    {/*<Meta*/}
                                        {/*title={<div>{this.isItemMarked(item)? <Tag color="green">已标注</Tag>:<div/>}{item.imageName}</div>}*/}
                                        {/*description="www.instagram.com"*/}
                                    {/*/>*/}
                                {/*</Card>*/}
                            </List.Item>
                        )}
                    />
                </div>


                <Modal
                    title={"图片详情"+this.state.watchImageDetailItem.info}
                    visible={this.state.watchImageDetailVisiable}
                    width={1300}
                    onOk={this.handleCancel.bind(this)}
                    onCancel={this.handleCancel.bind(this)}
                >
                    <div className='tag-detail-image-preview'>

                        <div>
                    <img width={this.isItemMarked(this.state.watchImageDetailItem)?600:1000} src={apiImageFullUrl(this.state.watchImageDetailItem.info)} />
                        </div>

                    {
                        this.isItemMarked(this.state.watchImageDetailItem)?<div><img width={600}
                                                                                style={{backgroundColor:'#000'}}
                                                                                     src={apiImageFullUrl(this.state.watchImageDetailItem.markImgRecord.fileFullPath)}/></div>:<div/>

                    }
                    </div>

                </Modal>

            </StarsFrame>
        )
    }


    onSwitchClick(value,item){

        // console.log("click on",value,item);

        if(item.imageName!=null) {

            var _this =this;

            if (value === true) {
                //add tag
                apiAddTagToImage(item.imageName,item.info,this.state.tagKey,function (ret) {

                    item.markByTag = true;

                    _this.setState(_this.state,()=>{
                        message.success("添加成功!");
                    });
                });

            } else {

                //remove tag
                apiRemoveTagToImage(item.imageName,this.state.tagKey,function (ret) {

                    item.markByTag = false;
                    _this.setState(_this.state,()=>{
                        message.success("去除成功");
                    });
                })
            }
        }
    }

    handleCancel(){

        this.setState({watchImageDetailVisiable:false});
    }
}

export default App;