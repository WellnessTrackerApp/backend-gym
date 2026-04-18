package com.gymtracker.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "Hello World!";
    }
}
