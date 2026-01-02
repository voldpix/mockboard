package dev.mockboard.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = {
            "/{path:[^.]*}",
            "/**/{path:[^.]*}"
    })
    public String forwardToSpa(@PathVariable String path) {
        return "forward:/index.html";
    }
}
