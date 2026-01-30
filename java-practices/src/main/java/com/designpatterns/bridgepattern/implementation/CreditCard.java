package com.designpatterns.bridgepattern.implementation;

import java.math.BigDecimal;

public class CreditCard implements Payment{
    @Override
    public void pay(final BigDecimal amount) {
        System.out.println("Paying " + amount + " with Credit Card");
    }
}
