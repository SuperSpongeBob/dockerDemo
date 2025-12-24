package org.example.dockerdemo.controller;

import org.example.dockerdemo.entity.TestUser;
import org.example.dockerdemo.service.TestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    private TestUserService testUserService;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping("/user/{id}")
    public TestUser selectUserById(@PathVariable String id) {
        return testUserService.getById(id);
    }

    @RequestMapping("/user2")
    public TestUser selectUserById2(String id) {
        return testUserService.getById(id);
    }

    @RequestMapping("/user3")
    public TestUser selectUserById3(@RequestParam("userId") String id) {
        return testUserService.getById(id);
    }
}
