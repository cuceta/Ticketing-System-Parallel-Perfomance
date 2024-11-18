//package org.example;
//
//import org.openjdk.jmh.annotations.*;
//
//import java.util.Random;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
//@State(Scope.Benchmark)
//public class TicketingBenchmark {
//    private static final int NUM_SEATS = 1000;
//    private ThreadSafeHashMap tsHashMap;
//    private ConcurrentHashMap<Integer, Boolean> concHashMap;
//    private Random random;
//
//    @Param({"50", "100", "200"}) // Different load scenarios
//    private int numThreads;
//
//    @Setup
//    public void setup() {
//        tsHashMap = new ThreadSafeHashMap(NUM_SEATS);
//        concHashMap = new ConcurrentHashMap<>(NUM_SEATS);
//        random = new Random();
//
//        // Initialize both hashmaps with available seats
//        for (int i = 0; i < NUM_SEATS; i++) {
//            tsHashMap.put(i, true);
//            concHashMap.put(i, true);
//        }
//    }
//
//    @Benchmark
//    @BenchmarkMode(Mode.Throughput)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    @Threads(Threads.MAX)
//    public void testTsHashMap() {
//        int seat = random.nextInt(NUM_SEATS);
//        if (random.nextBoolean()) {
//            tsHashMap.get(seat);
//        } else {
//            boolean available = tsHashMap.get(seat);
//            if (available) {
//                tsHashMap.put(seat, false);
//            }
//        }
//    }
//
//    @Benchmark
//    @BenchmarkMode(Mode.Throughput)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    @Threads(Threads.MAX)
//    public void testConcHashMap() {
//        int seat = random.nextInt(NUM_SEATS);
//        if (random.nextBoolean()) {
//            concHashMap.get(seat);
//        } else {
//            boolean available = concHashMap.get(seat);
//            if (available) {
//                concHashMap.put(seat, false);
//            }
//        }
//    }
//}
