package com.project.userService.controllers.testAuth;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tg-auth")
public class Auth {

    @GetMapping
    public void auth(@RequestBody(required = false) Object json) {
        System.out.println("get " + json);

    }

    @PostMapping
    public void authPost(@RequestBody(required = false) Object json) {
        System.out.println("post " + json);
    }
}
