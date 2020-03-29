package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    private static Random rn = new Random();

    private MatrixUtil() {
    }

    public static int[][] concurrentMultiplyParallelStream(int[][] matrixA, int[][] matrixB, int threadNumbers) throws ExecutionException, InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[][] matrixBT = getTMatrix(matrixB, matrixSize);

        new ForkJoinPool(threadNumbers).submit(() ->
                IntStream.range(0, matrixSize)
                        .parallel()
                        .forEach(i -> mainLoop(i, matrixA[i], matrixBT, matrixC, matrixSize))).get();
        return matrixC;
    }

     public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[][] matrixBT = getTMatrix(matrixB, matrixSize);

        List<Callable<Void>> result = new ArrayList<>(matrixSize);

        for (int i = 0; i < matrixSize; i++) {
            int finalI = i;
            result.add(() -> {
                mainLoop(finalI, matrixA[finalI], matrixBT, matrixC, matrixSize);
                return null;
            });
        }

        executor.invokeAll(result);

        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[][] matrixBT = getTMatrix(matrixB, matrixSize);

        for (int i = 0; i < matrixSize; i++) {
            mainLoop(i, matrixA[i], matrixBT, matrixC, matrixSize);
        }
        return matrixC;
    }

    private static int[][] getTMatrix(int[][] matrixB, int matrixSize) {
        int[][] matrixBT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }
        return matrixBT;
    }

    private static void mainLoop(int i, int[] rowA, int[][] matrixBT, int[][] matrixC, int matrixSize) {
        for (int j = 0; j < matrixSize; j++) {
            int sum = 0;
            for (int k = 0; k < matrixSize; k++) {
                sum += rowA[k] * matrixBT[j][k];
            }
            matrixC[i][j] = sum;
        }
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
