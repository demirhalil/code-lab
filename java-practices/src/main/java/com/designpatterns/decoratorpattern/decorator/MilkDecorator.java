package com.designpatterns.decoratorpattern.decorator;

import com.designpatterns.decoratorpattern.fundamental.Coffee;

import java.math.BigDecimal;

public class MilkDecorator extends CoffeeDecorator{
    public MilkDecorator(final Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " with milk";
    }

    @Override
    public BigDecimal getCost() {
        return super.getCost().add(BigDecimal.ONE);
    }
}
