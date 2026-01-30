package com.designpatterns.bridgepattern.abstraction;

import com.designpatterns.bridgepattern.implementation.Payment;

import java.math.BigDecimal;

public class OneTimePurchase extends Purchase{

    public OneTimePurchase(final Payment payment) {
        super(payment);
    }

    @Override
    public void purchase(final BigDecimal amount) {
        System.out.println("Processing one time purchase of " + amount);
        payment.pay(amount);
    }
}
