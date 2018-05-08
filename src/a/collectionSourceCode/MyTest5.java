package a.collectionSourceCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Comparable和Comparator的使用
 * @author yyl-pc
 *
 */
public class MyTest5 {
	public static void main(String[] args) {
		Person p1 = new Person("张三",14);
		Person p2 = new Person("李四",13);
		Person p3 = new Person("王五",15);
		List<Person> list = Arrays.asList(p1,p2,p3);
		list.sort(new Comparator<Person>() {
			@Override
			public int compare(Person o1, Person o2) {
				return o1.age-o2.age;
			}
		});
		System.out.println(list);
		Collections.sort(list);
		System.out.println(list);
	}
	
	
	static class Person implements Comparable<Person>{
		private String name;
		private int age;
		@Override
		public String toString() {
			return "Person [name=" + name + ", age=" + age + "]";
		}
		public Person(String name, int age) {
			super();
			this.name = name;
			this.age = age;
		}
		@Override
		public int compareTo(Person o) {
			return o.age-this.age;
		}
		
	}
}
