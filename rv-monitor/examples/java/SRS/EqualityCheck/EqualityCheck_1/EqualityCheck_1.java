package EqualityCheck_1;

import rvm.EqualityCheckRuntimeMonitor;

public class EqualityCheck_1 {
    public static void main(String[] args) {
        int times = 20;
        Thread t1 = new CThread("thread1", "A", times);
        t1.start();
        Thread t2 = new CThread("thread2", "B", times);
        t2.start();
        Thread t3 = new CThread("thread3", "C", times);
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rvm.EqualityCheckRuntimeMonitor.doneEvent();
    }
}

class CThread extends Thread {
    private int times;
    private String func;
    public CThread(String name, String fun, int t) {
        this.setName(name);
        this.func = fun;
        this.times = t;
    }
    public void run() {
        if(func.equals("A")) {
            for(int i = 0; i < times; i++) {
                try {
                    A();
                    rvm.EqualityCheckRuntimeMonitor.aEvent();
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if(func.equals("B")) {
            for(int i = 0; i < times - 1 ; i++) {
                try {
                    B();
                    rvm.EqualityCheckRuntimeMonitor.bEvent();
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if(func.equals("C")) {
            for(int i = 0; i < times; i++) {
                try {
                    C();
                    rvm.EqualityCheckRuntimeMonitor.cEvent();
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void A() {
        //System.out.println("A()");
    }
    public void B() {
        //System.out.println("B()");
    }
    public void C() {
        //System.out.println("C()");
    }
}
