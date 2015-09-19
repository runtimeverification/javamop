class SimpleRunnable1 implements Runnable{
    public void run(){
        System.out.println("Thread " + Thread.currentThread().getName());
    }
}

class SimpleRunnable2 implements Runnable{
    public void run(){
        System.out.println("Thread " + Thread.currentThread().getName());
    }
}

public class Main{
    public static void main(String args[]) throws Exception{
        Thread t1 = new Thread (new SimpleRunnable1());
        Thread t2 = new Thread (new SimpleRunnable2());
        t1.setName("T1");
        t2.setName("T2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
