package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    private static Random rn = new Random();

    private MatrixUtil() {
    }

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[][] matrixBT = getTMatrix(matrixB, matrixSize);

        List<Callable<Boolean>> result = new ArrayList<>(matrixSize);

        for (int i = 0; i < matrixSize; i++) {
            result.add(new GroupResult(i, matrixSize, matrixA, matrixBT, matrixC));
        }

        executor.invokeAll(result);

        return matrixC;
    }

    public static class GroupResult implements Callable<Boolean> {
        int i;
        final int matrixSize;
        int[][] matrixA;
        int[][] matrixBT;
        final int[][] matrixC;

        public GroupResult(int i, int matrixSize, int[][] matrixA, int[][] matrixBT, int[][] matrixC) {
            this.i = i;
            this.matrixSize = matrixSize;
            this.matrixA = matrixA;
            this.matrixBT = matrixBT;
            this.matrixC = matrixC;
        }

        @Override
        public Boolean call() {
            mainLoop(i, matrixA[i], matrixBT, matrixC, matrixSize);
            return true;
        }
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
