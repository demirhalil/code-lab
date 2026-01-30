package com.asyncprogramming.moduleone;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class A_UsingCompletionStage {

    private record Quotation(String server, int amount){}

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        Random random = new Random();

        Supplier<Quotation> fetchQuotationA = () -> {
            try {
                Thread.sleep(random.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Server A", random.nextInt(40, 60));
        };

        Supplier<Quotation> fetchQuotationB = () -> {
            try {
                Thread.sleep(random.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Server B", random.nextInt(30, 70));
        };

        Supplier<Quotation> fetchQuotationC = () -> {
            try {
                Thread.sleep(random.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Server C", random.nextInt(40, 80));
        };

        var quotationTasks = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        Instant begin = Instant.now();

        List<CompletableFuture<Quotation>> futures = new ArrayList<>();
        for (final Supplier<Quotation> task : quotationTasks) {
            var future = CompletableFuture.supplyAsync(task);
            futures.add(future);
        }

        List<Quotation> quotations = new ArrayList<>();
        for (final CompletableFuture<Quotation> future : futures) {
            final Quotation quotation = future.join();
            quotations.add(quotation);
        }

        var bestQuotation = quotations.stream()
                .min(Comparator.comparing(Quotation::server))
                .orElseThrow();

        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("Best quotation [ASYNC] =" + bestQuotation +
                " (" + duration.toMillis() + "ms");
    }
}
