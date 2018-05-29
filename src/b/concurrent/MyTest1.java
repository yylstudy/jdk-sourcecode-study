package b.concurrent;

/**
 * LockSupport�Ĳ���(ʹ��Object��wait��notifyʵ��)
 * ��ӡ˳��Ӧ����
 * before wait
 * before notify
 * after notify
 * after wait
 * @author yyl-pc
 *
 */
public class MyTest1 {
	public static void main(String[] args) throws InterruptedException {
		MyThread myThread = new MyThread();
		synchronized(myThread) {
			myThread.start();
			myThread.sleep(3000);
			System.out.println("before wait");
			myThread.wait();
			System.out.println("after wait");
		}
	}
	static class MyThread extends Thread{
		@Override
		public void run() {
			synchronized (this) {
				System.out.println("before notify");
				notify();
				System.out.println("after notify");
			}
		}
	}
	
}
