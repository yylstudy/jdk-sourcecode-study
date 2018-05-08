package a.collectionSourceCode;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
/**
 * ����ҪֻҪ�������ʱ������������ͬʱ������ʹ��IdentityHashMap
 * @author yyl-pc
 *
 */
public class MyTest2 {
	// IdentityHashMapѧϰ
	@Test
	public void test1() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(new String("aa"), "aa");// HashMap�Ƚϵ���== ����equals������ֻ��һ��Ԫ��
		map.put(new String("aa"), "bb");
		Map<String, String> map2 = new IdentityHashMap<String, String>();
		map2.put(new String("aa"), "aa");// IdentityHashMap û�бȽ�equals,�������ε�new String("aa")����һ������
		map2.put(new String("aa"), "bb");// ������������ֵ�ԣ�˵��IdentityHashMapֻ����key��ȫ���ʱ ==,�Ż��滻
		System.out.println(map);
		System.out.println(map2);
	}

	// goto ��ѧϰ
	//break�жϲ�������ǩ��ָ��ѭ����continue��ת����ǩָ����ѭ������������ִ�иñ�ǩ��ָ����ѭ����
	@Test
	public void test2() {
		outer: for (int i = 0; i < 10; i++) {
			System.out.println("\nouter_loop:" + i);
			inner: for (int k = 0; i < 10; k++) {
				System.out.println(k + " ");
				int x = new Random().nextInt(10);
				if (x > 7) {
					System.out.println(" >>x == " + x + "������innerѭ������������ִ��outerѭ���ˣ�");
					continue outer;
				}
				if (x == 1) {
					System.out.println(" >>x == 1����������������outer��innerѭ����");
					break outer;
				}
			}
		}
		System.out.println("------>>>����ѭ��ִ����ϣ�");
	}

}
