package org.example.abd;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RegisterTest {

    @Test
    public void sequential() {
        Manager manager = new Manager();
        Register<Integer> copy1 = manager.newRegister(true);
        Register<Integer> copy2 = manager.newRegister();
        Register<Integer> copy3 = manager.newRegister();

        assert copy2.read() == null;
        copy1.write(42);
        assert copy3.read() == 42;

    }

    @Test
    public void concurrent() throws ExecutionException, InterruptedException {
        int NCALLS = 1000;

        Manager manager = new Manager();
        Register<Integer> copy1 = manager.newRegister(true);
        Register<Integer> copy2 = manager.newRegister();
        Register<Integer> copy3 = manager.newRegister();

        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Void>> futures = new ArrayList<>();
        futures.add(executorService.submit(new Writer("w", copy1, NCALLS)));
        futures.add(executorService.submit(new Reader("r0", copy2, NCALLS)));
        futures.add(executorService.submit(new Reader("r1", copy3, NCALLS)));

        for (Future<Void> future : futures){
            future.get();
        }
    }

    //

    private abstract class Worker implements Callable<Void>{

        String name;
        Register<Integer> register;
        int ncalls;

        Worker(String name, Register<Integer> register, int ncalls) {
            this.name = name;
            this.register = register;
            this.ncalls = ncalls;
        }

        protected abstract void doCall();

        protected void log(boolean isInv, String msg){
            System.out.println(name + " - " + (isInv ? "INV" : "RES") + " - " + msg);
        }

        @Override
        public Void call() throws Exception {
            for(int i=0; i<ncalls; i++){
                doCall();
            }
            return null;
        }

    }


    private class Writer extends Worker{

        Random random;

        Writer(String name, Register<Integer> register, int ncalls) {
            super(name, register, ncalls);
            this.random = new Random(System.currentTimeMillis());
        }

        @Override
        protected void doCall() {
            int value = random.nextInt();
            log(true,"W("+value+")");
            register.write(value);
            log(false,"W("+value+")");
        }
    }

    private class Reader extends Worker{

        Reader(String name, Register<Integer> register, int ncalls) {
            super(name, register, ncalls);
        }

        @Override
        public void doCall(){
            log(true,"R()");
            Integer ret = register.read();
            log(false,"R()"+ret);
        }
    }

}
