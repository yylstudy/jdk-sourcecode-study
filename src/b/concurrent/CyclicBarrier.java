
package b.concurrent;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier {
	//内部类Generation，broken表示当前屏障是否损坏
    private static class Generation {
        boolean broken = false;
    }

    /** The lock for guarding barrier entry */
    //一个锁
    private final ReentrantLock lock = new ReentrantLock();
    //一个条件队列
    /** Condition to wait on until tripped */
    private final Condition trip = lock.newCondition();
    /** The number of parties */
    //参与的线程数量
    private final int parties;
    /* The command to run when tripped */
    //最后一个进入CyclicBarrier的线程执行的操作，这个Runnable会最先执行，并且是同步的
    private final Runnable barrierCommand;
    /** The current generation */
    //
    private Generation generation = new Generation();
    //正在等待进入屏障的线程数量
    private int count;

    private void nextGeneration() {
        // signal completion of last generation
        trip.signalAll();//唤醒所有被阻塞的线程
        // set up next generation
        //重置正在等待进入屏障的数量
        count = parties;
        //新生一代
        generation = new Generation();
    }

    private void breakBarrier() {
        generation.broken = true;
        count = parties;
        trip.signalAll();
    }

    /**
     * Main barrier code, covering the various policies.
     */
    private int dowait(boolean timed, long nanos)
        throws InterruptedException, BrokenBarrierException,
               TimeoutException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final Generation g = generation;

            if (g.broken)//屏障被破坏，抛出异常
                throw new BrokenBarrierException();

            if (Thread.interrupted()) {//线程被中断
                breakBarrier();//损坏当前屏障，并且唤醒所有的线程，只有拥有锁的时候才会调用
                throw new InterruptedException();
            }
            
            int index = --count;
            if (index == 0) {  // 正在等待进入屏障的线程数为0，那么说明所有线程都已经进入
                boolean ranAction = false;
                try {
                    final Runnable command = barrierCommand;
                    if (command != null)
                    	//注意这里是run不是start，说明是同步运行，所以说，若是barrierCommand
                    	//不为空的，那么barrierCommand的线程会是第一个运行的线程
                        command.run();
                    ranAction = true;
                  //唤醒所有阻塞的线程，并且重置count的数量，可以重新使用，
                  //也就是进入下一代的意思，（下一个屏障的意思？）
                    nextGeneration();
                    return 0;
                } finally {
                    if (!ranAction)
                        breakBarrier();
                }
            }

            // loop until tripped, broken, interrupted, or timed out
            for (;;) {
                try {
                    if (!timed)
                        trip.await();
                    else if (nanos > 0L)
                        nanos = trip.awaitNanos(nanos);
                } catch (InterruptedException ie) {
                    if (g == generation && ! g.broken) {
                        breakBarrier();
                        throw ie;
                    } else {
                        // We're about to finish waiting even if we had not
                        // been interrupted, so this interrupt is deemed to
                        // "belong" to subsequent execution.
                        Thread.currentThread().interrupt();
                    }
                }

                if (g.broken)
                    throw new BrokenBarrierException();

                if (g != generation)
                    return index;

                if (timed && nanos <= 0L) {
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public CyclicBarrier(int parties, Runnable barrierAction) {
        if (parties <= 0) throw new IllegalArgumentException();
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierAction;
    }

    public CyclicBarrier(int parties) {
        this(parties, null);
    }

    public int getParties() {
        return parties;
    }

    public int await() throws InterruptedException, BrokenBarrierException {
        try {
            return dowait(false, 0L);
        } catch (TimeoutException toe) {
            throw new Error(toe); // cannot happen
        }
    }

    public int await(long timeout, TimeUnit unit)
        throws InterruptedException,
               BrokenBarrierException,
               TimeoutException {
        return dowait(true, unit.toNanos(timeout));
    }

    public boolean isBroken() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return generation.broken;
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            breakBarrier();   // break the current generation
            nextGeneration(); // start a new generation
        } finally {
            lock.unlock();
        }
    }

    public int getNumberWaiting() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return parties - count;
        } finally {
            lock.unlock();
        }
    }
}
