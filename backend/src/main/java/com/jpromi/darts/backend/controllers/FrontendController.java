package com.jpromi.darts.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping(value = {
        "/",
        "/{path:^(?!api$|ws$)[^\\.]*}",
        "/{a:^(?!api$|ws$)[^\\.]+}/{path:[^\\.]*}",
        "/{a:^(?!api$|ws$)[^\\.]+}/{b:[^\\.]+}/{path:[^\\.]*}",
        "/{a:^(?!api$|ws$)[^\\.]+}/{b:[^\\.]+}/{c:[^\\.]+}/{path:[^\\.]*}"
    })
    public String handleForward() {
        return "forward:/index.html";
    }
}
