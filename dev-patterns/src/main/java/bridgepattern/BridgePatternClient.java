package bridgepattern;

import bridgepattern.abstraction.OneTimePurchase;
import bridgepattern.abstraction.Purchase;
import bridgepattern.abstraction.SubscriptionPurchase;
import bridgepattern.implementation.CreditCard;
import bridgepattern.implementation.Paypal;
import bridgepattern.implementation.Stripe;

import java.math.BigDecimal;

public class BridgePatternClient {
    public static void main(String[] args) {
        Purchase oneTime = new OneTimePurchase(new Paypal());
        oneTime.purchase(new BigDecimal("100.00"));

        Purchase subscription = new SubscriptionPurchase(new Stripe());
        subscription.purchase(new BigDecimal("100.00"));

        Purchase creditCard = new OneTimePurchase(new CreditCard());
        creditCard.purchase(new BigDecimal("100.00"));
    }
}
