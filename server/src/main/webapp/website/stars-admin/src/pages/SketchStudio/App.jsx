import React, {Component} from 'react';
import StarsFrame from '../../Component/Frame/StarsFrame'
import {Popconfirm,Modal, Tag, Card, Button, Radio, Slider, Switch, Input} from 'antd';
import { Stage,Image, Layer, Rect, Text,Circle,Shape} from 'react-konva';
import {hex2rgb, textButton} from '../../util/util'
import Konva from 'konva';
import './sketchStyle.css';
import DrawBoard from './DrawBoard.jsx';
import {apiQueryAllMarkTag,apiImageFullUrl,queryImgDetail,apiUploadImage} from "../../api/api";
import message from "antd/lib/message";
import urlPrase  from 'url-parse';
//https://konvajs.github.io/docs/overview.html
const { CheckableTag } = Tag;


class App extends Component {

    state = {
        addNewMarkSiwtch:false,
        newMarkName:"",
        newMarkColor:"",

        recordId:"",
        recordDetail:{},

        image: new window.Image(),
        markedImage: new window.Image(),

        maxImageSize:1000,
        stageWidth:1000,
        stageHeight:1000,
        imageWidth: 100,
        imageHeight: 100,
        isTouching:false,
        lastPoint:{},
        pointWidth:30,
        previewWidth:1000,
        previewHeight:1000,

        brushs:[],
        allBrushs:[],

        selectBrush:0,
    }

    componentDidMount() {

        this.queryForRecordRetail();
    }

    constructor(){
        super();

        var url = urlPrase(window.location.href,true);
        console.log('>>>>',url);

        this.state.recordId = url.query.imgId;

    }



    queryForTags(){

        apiQueryAllMarkTag((ret)=>{

            this.setState({allBrushs:ret.data},()=>{

                this.reloadBrush(()=>{

                    this.queryImage();
                });

            });
        })
    }

    queryForRecordRetail(){

        queryImgDetail(this.state.recordId,(ret)=>{

            console.log("detail:",ret);

            this.setState({recordDetail:ret.data},()=>{
                this.queryForTags();
            });

        })
    }

    reloadBrush(ret){

        console.log("reload brush");
        if(this.state.allBrushs.length > 0 && this.state.recordDetail.markImgRecord){

            var allBrushSet = {};
            for (var brush of this.state.allBrushs) {
                allBrushSet[brush.tagKey] = brush;
            }

            var brushs = [];
            for(var selectItem of this.state.recordDetail.markImgRecord.tag){

                if(allBrushSet[selectItem]){

                    brushs.push(allBrushSet[selectItem]);
                }
            }

            console.log("....",brushs,allBrushSet);
            this.setState({brushs:brushs},()=>{
                ret()
            });

        }else{
            ret();
        }
    }

    pointFromEvent(evt){
        var point =  {};
        point.x = evt.evt.layerX;
        point.y = evt.evt.layerY;
        return point;
    }



    drawPointTo(point){

        var context =  this.shapeContext;//this.shapeNode.getContext();
        if(this.state.selectBrush === -1){
            context.strokeStyle = "#fff";
            this.shapeContext.globalCompositeOperation='destination-out';
        }else {
            context.strokeStyle = this.state.brushs[this.state.selectBrush].tagColor;
            this.shapeContext.globalCompositeOperation='source-over';
        }

        context.lineWidth = this.state.pointWidth;

        context.beginPath();
        context.moveTo( this.state.lastPoint.x, this.state.lastPoint.y);
        context.lineTo(point.x,point.y);
        context.closePath();
        context.stroke();

        //get update rect
        var updatex = this.state.lastPoint.x > point.x?point.x:this.state.lastPoint.x;
        var updatey = this.state.lastPoint.y > point.y?point.y:this.state.lastPoint.y;
        var updateWidth = Math.abs(this.state.lastPoint.x-point.x);
        var updateHeight = Math.abs(this.state.lastPoint.y-point.y);

        updatex-=this.state.pointWidth/2;
        updatey-=this.state.pointWidth/2;
        updateWidth+=this.state.pointWidth*1.5;
        updateHeight+=this.state.pointWidth*1.5;

        this.state.lastPoint = point;

        // console.log("updaterect",updatex,updatey,updateWidth,updateHeight);

        if(this.state.selectBrush >=0 ) {

            var imgData = this.shapeContext.getImageData(updatex, updatey, updateWidth, updateHeight);

            var theColor = hex2rgb(this.state.brushs[this.state.selectBrush].tagColor);

            console.log("thecolor",theColor);

            if (imgData) {
                for (var i = 0, len = imgData.data.length; i < len; i += 4) {
                    // 改变每个像素的透明度
                    var color = imgData.data[i + 3];

                    if (color > 160) {
                        imgData.data[i] = theColor.r;
                        imgData.data[i + 1] = theColor.g;
                        imgData.data[i + 2] = theColor.b;
                        imgData.data[i + 3] = 160;
                    }
                }
                // 将获取的图片数据放回去。
                this.shapeContext.putImageData(imgData, updatex, updatey);
            }
        }

        // console.log(this.state.imageWidth,this.state.imageHeight);

        this.updateContext();
    }

