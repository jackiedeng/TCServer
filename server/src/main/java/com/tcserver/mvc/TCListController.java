package com.tcserver.mvc;

import com.alibaba.fastjson.JSON;
import com.starts.util.Result;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import com.tcserver.mvc.tcDO.ListDO;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

@Controller
@CrossOrigin
public class TCListController {

    @RequestMapping(value = "/tcserver/list.do", method = RequestMethod.GET)
    @ResponseBody
    public String getHtmlBody() throws IOException {


        ArrayList<ListDO> listDOArray = new ArrayList<>();

        for(int i = 0; i < 30; i++){

            ListDO listDO = new ListDO();

            listDO.setName("name"+i);
            listDO.setExt("test");
            listDO.setImage("http://img5.imgtn.bdimg.com/it/u=2822826686,3286551163&fm=26&gp=0.jpg");

            listDOArray.add(listDO);
        }

        Result<ArrayList<ListDO>> result = new Result<>();

        result.setData(listDOArray);
        result.setSuccess(true);

        return JSON.toJSONString(result);
    }

}