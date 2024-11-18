package org.example;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TicketingSystem {
    private static final int NUM_SEATS = 1000;

    public static void main(String[] args) throws InterruptedException {
        int[] threadCounts = {50, 100, 200};  // Different load scenarios
        int numOperations = 10000;  // Number of operations per thread

        for (int numThreads : threadCounts) {
            System.out.println("Running with " + numThreads + " threads:");
            runTest(numThreads, numOperations);
        }
    }

    public static void runTest(int numThreads, int numOperations) throws InterruptedException {
        ThreadSafeHashMap tsHashMap = new ThreadSafeHashMap(NUM_SEATS);
        ConcurrentHashMap<Integer, Boolean> concHashMap = new ConcurrentHashMap<>(NUM_SEATS);

        // Initialize both hashmaps with seats
        for (int i = 0; i < NUM_SEATS; i++) {
            tsHashMap.put(i, true);
            concHashMap.put(i, true);
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        Random random = new Random();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < numOperations; j++) {
                    int seat = random.nextInt(NUM_SEATS);

                    // 50% chance to read or write
                    if (random.nextBoolean()) {
                        // Read
                        tsHashMap.get(seat);
                        concHashMap.get(seat);
                    } else {
                        // Write
                        boolean available = tsHashMap.get(seat);
                        if (available) {
                            tsHashMap.put(seat, false);
                        }

                        available = concHashMap.get(seat);
                        if (available) {
                            concHashMap.put(seat, false);
                        }
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
    }
}
