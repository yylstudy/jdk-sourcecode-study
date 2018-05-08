package a.collectionSourceCode;

import java.util.Set;

/**
 * HashSet的使用：HashSet底层是使用HashMap或者LinkedHashMap实现的，HahsSet存放的是key值的集合，
 * 所有key值对应的value值都是Hash定义的常量PRESENT，也就是new Object()，
 * 我们知道HashMap是键值对存储，所以为了适应HashMap存储，HashSet增加了一个PRESENT类域（类所有），
 * 所有的键都有同一个值（PRESENT）。
 * @author yyl-pc
 *
 */
public class MyTest4 {
	public static void main(String[] args) {
		Set set = new HashSet();
		set.add("1");
		set.add("3");
		set.add("2");
		set.add("4");
		System.out.println(set);
	}
}
