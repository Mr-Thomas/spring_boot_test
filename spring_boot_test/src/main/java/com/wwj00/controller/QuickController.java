package com.wwj00.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Nancy on 2019/7/29 14:14
 */
@Controller
@ResponseBody
public class QuickController {

    @RequestMapping("/quick")
    public String quick(){
        return "hello,springboot";
    }
}
