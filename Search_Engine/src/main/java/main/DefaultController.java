package main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class DefaultController {


    @RequestMapping("/YahoooSearchBot")
    public String index() {
        return "index";
    }


}
