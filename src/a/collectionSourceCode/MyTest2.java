package a.collectionSourceCode;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
/**
 * 当需要只要对象相等时而不是内容相同时，考虑使用IdentityHashMap
 * @author yyl-pc
 *
 */
public class MyTest2 {
	// IdentityHashMap学习
	@Test
	public void test1() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(new String("aa"), "aa");// HashMap比较的是== 或者equals，所以只有一个元素
		map.put(new String("aa"), "bb");
		Map<String, String> map2 = new IdentityHashMap<String, String>();
		map2.put(new String("aa"), "aa");// IdentityHashMap 没有比较equals,所以两次的new String("aa")不是一个对象
		map2.put(new String("aa"), "bb");// 所以有两个键值对，说明IdentityHashMap只有在key完全相等时 ==,才会替换
		System.out.println(map);
		System.out.println(map2);
	}

	// goto 的学习
	//break中断并跳出标签所指定循环，continue跳转到标签指定的循环处，并继续执行该标签所指定的循环。
	@Test
	public void test2() {
		outer: for (int i = 0; i < 10; i++) {
			System.out.println("\nouter_loop:" + i);
			inner: for (int k = 0; i < 10; k++) {
				System.out.println(k + " ");
				int x = new Random().nextInt(10);
				if (x > 7) {
					System.out.println(" >>x == " + x + "，结束inner循环，继续迭代执行outer循环了！");
					continue outer;
				}
				if (x == 1) {
					System.out.println(" >>x == 1，跳出并结束整个outer和inner循环！");
					break outer;
				}
			}
		}
		System.out.println("------>>>所有循环执行完毕！");
	}

}
