package bridgepattern.abstraction;

import bridgepattern.implementation.Payment;

import java.math.BigDecimal;

public abstract class Purchase {
    protected Payment payment;

    public Purchase(final Payment payment) {
        this.payment = payment;
    }

    public abstract void purchase(BigDecimal amount);
}
