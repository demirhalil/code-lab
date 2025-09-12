package decoratorpattern.decorator;

import decoratorpattern.fundamental.Coffee;

import java.math.BigDecimal;

public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;

    public CoffeeDecorator(final Coffee coffee) {
        this.coffee = coffee;
    }
    @Override
    public String getDescription() {
        return coffee.getDescription();
    }

    @Override
    public BigDecimal getCost() {
        return coffee.getCost();
    }
}
