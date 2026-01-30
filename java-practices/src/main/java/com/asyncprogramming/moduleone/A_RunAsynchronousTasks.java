package com.asyncprogramming.moduleone;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class A_RunAsynchronousTasks {
    private record Quotation(String server, int amount){}

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        run();
    }

    public static void run() throws ExecutionException, InterruptedException {
        Random random = new Random();

        Callable<Quotation> fetchQuotationA = () -> {
            Thread.sleep(random.nextInt(80, 120));
            return new Quotation("Server A", random.nextInt(40, 60));
        };

        Callable<Quotation> fetchQuotationB = () -> {
            Thread.sleep(random.nextInt(80, 120));
            return new Quotation("Server B", random.nextInt(30, 70));
        };

        Callable<Quotation> fetchQuotationC = () -> {
            Thread.sleep(random.nextInt(80, 120));
            return new Quotation("Server C", random.nextInt(40, 80));
        };

        var quotationTasks = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        var executor = Executors.newFixedThreadPool(4);

        Instant begin = Instant.now();

        var futures = new ArrayList<Future<Quotation>>();
        for (Callable<Quotation> task : quotationTasks) {
            final Future<Quotation> future = executor.submit(task);
            futures.add(future);
        }

        List<Quotation> quotations = new ArrayList<>();
        for (final Future<Quotation> future : futures) {
            final Quotation quotation = future.get();
            quotations.add(quotation);
        }

        var bestQuotation = quotations.stream()
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();

        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);

        System.out.println("Best quotation [ES ] = " + bestQuotation +
                " (" + duration.toMillis() + "ms");
    }

    private static Quotation fetchQuotation(final Callable<Quotation> task) {
        try {
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
