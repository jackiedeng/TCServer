package com.springapp.mvc;

import com.alibaba.fastjson.JSON;
import com.starts.util.LogUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/test.do")
public class HelloController {
	@RequestMapping(method =RequestMethod.GET)
	@ResponseBody
	public String printWelcome(@RequestParam(value="name",required=false) String name,
							   @RequestParam(value ="id",required = false) Long id) {
//		model.addAttribute("message", "Hello From S.T.A.R.S!");

//		LogUtil.debug.warn("hello");
//		LogUtil.debug.warn("hello {}","12353213");

		Map<String,String > welcomeMap = new HashMap<>();
		welcomeMap.put("hello","12324");
		welcomeMap.put("hellp2","3214");
		welcomeMap.put("name",name==null?"null":name);
		welcomeMap.put("id",String.valueOf(id==null?0:id));

		return JSON.toJSONString(welcomeMap);
	}
}