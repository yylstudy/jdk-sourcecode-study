
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

    protected T initialValue() {//���ó�ʼֵ
        return null;
    }
    public static <S> MyThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        return new SuppliedThreadLocal<>(supplier);
    }
    public MyThreadLocal() {
    }

    public T get() {
        Thread t = Thread.currentThread();//��ȡ��ǰ�߳�
      //��ȡ��ǰ�̵߳�ThreadLocalMap��Thread������ThreadLocalMap������
        ThreadLocalMap map = getMap(t);
        //���ڻ�ȡ��ǰ�̣߳������ڶ��̵߳����⣬���ü���
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();//��ʼ��ֵ��ThreadLocalMap
    }
    private T setInitialValue() {
        T value = initialValue();//��ʼֵ
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);//��ʼ��ThreadLocalMap
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
//        return t.threadLocals;//Դ�룬�����޷�����t.threadLocals������ע�ͣ�������뱨��
    	return null;
    }
    void createMap(Thread t, T firstValue) {
    	//Դ�룬�����޷�����t.threadLocals������ע�ͣ�������뱨��
    	//��δ����Ǵ���һ��ThreadLocalMap����
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
    //�߳��ڴ�ŵ�һ��Map����Thread�е�һ�����ԣ��ѵ�ǰ��ThreadLocal����Ϊkey���Դ洢��ֵΪvalue
    //ΪʲôҪ����ΪMap�أ�ThreadLocal����ֻ��һ��ֵ����Ϊһ���߳��п���ʹ�ö��ThreadLocal,
    //�ж��ٸ�ThreadLocal�����Map���ж��ٸ�Entry������ʹ���� WeakReference java��������
    //Entry.get() ���صľ���ThreadLocal����
    static class ThreadLocalMap {
    	//�����Entry���������WeakReference��Ϊʲô�أ���Ϊ���ThreadLocal�����٣�����ThreadLocal��
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
        //��ʼ��ThreadLocalMap
        ThreadLocalMap(MyThreadLocal<?> firstKey, Object firstValue) {
            table = new Entry[INITIAL_CAPACITY]; //��ʼ��table
          //�����һ�������ThreadLocal��hash����λ�洢��table���±�
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
          //���÷�ֵ�����ThreadLocalMap�洢��ThreadLocal�ĸ������������ֵʱ����Ҫ����
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
        //����Entry��keyҲ����ThreadLocal����Entry[]�����л�ȡEntry
        private Entry getEntry(MyThreadLocal<?> key) {
        	//����key��hash��λentry[]������±�
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            if (e != null && e.get() == key)//�����λ��Entry��key�봫���ThreadLocal��ȣ���ô�������Entry
                return e;
            else
            	//��Ϊʹ�õ�������̽�⣬���������һ����п����ҵ�Ŀ���
            	//�������������ʵ�Ǹ��������Կ�nextIndex��ʵ�֣���ʵ�Ǹ���״��
                return getEntryAfterMiss(key, i, e);
        }
        //��������̽�ⷨ���������̽��ֱ�������յ�entry
        private Entry getEntryAfterMiss(MyThreadLocal<?> key, int i, Entry e) {
            Entry[] tab = table;
            int len = tab.length;

            while (e != null) {
                MyThreadLocal<?> k = e.get();
                if (k == key)//�ҵ�Ŀ��
                    return e;
                if (k == null)
                	//��Entry��Ӧ��ThreadLocal�����գ��Ǿ�Ҫ����entry������ôΪʲôThreadLocal������
                    expungeStaleEntry(i);
                else
                    i = nextIndex(i, len);
                e = tab[i];
            }
            return null;
        }
        //��ֵ����ֵ�Բ���� ThreadLocalMap
        private void set(MyThreadLocal<?> key, Object value) {
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);
            //����̽��
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                MyThreadLocal<?> k = e.get();
                if (k == key) {
                    e.value = value;
                    return;
                }
                //�滻ʧЧ��key�����ǰѵ�ǰ�ļ�ֵ�Բ���ʧЧ��key��
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
        //staleSlot keyҲ����ThreadLocalʧЧ��table���±�
        private void replaceStaleEntry(MyThreadLocal<?> key, Object value,
                                       int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;
            Entry e;
            int slotToExpunge = staleSlot;
            //��ǰɨ�裬������ǰһ����Ч��slot
            for (int i = prevIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = prevIndex(i, len))
                if (e.get() == null)
                    slotToExpunge = i;

            //������table
            for (int i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                MyThreadLocal<?> k = e.get();
                if (k == key) {
                	//i��ʾ�ɹ�ƥ����±�
                	//���¶�Ӧslot��ֵ
                    e.value = value;

                    tab[i] = tab[staleSlot];
                    tab[staleSlot] = e;

                    //����ɨ������У���������һ��ʼ��ǰɨ����i�����ɨ�裩
                    //�ҵ�֮ǰ��Ч��slot��������������Ϊ�����λ��
                    //�����Ե�ǰslotToExpunge��Ϊ�������
                    if (slotToExpunge == staleSlot)
                        slotToExpunge = i;
                    //��slotToExpunge��ʼ����һ�������ε���������һ������ʽ������
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
        //����staleSlot��Ӧ��entry����
        //���������ThreadLocal�ĺ��������������Ǵ�staleSlot��ʼ����������Ч��������ָ���������
        //����Ӧ��entry��value��Ϊ�գ������entry��table[i]��Ϊ�գ�ֱ���յ�entry
        //��������������л���Էǿյ�entry����rehash
        
        private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // expunge entry at staleSlot
            tab[staleSlot].value = null;//Entry��value������Ϊ�գ�help GC
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
         * ����ʽ����slot
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
            	//i���κ�����£��Լ���������һ����Ч��slot�����Դ���һ����ʼ�ж�
                i = nextIndex(i, len);
                Entry e = tab[i];
                if (e != null && e.get() == null) {
                	//����ɨ���������
                    n = len;
                    removed = true;
                    //����һ��������
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

        //��һ��ȫ������������ThreadLocalΪ�յ�Entry
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
