package a.collectionSourceCode;

import java.util.Map;

import org.junit.Test;

/**
 * 当我们需要把插入的元素进行排序的时候，就是时候考虑TreeMap了
 * 红黑树不懂，跳过了。。。
 * @author yyl-pc
 *
 */
public class MyTest3 {
	@Test
	/**
	 * 发现TreeMap会自动对key进行排序
	 */
	public void test1() {
		Map<String,String> map = new TreeMap<String,String>();
		map.put("aa", "aa");
		map.put("cc", "cc");
		map.put("bb", "bb");
		System.out.println(map);
	}
}
