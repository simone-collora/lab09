package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;



public final class MultiThreadedSumMatrix implements SumMatrix{

    private final int nthread;

    public MultiThreadedSumMatrix(final int nthread){
        this.nthread=nthread;
    }

    private static class Worker extends Thread{
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private long res;

        Worker(final double[][]matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        public void run(){
            System.out.println("Working from position " + startpos + " to position" + (startpos + nelem -1));
            for(int i= startpos; i<matrix.length && i<startpos + nelem; i++){
                for(final double a: this.matrix[i]){
                    this.res+= a;
                }
            }
        }

        public long getResult() {
            return this.res;
        }
    }
    

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        final List<Worker>workers = new ArrayList<>(nthread);
        for(int i=0;i<matrix.length;i+=size){
            workers.add(new Worker(matrix, i, size));
        }
        for(final Thread w:workers){
            w.start();
        }
       
        double sum=0;
        for(final Worker w: workers) {
            try{
                w.join();
                sum += w.getResult();
            } catch(InterruptedException e){
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }


    
}