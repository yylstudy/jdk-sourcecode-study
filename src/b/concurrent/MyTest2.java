package b.concurrent;

/**
 * LockSupport的测试，（使用LockSupport的park和unpark实现）
 * 这里也验证了LockSupprot的双重setBlocker()
 * 这里主线程从调用了LockSupport.unpark后，MyThread和Main线程再次交由cpu调度，
 * 所有为了置空blocker,MyThread线程睡眠500
 * 
 * 测试结果为：
 * before park
 * before unpark
 * blocker info ParkAndUnparkDemo
 * after park
 * blocker info null
 * after unpark
 * 
 * @author yyl-pc
 *
 */
import java.util.concurrent.locks.LockSupport;
public class MyTest2 {
	public static void main(String[] args) {
		MyThread myThread = new MyThread(Thread.currentThread());
		myThread.start();
		System.out.println("before park");
		LockSupport.park("ParkAndUnparkDemo");//阻塞
		System.out.println("after park");
	}
	static class MyThread extends Thread{
		private Object obejct;
		public MyThread(Object obejct) {
			this.obejct = obejct;
		}
		@Override
		public void run() {
			try {
				System.out.println("before unpark");
				Thread.sleep(1000);
				//获取blocker
				System.out.println("blocker info "+LockSupport.getBlocker((Thread)obejct));
				//释放许可
				LockSupport.unpark((Thread)obejct);
				//休眠500ms，保证先执行park的setBlocker(t,null)
				Thread.sleep(500);
				//再次获取blocker
				System.out.println("blocker info "+LockSupport.getBlocker((Thread)obejct));
				System.out.println("after unpark");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