    updateContext(){

        this.imageNode.getContext().drawImage(this.state.image,0,0,this.state.stageWidth,this.state.stageHeight);

        this.imageNode.getContext().drawImage(this.offCanvas,0,0);

        this.previewNode.getContext().clearRect(0,0,this.state.previewWidth,this.state.previewHeight);

        this.previewNode.getContext().drawImage(this.offCanvas,0,0,this.state.stageWidth,this.state.stageHeight,
            0,0,this.state.previewWidth,this.state.previewHeight);

    }

    handleDragStart = e => {
        // console.log("start",e);
        this.state.isTouching = true;
        this.state.lastPoint = this.pointFromEvent(e);
    };

    handleDragEnd = e => {
        // console.log("end",e);
        this.state.isTouching = false;
        this.drawPointTo(this.pointFromEvent(e))


    };

    handleDragMove = e => {

        // console.log(this.state.isTouching,">>>");
        if(this.state.isTouching) {
            // console.log("move", e);
            this.drawPointTo(this.pointFromEvent(e))
        }
    };


    queryImage(){

        if(this.state.recordDetail.imgFilePath) {

            this.state.image.src = apiImageFullUrl(this.state.recordDetail.imgFilePath);

            this.state.image.onload = (doc) => {

                console.log(">>>>>><><><><<",this.imageNode);

                if (this.imageNode) {

                    console.log(doc, this.imageNode.getSize());

                    if (this.imageNode.getSize().width > this.state.maxImageSize ||
                        this.imageNode.getSize().height > this.state.maxImageSize) {

                        var wscale = this.state.maxImageSize / this.imageNode.getSize().width;
                        var hscale = this.state.maxImageSize / this.imageNode.getSize().height;

                        var scale = wscale < hscale ? wscale : hscale;

                        this.state.stageWidth = scale * this.imageNode.getSize().width;
                        this.state.stageHeight = scale * this.imageNode.getSize().height;

                    } else {
                        this.state.stageWidth = this.imageNode.getSize().width;
                        this.state.stageHeight = this.imageNode.getSize().height;
                    }


                    var width = this.imageNode.getSize().width * this.imageNode.getSize().width / this.state.stageWidth;

                    var height = this.imageNode.getSize().height * this.imageNode.getSize().height / this.state.stageHeight;

                    this.state.previewWidth = this.state.stageWidth / 3.0;
                    this.state.previewHeight = this.state.stageHeight / 3.0;

                    this.setState({
                        imageWidth: width,
                        imageHeight: height,
                    });
                }

                this.stageNode.on('mousedown touchstart', this.handleDragStart);
                this.stageNode.on('mouseup touchend', this.handleDragEnd);
                this.stageNode.on('mousemove touchmove', this.handleDragMove);

                var offCanvas = document.createElement("canvas");
                offCanvas.width = this.state.stageWidth;
                offCanvas.height = this.state.stageHeight;
                this.offCanvas = offCanvas;
                this.shapeContext = offCanvas.getContext("2d");
                this.shapeContext.lineJoin = "round";

                //queryfor marketImage
                if(this.state.recordDetail.markImgRecord){

                    this.state.markedImage.src = apiImageFullUrl(this.state.recordDetail.markImgRecord.fileFullPath);
                    this.state.markedImage.crossOrigin = "Anonymous";
                    this.state.markedImage.onload = (doc)=>{

                        console.log("load mark img",doc,this.state.markedImage);

                        this.shapeContext.drawImage(this.state.markedImage,0,0,this.offCanvas.width,this.offCanvas.height);

                        this.setState(this.state,()=>{this.updateContext()});
                    };


                }

            }
        }
    }


    imageInfo(){

        if(this.imageNode) {
            return `image size ${this.imageNode.getSize().width} * ${this.imageNode.getSize().height}`;
        }
    }


