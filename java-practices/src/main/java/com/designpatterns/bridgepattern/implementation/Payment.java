package com.designpatterns.bridgepattern.implementation;

import java.math.BigDecimal;

public interface Payment {
    void pay(BigDecimal amount);
}
