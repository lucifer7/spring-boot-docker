package com.yang.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Usage: <b> A simple Restful controller for Docker test </b>
 *
 * @author Jingyi.Yang
 *         Date 2016/11/9
 **/
@RestController
public class SimpleController {
    @RequestMapping("/docker")
    public String dockerTest() {
        return "Hello Spring Boot with Docker.";
    }
}
