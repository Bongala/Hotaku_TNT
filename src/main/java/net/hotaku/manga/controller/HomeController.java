package net.hotaku.manga.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class HomeController {
    @GetMapping("/")
    public String homepage() {
        return "homepage";
    }
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