    render() {
        return (
            <StarsFrame subTitle="图片标注页面">

                <div className='sketch-play-ground'>


                    <div className="sketch-space">

                        <div>{this.imageInfo()}</div>

                        <div className='draw-space'>

                            <Stage
                                   width={this.state.stageWidth}
                                   height={this.state.stageHeight}
                                   ref={node => {
                                       this.stageNode = node;
                                   }}
                            >
                                <Layer>

                                    <Image
                                        image={this.state.image}
                                        // width={this.state.imageWidth}
                                        // height={this.state.imageHeight}
                                        // cropX={1000}
                                        // cropY={100}
                                        cropWidth={this.state.imageWidth}
                                        cropHeight={this.state.imageHeight}
                                        ref={node => {
                                            this.imageNode = node;

                                        }}
                                    />

                                </Layer>
                            </Stage>

                        </div>

                    </div>

                    <div className='sketch-preview-space'>

                        <div>preview</div>

                        <div style={{width:this.state.previewWidth,
                            height:this.state.previewHeight}}>
                        <Stage
                            style={{backgroundColor:'#000'}}
                            width={this.state.previewWidth}
                            height={this.state.previewHeight}
                        >

                            <Layer>
                                <Shape
                                    ref={node=>{
                                        this.previewNode=node;
                                    }}
                                />
                            </Layer>

                        </Stage>
                        </div>

                        <div style={{marginTop:20,marginBottom:20}}>brush size </div>

                        <Slider
                                step={5}
                                defaultValue={[10, 50]}
                                value={this.state.pointWidth}
                                onChange={(value)=>{

                                    this.setState({pointWidth:value})

                        }} />

                        <div style={{marginTop:20,marginBottom:20}}>标注列表
                            {textButton("+新增标注",()=>{this.setState({addNewMarkSiwtch:true,
                                newMarkName:"newMark",
                                newMarkColor:""})})}

                            {/*{this.state.selectBrush >= 0?*/}

                                {/*<Popconfirm title={"确认删除标注?"}*/}
                                            {/*onConfirm={()=>{*/}

                                                {/*this.state.brushs.splice(this.state.selectBrush,1);*/}
                                                {/*this.state.selectBrush=-1;*/}
                                                {/*this.setState(this.state);*/}

                                            {/*}*/}
                                            {/*}*/}
                                            {/*onCancel={()=>{*/}
                                            {/*}*/}
                                            {/*}*/}
                                            {/*okText="Yes"*/}
                                            {/*cancelText="No">*/}
                                    {/*{*/}
                                        {/*textButton("-删除选中标注",()=>{*/}


                                        {/*})*/}
                                    {/*}*/}
                                {/*</Popconfirm>*/}
                                {/*:<div/>}*/}

                            </div>

                        <div className='sketch-tool-bar'>

                            <Button  type={this.state.selectBrush===-1?'primary':'default'}
                                     style={{marginLeft:10}}
                                     onClick={(value)=>{
                                         this.setState({selectBrush:-1})
                                     }}
                            ><div>擦除</div></Button>

                            {
                                this.renderForBrushs()
                            }

                        </div>

                        <div style={{marginTop:20,marginBottom:20}}>save </div>

                        <Button onClick={()=>{

                            this.onUploadFileCallBack()

                        }}>save</Button>

                     </div>
                </div>

                <Modal
                    title="新增标注"
                    visible={this.state.addNewMarkSiwtch}
                    onOk={this.handleOk.bind(this)}
                    onCancel={this.handleCancel.bind(this)}
                >

                    {
                        this.state.allBrushs.map((item,index)=>{


                            for(var brush of this.state.brushs){

                                if(brush.tagKey == item.tagKey)
                                {
                                    return;
                                }

                            }

                            return <Button key={index+"bucction"} style={{marginTop:10,marginLeft:10}} onClick={()=>{

                                this.state.brushs.push(item);

                                this.setState(this.state);
                            }
                            }>{item.tagName}</Button>


                        })
                    }

                </Modal>

            </StarsFrame>
        )
    }

    dataURLtoBlob(dataurl) {
        var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
        while(n--){
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], {type:mime});
    }


    onUploadFileCallBack(){


        message.loading("保存中...");

        var data = this.offCanvas.toDataURL('image/jpg');

        var blob = this.dataURLtoBlob(data);

        apiUploadImage(blob,this.state.recordId,this.state.brushs,(ret)=>{

            message.destroy();
            message.success("保存成功");
            console.log(ret);
        })

    }

    handleOk(){

        this.handleCancel();
    }

    handleCancel(){
        this.setState({addNewMarkSiwtch:false})
    }

    brushColums(){

        const data = [{
            title: '标注名称',
            dataIndex: 'label',
            key: 'label'
        }, {
            title: '颜色值',
            dataIndex: 'color',
            key: 'color',
            render: color => (
                <div style={{width:20,height:20,backgroundColor:color}}/>
            )
        }, {
            title: '选择',
            key:'operation',
            render:(value,item,index)=>{
                return <div>
                    <Switch checked={index===this.state.selectBrush}
                            onChange={(value)=>{

                                this.setState({selectBrush:index})
                            }
                            }
                    />

                </div>
            }
        }];


        return data;
    }

    resetContextAlphaByRect(context,x,y,width,height,alpha){

        var imgData = this.shapeContext.getImageData(x, y, width, height);

        if (imgData) {
            for (var i = 0, len = imgData.data.length; i < len; i += 4) {
                // 改变每个像素的透明度
                var color = imgData.data[i + 3];

                if (color > alpha) {
                    imgData.data[i + 3] = alpha;
                }
            }
            // 将获取的图片数据放回去。
            this.shapeContext.putImageData(imgData, x, y);
        }
    }

    //tool bar
    renderForBrushs(){

        return this.state.brushs.map((item,index)=>{

            return <Button  type={this.state.selectBrush===index?'primary':'default'}
                            style={{marginLeft:10}}
                            onClick={(value)=>{
                                     this.setState({selectBrush:index})
                                 }}
            >{item.tagName}
                <div style={{backgroundColor:item.tagColor,marginBottom:5,width:30,height:10}}/></Button>

        })
    }
}

export default App;