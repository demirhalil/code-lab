package com.designpatterns.bridgepattern;

import com.designpatterns.bridgepattern.abstraction.OneTimePurchase;
import com.designpatterns.bridgepattern.abstraction.Purchase;
import com.designpatterns.bridgepattern.abstraction.SubscriptionPurchase;
import com.designpatterns.bridgepattern.implementation.CreditCard;
import com.designpatterns.bridgepattern.implementation.Paypal;
import com.designpatterns.bridgepattern.implementation.Stripe;

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
