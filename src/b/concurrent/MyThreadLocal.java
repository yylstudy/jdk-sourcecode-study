
package b.concurrent;
import java.lang.ref.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class MyThreadLocal<T> {
    private final int threadLocalHashCode = nextHashCode();

    private static AtomicInteger nextHashCode =
        new AtomicInteger();

    private static final int HASH_INCREMENT = 0x61c88647;

    /**
     * Returns the next hash code.
     */
    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    protected T initialValue() {//设置初始值
        return null;
    }
    public static <S> MyThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }
    public MyThreadLocal() {
    }

    public T get() {
        Thread t = Thread.currentThread();//获取当前线程
      //获取当前线程的ThreadLocalMap，Thread类中有ThreadLocalMap的属性
        ThreadLocalMap map = getMap(t);
        //由于获取当前线程，不存在多线程的问题，不用加锁
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();//初始化值和ThreadLocalMap
    }
    private T setInitialValue() {
        T value = initialValue();//初始值
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);//初始化ThreadLocalMap
        return value;
    }
    public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
     public void remove() {
         ThreadLocalMap m = getMap(Thread.currentThread());
         if (m != null)
             m.remove(this);
     }

    ThreadLocalMap getMap(Thread t) {
//        return t.threadLocals;//源码，由于无法访问t.threadLocals，将其注释，否则编译报错
    	return null;
    }
    void createMap(Thread t, T firstValue) {
    	//源码，由于无法访问t.threadLocals，将其注释，否则编译报错
    	//这段代码是创建一个ThreadLocalMap对象
//        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }

    static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) {
        return new ThreadLocalMap(parentMap);
    }

    T childValue(T parentValue) {
        throw new UnsupportedOperationException();
    }

    static final class SuppliedThreadLocal<T> extends MyThreadLocal<T> {
        private final Supplier<? extends T> supplier;
        SuppliedThreadLocal(Supplier<? extends T> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
        }
        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }
    //线程内存放的一个Map，是Thread中的一个属性，已当前的ThreadLocal对象为key，以存储的值为value
    //为什么要设置为Map呢？ThreadLocal不是只有一个值吗，因为一个线程中可以使用多个ThreadLocal,
    //有多少个ThreadLocal，这个Map就有多少个Entry，这里使用了 WeakReference java的弱引用
    //Entry.get() 返回的就是ThreadLocal对象
    static class ThreadLocalMap {
    	//这里的Entry对象必须是WeakReference，为什么呢，因为如果ThreadLocal被销毁，但是ThreadLocal中
        static class Entry extends WeakReference<MyThreadLocal<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;

            Entry(MyThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }

        /**
         * The initial capacity -- MUST be a power of two.
         */
        private static final int INITIAL_CAPACITY = 16;

        /**
         * The table, resized as necessary.
         * table.length MUST always be a power of two.
         */
        private Entry[] table;

        /**
         * The number of entries in the table.
         */
        private int size = 0;

        /**
         * The next size value at which to resize.
         */
        private int threshold; // Default to 0

        /**
         * Set the resize threshold to maintain at worst a 2/3 load factor.
         */
        private void setThreshold(int len) {
            threshold = len * 2 / 3;
        }

        /**
         * Increment i modulo len.
         */
        private static int nextIndex(int i, int len) {
            return ((i + 1 < len) ? i + 1 : 0);
        }

        /**
         * Decrement i modulo len.
         */
        private static int prevIndex(int i, int len) {
            return ((i - 1 >= 0) ? i - 1 : len - 1);
        }
        //初始化ThreadLocalMap
        ThreadLocalMap(MyThreadLocal<?> firstKey, Object firstValue) {
            table = new Entry[INITIAL_CAPACITY]; //初始化table
          //计算第一个传入的ThreadLocal的hash，定位存储在table的下标
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
          //设置阀值，这个ThreadLocalMap存储的ThreadLocal的个数大于这个阈值时，就要扩容
            setThreshold(INITIAL_CAPACITY);
        }

        private ThreadLocalMap(ThreadLocalMap parentMap) {
            Entry[] parentTable = parentMap.table;
            int len = parentTable.length;
            setThreshold(len);
            table = new Entry[len];

            for (int j = 0; j < len; j++) {
                Entry e = parentTable[j];
                if (e != null) {
                    @SuppressWarnings("unchecked")
                    MyThreadLocal<Object> key = (MyThreadLocal<Object>) e.get();
                    if (key != null) {
                        Object value = key.childValue(e.value);
                        Entry c = new Entry(key, value);
                        int h = key.threadLocalHashCode & (len - 1);
                        while (table[h] != null)
                            h = nextIndex(h, len);
                        table[h] = c;
                        size++;
                    }
                }
            }
        }
        //根据Entry的key也就是ThreadLocal，在Entry[]数组中获取Entry
        private Entry getEntry(MyThreadLocal<?> key) {
        	//根据key的hash定位entry[]数组的下标
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            if (e != null && e.get() == key)//如果定位的Entry的key与传入的ThreadLocal相等，那么返回这个Entry
                return e;
            else
            	//因为使用的是线性探测，所以往后找还是有可能找到目标的
            	//这里的往后找其实是个环，可以看nextIndex的实现，其实是个环状的
                return getEntryAfterMiss(key, i, e);
        }
        //基于线性探测法，不断向后探测直到遇到空的entry
        private Entry getEntryAfterMiss(MyThreadLocal<?> key, int i, Entry e) {
            Entry[] tab = table;
            int len = tab.length;

            while (e != null) {
                MyThreadLocal<?> k = e.get();
                if (k == key)//找到目标
                    return e;
                if (k == null)
                	//该Entry对应的ThreadLocal被回收，那就要清理entry对象，那么为什么ThreadLocal被回收
                    expungeStaleEntry(i);
                else
                    i = nextIndex(i, len);
                e = tab[i];
            }
            return null;
        }
        //赋值将键值对插入进 ThreadLocalMap
        private void set(MyThreadLocal<?> key, Object value) {
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);
            //线性探测
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                MyThreadLocal<?> k = e.get();
                if (k == key) {
                    e.value = value;
                    return;
                }
                //替换失效的key，就是把当前的键值对插入失效的key中
                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }

            tab[i] = new Entry(key, value);
            int sz = ++size;
            if (!cleanSomeSlots(i, sz) && sz >= threshold)
                rehash();
        }

        /**
         * Remove the entry for key.
         */
        private void remove(MyThreadLocal<?> key) {
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                if (e.get() == key) {
                    e.clear();
                    expungeStaleEntry(i);
                    return;
                }
            }
        }
        //staleSlot key也就是ThreadLocal失效的table的下标
        private void replaceStaleEntry(MyThreadLocal<?> key, Object value,
                                       int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;
            Entry e;
            int slotToExpunge = staleSlot;
            //向前扫描，查找最前一个无效的slot
            for (int i = prevIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = prevIndex(i, len))
                if (e.get() == null)
                    slotToExpunge = i;

            //向后遍历table
            for (int i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                MyThreadLocal<?> k = e.get();
                if (k == key) {
                	//i表示成功匹配的下标
                	//更新对应slot的值
                    e.value = value;

                    tab[i] = tab[staleSlot];
                    tab[staleSlot] = e;

                    //整个扫描过程中（包括函数一开始向前扫描与i的向后扫描）
                    //找到之前无效的slot，以这个做起点作为清理的位置
                    //否则以当前slotToExpunge作为起点清理
                    if (slotToExpunge == staleSlot)
                        slotToExpunge = i;
                    //从slotToExpunge开始，做一次连续段的清理，再做一次启发式的清理
                    cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
                    return;
                }

                // If we didn't find stale entry on backward scan, the
                // first stale entry seen while scanning for key is the
                // first still present in the run.
                if (k == null && slotToExpunge == staleSlot)
                    slotToExpunge = i;
            }

            // If key not found, put new entry in stale slot
            tab[staleSlot].value = null;
            tab[staleSlot] = new Entry(key, value);

            // If there are any other stale entries in run, expunge them
            if (slotToExpunge != staleSlot)
                cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
        }
        //清理staleSlot对应的entry对象
        //这个函数是ThreadLocal的核心清理函数，就是从staleSlot开始遍历，将无效的弱引用指向对象清理
        //即对应的entry的value置为空，将这个entry的table[i]置为空，直到空的entry
        //另外在这个过程中还会对非空的entry进行rehash
        
        private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // expunge entry at staleSlot
            tab[staleSlot].value = null;//Entry的value引用置为空，help GC
            tab[staleSlot] = null;//help GC
            size--;

            // Rehash until we encounter null
            Entry e;
            int i;
            for (i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                MyThreadLocal<?> k = e.get();
                if (k == null) {
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    int h = k.threadLocalHashCode & (len - 1);
                    if (h != i) {
                        tab[i] = null;

                        // Unlike Knuth 6.4 Algorithm R, we must scan until
                        // null because multiple entries could have been stale.
                        while (tab[h] != null)
                            h = nextIndex(h, len);
                        tab[h] = e;
                    }
                }
            }
            return i;
        }
        /**
         * 启发式清理slot
         * 
         * @param i
         * @param n
         * @return
         */
        private boolean cleanSomeSlots(int i, int n) {
            boolean removed = false;
            Entry[] tab = table;
            int len = tab.length;
            do {
            	//i在任何情况下，自己都不会是一个无效的slot，所以从下一个开始判断
                i = nextIndex(i, len);
                Entry e = tab[i];
                if (e != null && e.get() == null) {
                	//扩大扫描控制因子
                    n = len;
                    removed = true;
                    //清理一个连续段
                    i = expungeStaleEntry(i);
                }
            } while ( (n >>>= 1) != 0);
            return removed;
        }

        /**
         * Re-pack and/or re-size the table. First scan the entire
         * table removing stale entries. If this doesn't sufficiently
         * shrink the size of the table, double the table size.
         */
        private void rehash() {
            expungeStaleEntries();

            // Use lower threshold for doubling to avoid hysteresis
            if (size >= threshold - threshold / 4)
                resize();
        }

        /**
         * Double the capacity of the table.
         */
        private void resize() {
            Entry[] oldTab = table;
            int oldLen = oldTab.length;
            int newLen = oldLen * 2;
            Entry[] newTab = new Entry[newLen];
            int count = 0;

            for (int j = 0; j < oldLen; ++j) {
                Entry e = oldTab[j];
                if (e != null) {
                    MyThreadLocal<?> k = e.get();
                    if (k == null) {
                        e.value = null; // Help the GC
                    } else {
                        int h = k.threadLocalHashCode & (newLen - 1);
                        while (newTab[h] != null)
                            h = nextIndex(h, newLen);
                        newTab[h] = e;
                        count++;
                    }
                }
            }

            setThreshold(newLen);
            size = count;
            table = newTab;
        }

        //做一次全量的清理，清理ThreadLocal为空的Entry
        private void expungeStaleEntries() {
            Entry[] tab = table;
            int len = tab.length;
            for (int j = 0; j < len; j++) {
                Entry e = tab[j];
                if (e != null && e.get() == null)
                    expungeStaleEntry(j);
            }
        }
    }
}
