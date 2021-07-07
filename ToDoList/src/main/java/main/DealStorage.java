package main;

import org.springframework.stereotype.Component;
import main.model.Deal;
import org.springframework.stereotype.Repository;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.atomic.AtomicInteger;
@Component
public class DealStorage  {
    private final AtomicInteger currentId = new AtomicInteger(1);
    private  ConcurrentHashMap<Integer, Deal> dealsList = new ConcurrentHashMap<Integer, Deal>();

    public  ConcurrentHashMap<Integer, Deal> getDealsList() {
        return dealsList;
    }

    public Vector<Deal> getAllDeals(){
        return new Vector<Deal>(dealsList.values());
    }

    public  int addDeal(Deal deal) {
        int id = currentId.getAndIncrement();
        deal.setId(id);
        dealsList.put(id, deal);
        return id;

    }
    public  Deal getDeal(int dealId)
    {
        return dealsList.getOrDefault(dealId, null);
    }
}
