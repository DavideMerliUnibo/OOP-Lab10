package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThreads;
    
    public MultiThreadedSumMatrix(final int n) {
        this.nThreads = n;
    }
    
    @Override
    public double sum(final double[][] matrix) {
        /*
         * Copy matrix in a list for an easier use
         */
        final List<Double> matrixList = new ArrayList<>();
        for (final double[] arr : matrix) {
            for (final double elem : arr) {
                matrixList.add(elem);
            }
        }
        
        final int size = matrixList.size() % nThreads + matrixList.size() / nThreads;
        
        /*
         * Create worker list
         */
        final List<Worker> workers = new ArrayList<>(this.nThreads);
        for (int start = 0; start < matrixList.size(); start += size) {
            workers.add(new Worker(matrixList, start, size));
        }
        
        /*
         * Start worker threads
         */
        for (final Worker w : workers) {
            w.start();
        }
        
        /*
         * Wait for workers to finish their task
         */
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        
        /*
         * Return result
         */
        return sum;
    }
    
    private static class Worker extends Thread{
        
        private final List<Double> list;
        private final int startPos;
        private final int nElem;
        private double result;
        
        Worker(final List<Double> list, final int startPosition, final int nElements) {
            super();
            this.list = list;
            this.startPos = startPosition;
            this.nElem = nElements;
        }
        
        @Override
        public void run() {
            System.out.println("Working from position " + startPos + " to " + (nElem + startPos - 1));
            for (int i = startPos; i < list.size() && i < startPos + nElem; i++) {
                this.result += this.list.get(i);
            }
            
        }
        
        public double getResult() {
            return this.result;
        }
    }

}
