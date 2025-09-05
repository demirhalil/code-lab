package bridgepattern.abstraction;

import bridgepattern.implementation.Payment;

import java.math.BigDecimal;

public class SubscriptionPurchase extends Purchase{
    public SubscriptionPurchase(final Payment payment) {
        super(payment);
    }

    @Override
    public void purchase(final BigDecimal amount) {
        System.out.println("Processing subscription purchase of " + amount);
        payment.pay(amount);
    }
}
