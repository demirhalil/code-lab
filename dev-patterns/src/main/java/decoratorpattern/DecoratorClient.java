package decoratorpattern;

import decoratorpattern.decorator.MilkDecorator;
import decoratorpattern.decorator.SugarDecorator;
import decoratorpattern.decorator.WhippedCreamDecorator;
import decoratorpattern.fundamental.BasicCoffee;
import decoratorpattern.fundamental.Coffee;

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
