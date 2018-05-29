package b.concurrent;

/**
 * LockSupport�Ĳ��ԣ���ʹ��LockSupport��park��unparkʵ�֣�
 * ����Ҳ��֤��LockSupprot��˫��setBlocker()
 * �������̴߳ӵ�����LockSupport.unpark��MyThread��Main�߳��ٴν���cpu���ȣ�
 * ����Ϊ���ÿ�blocker,MyThread�߳�˯��500
 * 
 * ���Խ��Ϊ��
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
		LockSupport.park("ParkAndUnparkDemo");//����
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
				//��ȡblocker
				System.out.println("blocker info "+LockSupport.getBlocker((Thread)obejct));
				//�ͷ����
				LockSupport.unpark((Thread)obejct);
				//����500ms����֤��ִ��park��setBlocker(t,null)
				Thread.sleep(500);
				//�ٴλ�ȡblocker
				System.out.println("blocker info "+LockSupport.getBlocker((Thread)obejct));
				System.out.println("after unpark");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
