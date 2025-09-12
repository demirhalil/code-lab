package decoratorpattern.decorator;

import decoratorpattern.fundamental.Coffee;

import java.math.BigDecimal;

public class WhippedCreamDecorator extends CoffeeDecorator{
    public WhippedCreamDecorator(final Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " with whipped cream";
    }

    @Override
    public BigDecimal getCost() {
        return super.getCost().add(BigDecimal.ONE);
    }
}
