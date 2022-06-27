            try {
                        PrintWriter w =  new PrintWriter(new File("/tmp/internal.txt"));
                        com.runtimeverification.rvmonitor.java.rt.observable.InternalBehaviorDumper dumper = new com.runtimeverification.rvmonitor.java.rt.observable.InternalBehaviorDumper(w);
                        subscribe(dumper);
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        List<com.runtimeverification.rvmonitor.java.rt.observable.IInternalBehaviorObserver> obs = observer.getObservers();
                        for (com.runtimeverification.rvmonitor.java.rt.observable.IInternalBehaviorObserver o : obs)
                            o.onCompleted();
                    }
                });
