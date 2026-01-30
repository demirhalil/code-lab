package com.fastpass.controller;

import com.fastpass.domain.FastPassCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FastPassController {

    private final List<FastPassCustomer> customers;
    private final Logger logger = LoggerFactory.getLogger(FastPassController.class);

    public FastPassController() {
        customers = new ArrayList<>();
        customers.add(new FastPassCustomer("800", "Omar Zidan", "555-123-4567", 19.5f));
        customers.add(new FastPassCustomer("801", "Maggie Well", "555-321-7654", 11.5f));
        customers.add(new FastPassCustomer("802", "Omar Tiffany wallace", "555-987-6543", 9.5f));
    }

    @RequestMapping("/fastpass")
    public FastPassCustomer getFastPassById(@RequestParam String fastPassId) {
        logger.info("Fastpast customer is requested: {}", fastPassId);
        return customers.stream()
                .filter(customer -> fastPassId.equalsIgnoreCase(customer.fastPassId()))
                .findAny().get();
    }
}
