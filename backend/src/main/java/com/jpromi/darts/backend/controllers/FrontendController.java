package com.jpromi.darts.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping(value = {
            "/",
            "/{path:[^\\.]*}",
            "/{a:[^\\.]+}/{path:[^\\.]*}",
            "/{a:[^\\.]+}/{b:[^\\.]+}/{path:[^\\.]*}",
            "/{a:[^\\.]+}/{b:[^\\.]+}/{c:[^\\.]+}/{path:[^\\.]*}"
    })
    public String handleForward() {
        return "forward:/index.html";
    }
}
