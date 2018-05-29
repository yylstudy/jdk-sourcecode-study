package b.concurrent;

public class MyTest3 {
	private static ThreadLocal<Integer> local = new ThreadLocal<Integer>();
//	private static ThreadLocal<Integer> local2 = new ThreadLocal<Integer>();
	public static void main(String[] args) {
		for(int i=0;i<10;i++) {
			local.set(i);
//			local2.set(i);
		}
		System.out.println(local.get());
//		System.out.println(local2.get());
	}
}
