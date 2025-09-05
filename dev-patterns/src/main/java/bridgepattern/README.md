# Bridge Design Pattern
Separate abstraction from implementation so both can vary independently 

## What is the Bridge Pattern?
The Bridge Pattern is a structural design pattern that decouples an abstraction from its implementation, allowing both to evolve independently. Instead of creating a rigid inheritance hierarchy, it uses composition to "bridge" two separate class hierarchies.

## The Problem
Without the Bridge Pattern, you might end up with an explosion of classes:

❌ Without Bridge Pattern:
- StripeOneTimePurchase
- PaypalOneTimePurchase  
- CreditCardOneTimePurchase
- StripeSubscriptionPurchase
- PaypalSubscriptionPurchase
- CreditCardSubscriptionPurchase
## The Solution
The Bridge Pattern solves this problem by creating a bridge between the two class hierarchies.

✅ With Bridge Pattern:
- 2 Purchase types (OneTime, Subscription)
- 3 Payment methods (Stripe, Paypal, CreditCard)
- Total: 5 classes instead of 6+

Abstraction Hierarchy          Implementation Hierarchy
┌─────────────┐                ┌─────────────┐
│   Purchase  │◇──────────────▷│   Payment   │
└─────────────┘                └─────────────┘
△                              △
│                              │
┌─────────────┐                ┌─────────────┐
│ OneTimePur- │                │   Stripe    │
│   chase     │                └─────────────┘
└─────────────┘                ┌─────────────┐
┌─────────────┐                │   Paypal    │
│Subscription-│                └─────────────┘
│  Purchase   │                ┌─────────────┐
└─────────────┘                │ CreditCard  │
                               └─────────────┘



## When to Use Bridge Pattern
### ✅ Use Bridge When:
- You have **two dimensions of variation** (what + how)
- You need to **switch implementations at runtime**
- You want to **avoid class explosion**
- **Both hierarchies change independently**


