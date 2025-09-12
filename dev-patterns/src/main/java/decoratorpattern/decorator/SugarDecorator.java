package decoratorpattern.decorator;

import decoratorpattern.fundamental.Coffee;

import java.math.BigDecimal;

public class SugarDecorator extends CoffeeDecorator{
    public SugarDecorator(final Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " with sugar";
    }

    @Override
    public BigDecimal getCost() {
        return super.getCost().add(BigDecimal.ONE);
    }
}
