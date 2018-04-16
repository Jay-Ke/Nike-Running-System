package com.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldRestController {

    @RequestMapping(value="/hello",method = RequestMethod.GET)
    public String helloWorld(){
        return "Hello Nike Running";
    }
}
