package b.concurrent;

@SuppressWarnings("restriction")
public class LockSupport {
    private LockSupport() {} // Cannot be instantiated.

    private static void setBlocker(Thread t, Object arg) {
        UNSAFE.putObject(t, parkBlockerOffset, arg);
    }
  //unpark�������������park���������߳�
    public static void unpark(Thread thread) {
        if (thread != null)
            UNSAFE.unpark(thread);
    }
    /**
     * ����ΪʲôҪ��������serBlocker�أ���ǰ�߳����ú���parkBlocker��Ȼ���ٵ���UNSAFE��park����
     * �˺�ǰ�̱߳�����ס�ˣ��ȴ����̵߳�unpark�����������Ի��Ѹ��̣߳�����setBlocker(t, null)���޷����У�
     * ��ǰ�̵߳���unparkʱ�����̻߳�ȡ��ɣ��Ϳ��Լ��������ˣ�Ҳ�������˵ڶ���setBlocker���Ѹ��̵߳�parkBlocker����Ϊnull
     * ���������������park�������߼������û�еڶ���setBlocker����ô֮��û�е���park(Object blocker)����ֱ�ӵ���
     * getBlocker�������õ��Ļ���ǰһ��park(Object blocker)���õ�blocker����Ȼ�ǲ������߼��ģ���֮���뱣֤
     * ��park��Object blocker����������ִ����Ϻ󣬸��̵߳�parkBlocker�ֶ��ָֻ�null������
     * ����������setBlocker�������뱻��������
     * @param blocker
     */
  //park�����������߳�
    public static void park(Object blocker) {
        Thread t = Thread.currentThread();//��ȡ��ǰ�߳�
        setBlocker(t, blocker);//����blocker  ���������Ҫ�������߳� ������ԭ�򣬿��Է������
        UNSAFE.park(false, 0L);//��ȡ���
        setBlocker(t, null);//���¿����к�������blocker
    }

    //��ʾ����ɿ���ǰ���õ�ǰ�̣߳������ȴ�ָ���ľ���ʱ��
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
    //��ʾ�ڴ�ƫ�Ƶ�ַ
    private static final long parkBlockerOffset;
    //��ʾ�ڴ�ƫ�Ƶ�ַ
    private static final long SEED;
    //��ʾ�ڴ�ƫ�Ƶ�ַ
    private static final long PROBE;
    //��ʾ�ڴ�ƫ�Ƶ�ַ
    private static final long SECONDARY;
    static {
        try {
        	// ��ȡUnsafeʵ��
            UNSAFE = sun.misc.Unsafe.getUnsafe();
         // �߳�������
            Class<?> tk = Thread.class;
         // ��ȡThread��parkBlocker�ֶε��ڴ�ƫ�Ƶ�ַ
            parkBlockerOffset = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("parkBlocker"));
         // ��ȡThread��threadLocalRandomSeed�ֶε��ڴ�ƫ�Ƶ�ַ
            SEED = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSeed"));
         // ��ȡThread��threadLocalRandomProbe�ֶε��ڴ�ƫ�Ƶ�ַ
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
         // ��ȡThread��threadLocalRandomSecondarySeed�ֶε��ڴ�ƫ�Ƶ�ַ
            SECONDARY = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSecondarySeed"));
        } catch (Exception ex) { throw new Error(ex); }
    }

}
