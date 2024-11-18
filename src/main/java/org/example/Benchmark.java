package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class Benchmark {

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(Benchmark.class.getSimpleName())
                .result("jmh-result.json")
                .resultFormat(ResultFormatType.JSON)
                .threads(10)
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

    @Param({"100", "100", "100", "100", "100"})
    private int clients;

    @Param({"1000"})
    private int seats;

    private ThreadSafeHashMap tsHashMap;
    private ConcurrentHashMap<Integer, Boolean> concHashMap;
    private Random random;

    @Setup(Level.Trial)
    public void setupBenchmark() {
        tsHashMap = new ThreadSafeHashMap(seats);
        concHashMap = new ConcurrentHashMap<>(seats);
        random = new Random();

        // Initialize both hashmaps with available seats
        for (int i = 0; i < seats; i++) {
            tsHashMap.put(i, true);
            concHashMap.put(i, true);
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkThreadSafeHashMap() {
        for (int i = 0; i < clients; i++) {
            int seat = random.nextInt(seats);
            if (random.nextBoolean()) {
                // Read
                tsHashMap.get(seat);
            } else {
                // Write
                boolean available = tsHashMap.get(seat);
                if (available) {
                    tsHashMap.put(seat, false);
                }
            }
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkConcurrentHashMap() {
        for (int i = 0; i < clients; i++) {
            int seat = random.nextInt(seats);
            if (random.nextBoolean()) {
                // Read
                concHashMap.get(seat);
            } else {
                // Write
                boolean available = concHashMap.get(seat);
                if (available) {
                    concHashMap.put(seat, false);
                }
            }
        }
    }
}
