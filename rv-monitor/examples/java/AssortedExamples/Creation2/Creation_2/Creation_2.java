package Creation_2;

public class Creation_2 {
	public int temp = 0;
	public void fun1(){
		temp = 1;
	}
	public void fun2(){
		temp = 2;
	}

	public static void main(String[] args){
		Creation_2 o = new Creation_2();

		System.out.println("fun2");
		o.fun2();
		rvm.CreationRuntimeMonitor.fun2Event(o);
		System.out.println("fun2");
		o.fun2();
		rvm.CreationRuntimeMonitor.fun2Event(o);
		System.out.println("fun2");
		o.fun2();
		rvm.CreationRuntimeMonitor.fun2Event(o);

		System.out.println("main end");
		rvm.CreationRuntimeMonitor.mainendEvent();
	}
}



