import React, { Component}  from 'react';
import "./gstyle.css"
/* 点击文字 */
export function textButton(text,ret) {

    return (<div className="g-text-button" onClick={() => {
        ret();
    }}>{text}</div>);

}


export function hex2rgb(color){

    var sColor = color.toLowerCase();
    var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;

    var color = {r:0,g:0,b:0};

    if(sColor && reg.test(sColor)){

        if(sColor.length === 4){
            var sColorNew = "#";
            for(var i=1; i<4; i+=1){
                sColorNew += sColor.slice(i,i+1).concat(sColor.slice(i,i+1));
            }
            sColor = sColorNew;
        }
        //处理六位的颜色值
        var sColorChange = [];
        for(var i=1; i<7; i+=2){
            sColorChange.push(parseInt("0x"+sColor.slice(i,i+2)));
        }

        // return "rgb(" + sColorChange.join(",") + ")";

        if(sColorChange.length ==3){
            color.r = sColorChange[0];
            color.g = sColorChange[1];
            color.b = sColorChange[2];
        }

    }

    return color;
};