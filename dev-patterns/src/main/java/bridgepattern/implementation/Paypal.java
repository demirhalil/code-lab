package bridgepattern.implementation;

import java.math.BigDecimal;

public class Paypal implements Payment{
    @Override
    public void pay(final BigDecimal amount) {
        System.out.println("Paying " + amount + " with Paypal");
    }
}
