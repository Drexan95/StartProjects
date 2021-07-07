package main;

import main.model.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import main.model.Deal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


@Component
@Controller
public class DealController {
    @Autowired
    private final DealRepository dealRepository;
    SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
    public DealController(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }



    @GetMapping("/")
    public String index(Model model) {
        Iterable<Deal> dealIterable = dealRepository.findAll();
        ArrayList<Deal> deals = new ArrayList<>();
        for(Deal deal : dealIterable){
            deals.add(deal);
        }
        model.addAttribute("deals",deals);
        model.addAttribute("dealsCount",deals.size());
        return "index";
    }

    @PostMapping("/")
    public ResponseEntity<?> add(Deal deal) {
        dealRepository.save(deal);
        return new ResponseEntity<>(deal, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeal(@PathVariable int id) {
        if (dealRepository.findById(id).isPresent()) {
            return new ResponseEntity<>(dealRepository.findById(id).get(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> replaceDeal(@PathVariable int id, @RequestParam String newName) {
        if (dealRepository.findById(id).isPresent()) {
            Deal deal = dealRepository.findById(id).get();
            deal.setName(newName);
            deal.setDate(fmt.format(Calendar.getInstance().getTime()));
            dealRepository.save(deal);
            return new ResponseEntity<>( deal, HttpStatus.CREATED);
        } else{
            Deal newDeal = new Deal();
            this.add(newDeal);
            newDeal.setId(id);
            newDeal.setName(newName);
            dealRepository.save(newDeal);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(dealRepository.findById(id).get());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateDeal(@PathVariable int id, @RequestParam String newName) {
        if (dealRepository.findById(id).isPresent()) {
            Deal deal = dealRepository.findById(id).get();
            deal.setName(newName);
            dealRepository.save(deal);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeal(@PathVariable int id) {
        if (dealRepository.findById(id).isPresent()){
            dealRepository.deleteById(id);
            return  ResponseEntity.status(HttpStatus.OK).body(null);
        } else  return  ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAllDeals(){
      dealRepository.deleteAll();
        return   ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
