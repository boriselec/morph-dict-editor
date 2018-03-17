package com.boriselec.morphdict.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("/")
@RequestMapping("/")
public class ViewController {
    @RequestMapping(method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String singePage(){
        return "main";
    }
}
