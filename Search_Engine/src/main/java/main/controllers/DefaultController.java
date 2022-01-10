package main.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class DefaultController {


    @RequestMapping("/VEGA")
    public String index() {
        return "index";
    }


}
