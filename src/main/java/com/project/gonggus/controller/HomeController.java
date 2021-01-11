package com.project.gonggus.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://gonggus.cf", allowCredentials = "true")
public class HomeController {

    @RequestMapping("/")
    public String home(){
        return "home";
    }
}
