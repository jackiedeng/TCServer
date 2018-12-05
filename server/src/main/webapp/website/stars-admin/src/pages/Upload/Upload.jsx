import React, {Component} from 'react';
import {apiUploadImage,apiGetAllTagList} from "../../api/api"

class Upload extends Component {

    uploadTest(){

        console.log("update test!!!!");

        apiUploadImage({file:"String",fileName:"1.txt"},function (ret) {

            console.log(ret);
        })


        apiGetAllTagList(function (ret) {

            console.log("loog",ret);
        })
    }


    render() {
        return (
            <div onClick={()=>{

                this.uploadTest()

            }}>
                onclick
            </div>
        );
    }
}

export default Upload;