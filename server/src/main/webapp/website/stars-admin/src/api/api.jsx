import React, { Component}  from 'react';
import message from "antd/lib/message"
import 'whatwg-fetch'

var ISJSONP = true;

export function sysErrCallback(ret) {
    // console.error(ret);
    if (ret) {
        message.error('error',2000);
    } else {
        message.error('网络或系统异常',2000);
    }
}


export function getCurrentHost(){

    // return 'http://localhost:8080'
    return ''
}

export function mixData(url,data) {

    var subKey = "";

    for(var key in data){

        if(subKey == ""){
            subKey += "?"+key+"="+data[key];
        }else{
            subKey += "&"+key+"="+data[key];
        }
    }

    return url+subKey;
}

function _queryData(url,data, succ) {

    var hostUrl = getCurrentHost()+url;

    hostUrl = mixData(hostUrl,data);

    console.log("url:::",hostUrl);

    fetch(hostUrl, {
        method: "GET",
        mode: "cors",
        headers:{
            'Accept':'application/json,text/plain,*/*',
            "content-type":"text/html;charset=utf-8"
        }

    }).then(response => response.text())
        .then(result => {
            // 在此处写获取数据之后的处理逻辑
            console.log(result);
            var jsonResult  = result;
            try{
                jsonResult = JSON.parse(jsonResult);
            }catch(e){
                jsonResult = result;
            }
            succ(jsonResult);

        }).catch(function (e) {
        console.log("fetch fail");
        sysErrCallback(e);
    });
}

function _PostData(url,data, succ) {

    var hostUrl = getCurrentHost()+url;

    // hostUrl = mixData(hostUrl,data);
    const formData = new FormData()
    for(var key in data){
        formData.append(key,data[key]);
    }
    // formData.append('file', file)

    console.log("url:::",hostUrl);

    fetch(hostUrl, {
        method: "POST",
        body:formData,
        mode: "no-cors"

    }).then(response => response.text())
        .then(result => {
            // 在此处写获取数据之后的处理逻辑
            console.log(result);
            var jsonResult  = result;
            try{
                jsonResult = JSON.parse(jsonResult);
            }catch(e){
                jsonResult = result;
            }
            succ(jsonResult);

        }).catch(function (e) {
        console.log("fetch fail");
        sysErrCallback(e);
    });
}


function _queryDataOut(url,data, succ) {

    fetch(url, {
        method: "GET",
        mode: "cors",
        headers:{
            'Accept':'application/json,text/plain,*/*',
            "content-type":"text/html;charset=utf-8"
        }

    }).then(response => response.text())
        .then(result => {
            // 在此处写获取数据之后的处理逻辑
            console.log(result);
            var jsonResult  = result;
            try{
                jsonResult = JSON.parse(jsonResult);
            }catch(e){
                jsonResult = result;
            }
            succ(jsonResult);

        }).catch(function (e) {
        console.log("fetch fail");
        sysErrCallback(e);
    });

}

export function apiImageFullUrl(url){

    return getCurrentHost()+url;
}


//查询所有
export function apiGetAllTagList(ret){
    _queryData("/request/pool/queryAll.do",{a:1,b:2},ret);
}


//删除
export function apiDeleteTagByKey(key,ret){

    _queryData("/request/pool/delete.do",{poolKey:key},ret);
}

//新增标签
export function apiCreateNewTag(tagname,tagkey,ret){
    _queryData("/request/pool/add.do",{poolKey:tagkey,poolName:tagname},ret);
}

//请求图片资源目录
export function apiImageFolderQuery(ret) {

    _queryData("/imageFolder/queryFolder.do",{},ret);
}

//请求标签详情
export function apiQueryTagDetail(tagKey,suc) {

    _queryData("/imageFolder/queryTagDetail.do",{tagId:tagKey},suc);
}

//请求图片资源列表
export function apiQueryForImageListForFolder(folder,index,size,tag,suc){

    _queryData("/imageFolder/query.do",{path:folder,index:index,size:size,tag:tag},suc);
}

//给图片打标
export function apiAddTagToImage(name,path,tag,suc){

    _queryData("/tag/insertTagToImage.do",{imageName:name,filePath:path,tag:tag},suc);
}

//去除标
export function apiRemoveTagToImage(name,tag,suc) {

    _queryData("/tag/removeTagToImage.do",{imageName:name,tag:tag},suc);
}

//已经打标的图片列表
export function apiTagContainsImageListQuery(tag,index,size,suc) {
    _queryData("/tag/queryList.do",{tag:tag,index:index,size:size},suc);
}



//上传图片
export function apiUploadImage(data,fileName,tags,suc) {

    _PostData("/imageUpload.do",{data:data,fileName:fileName,tags:JSON.stringify(tags)},suc);
}

//查询单张图片记录
export function queryImgDetail(fileName,suc){

    _queryData('/tag/queryDetail.do',{imageName:fileName},suc)
}

//新增标签
export function apiNewOrUpdateMarkTag(tagName,tagKey,color,ext,suc){

    console.log(tagName,tagKey,encodeURIComponent(color),ext);

    _queryData("/request/markTag/update.do",{tagName:tagName,tagKey:tagKey,tagColor:encodeURIComponent(color),ext:encodeURIComponent(ext)},suc);
}

//查询标签
export function apiQueryAllMarkTag(suc) {

    _queryData('/request/common/queryAll.do',{type:"markTag"},suc)
}

//删除标注
export function apiDeleteMarkTag(tagName,suc) {

    _queryData('/request/markTag/delete.do',{tagName:tagName},suc);

}