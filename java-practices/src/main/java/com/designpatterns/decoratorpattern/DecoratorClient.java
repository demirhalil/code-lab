package com.designpatterns.decoratorpattern;

import com.designpatterns.decoratorpattern.decorator.MilkDecorator;
import com.designpatterns.decoratorpattern.decorator.SugarDecorator;
import com.designpatterns.decoratorpattern.decorator.WhippedCreamDecorator;
import com.designpatterns.decoratorpattern.fundamental.BasicCoffee;
import com.designpatterns.decoratorpattern.fundamental.Coffee;

public class DecoratorClient {

    public static void main(String[] args) {
        Coffee coffee = new BasicCoffee();
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());

        // Add milk
        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());

        // Add sugar
        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());

        // Add whipped cream
        coffee = new WhippedCreamDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
    }
}
