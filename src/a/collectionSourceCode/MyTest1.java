package a.collectionSourceCode;

import org.junit.Test;

import a.collectionSourceCode.LinkedHashMap.Entry;

/**
 * 当需要元素有序的Map时，考虑使用LinkedHashMap
 * @author yyl-pc
 *
 */
public class MyTest1 {
	//所谓的accessOrder 为true ，安装元素的访问顺序，
	//即使出现相同的元素，也会在最后节点添加
	//所以  aa3是头结点，aa是尾节点
	//造成此影响是 LinkedHashMap 中的afterNodeAccess方法
	@Test
	public void test1() {
		LinkedHashMap<String,String> map = new LinkedHashMap<String,String>(16,0.75f,true);
		map.put("aa", "aa");
		map.put("aa3", "aa3");
		map.put("aa2", "aa2");
		map.put("aa", "aa1");
		Entry<String,String> entry = map.head;
		Entry<String,String> next = null;
		System.out.println(entry);
		while((next = entry.after)!=null) {
			System.out.println(next.key+":"+next.value);
			entry = next;
		}
	}
	//默认 accessOrder 为fasle，按照元素的插入顺序，所以，aa还是头节点
	@Test
	public void test2() {
		LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();
		map.put("aa", "aa");
		map.put("aa3", "aa3");
		map.put("aa2", "aa2");
		map.put("aa", "aa1");
		Entry<String,String> entry = map.head;
		Entry<String,String> next = null;
		System.out.println(entry);
		while((next = entry.after)!=null) {
			System.out.println(next.key+":"+next.value);
			entry = next;
		}
	}
}
