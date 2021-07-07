package main;

import main.model.Deal;
import main.model.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

//@Controller
//public class DefaultController {
//
//
//    @Autowired
//    DealRepository dealRepository;
//
//
//    @GetMapping("/")
//    public String index(Model model) {
//        Iterable<Deal> dealIterable = dealRepository.findAll();
//       ArrayList<Deal> deals = new ArrayList<>();
//        for(Deal deal : dealIterable){
//            deals.add(deal);
//        }
//        model.addAttribute("deals",deals);
//        model.addAttribute("dealsCount",deals.size());
//        return "index";
//    }
//
//
//}
