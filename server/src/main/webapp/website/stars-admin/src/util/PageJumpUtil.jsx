import React from 'react';
import {mixData} from '../api/api'

export var pageList = {

    home:"/",
    tagDetail:"/imgPool",
    sketchStudio:"/sketch",
    tagManage:"/datatag",
    train:"/train"
};


export function jump2page(page,query){

    var url = mixData(page,query);

    console.log("jump->",url)

    window.open(url,"_self");

}