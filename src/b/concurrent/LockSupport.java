package b.concurrent;

@SuppressWarnings("restriction")
public class LockSupport {
    private LockSupport() {} // Cannot be instantiated.

    private static void setBlocker(Thread t, Object arg) {
        UNSAFE.putObject(t, parkBlockerOffset, arg);
    }
  //unpark函数：激活调用park后阻塞的线程
    public static void unpark(Thread thread) {
        if (thread != null)
            UNSAFE.unpark(thread);
    }
    /**
     * 这里为什么要调用两次serBlocker呢？当前线程设置好了parkBlocker，然后再调用UNSAFE的park方法
     * 此后当前线程被阻塞住了，等待该线程的unpark方法被调用以唤醒该线程，所以setBlocker(t, null)还无法运行，
     * 当前线程调用unpark时，该线程获取许可，就可以继续运行了，也就运行了第二个setBlocker，把该线程的parkBlocker设置为null
     * 这样就完成了整个park函数的逻辑。如果没有第二个setBlocker，那么之后没有调用park(Object blocker)，而直接调用
     * getBlocker函数，得到的还是前一个park(Object blocker)设置的blocker，显然是不符合逻辑的，总之必须保证
     * 在park（Object blocker）整个函数执行完毕后，该线程的parkBlocker字段又恢复null。所以
     * 整个函数的setBlocker函数必须被调用两次
     * @param blocker
     */
  //park函数：阻塞线程
    public static void park(Object blocker) {
        Thread t = Thread.currentThread();//获取当前线程
        setBlocker(t, blocker);//设置blocker  这个方法主要是设置线程 阻塞的原因，可以方便调试
        UNSAFE.park(false, 0L);//获取许可
        setBlocker(t, null);//重新可运行后在设置blocker
    }

    //表示在许可可用前禁用当前线程，并最多等待指定的具体时间
    public static void parkNanos(Object blocker, long nanos) {
        if (nanos > 0) {
            Thread t = Thread.currentThread();
            setBlocker(t, blocker);
            UNSAFE.park(false, nanos);
            setBlocker(t, null);
        }
    }
    /**
     * 
     * @param blocker
     * @param deadline
     */
    public static void parkUntil(Object blocker, long deadline) {
        Thread t = Thread.currentThread();
        setBlocker(t, blocker);
        UNSAFE.park(true, deadline);
        setBlocker(t, null);
    }

    public static Object getBlocker(Thread t) {
        if (t == null)
            throw new NullPointerException();
        return UNSAFE.getObjectVolatile(t, parkBlockerOffset);
    }

    public static void park() {
        UNSAFE.park(false, 0L);
    }

    public static void parkNanos(long nanos) {
        if (nanos > 0)
            UNSAFE.park(false, nanos);
    }

    public static void parkUntil(long deadline) {
        UNSAFE.park(true, deadline);
    }

	static final int nextSecondarySeed() {
        int r;
        Thread t = Thread.currentThread();
        if ((r = UNSAFE.getInt(t, SECONDARY)) != 0) {
            r ^= r << 13;   // xorshift
            r ^= r >>> 17;
            r ^= r << 5;
        }
        else if ((r = java.util.concurrent.ThreadLocalRandom.current().nextInt()) == 0)
            r = 1; // avoid zero
        UNSAFE.putInt(t, SECONDARY, r);
        return r;
    }

    // Hotspot implementation via intrinsics API
    private static final sun.misc.Unsafe UNSAFE;
    //表示内存偏移地址
    private static final long parkBlockerOffset;
    //表示内存偏移地址
    private static final long SEED;
    //表示内存偏移地址
    private static final long PROBE;
    //表示内存偏移地址
    private static final long SECONDARY;
    static {
        try {
        	// 获取Unsafe实例
            UNSAFE = sun.misc.Unsafe.getUnsafe();
         // 线程类类型
            Class<?> tk = Thread.class;
         // 获取Thread的parkBlocker字段的内存偏移地址
            parkBlockerOffset = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("parkBlocker"));
         // 获取Thread的threadLocalRandomSeed字段的内存偏移地址
            SEED = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSeed"));
         // 获取Thread的threadLocalRandomProbe字段的内存偏移地址
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
         // 获取Thread的threadLocalRandomSecondarySeed字段的内存偏移地址
            SECONDARY = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSecondarySeed"));
        } catch (Exception ex) { throw new Error(ex); }
    }

}
