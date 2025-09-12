package decoratorpattern.fundamental;

import java.math.BigDecimal;

public class BasicCoffee implements Coffee{
    @Override
    public String getDescription() {
        return "Basic Coffee";
    }

    @Override
    public BigDecimal getCost() {
        return BigDecimal.TWO;
    }
}
