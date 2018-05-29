package a.collectionSourceCode;

import org.junit.Test;

import a.collectionSourceCode.LinkedHashMap.Entry;

/**
 * ����ҪԪ�������Mapʱ������ʹ��LinkedHashMap
 * @author yyl-pc
 *
 */
public class MyTest1 {
	//��ν��accessOrder Ϊtrue ����װԪ�صķ���˳��
	//��ʹ������ͬ��Ԫ�أ�Ҳ�������ڵ����
	//����  aa3��ͷ��㣬aa��β�ڵ�
	//��ɴ�Ӱ���� LinkedHashMap �е�afterNodeAccess����
	@Test
	public void test1() {
		LinkedHashMap<String,String> map = new LinkedHashMap<String,String>(16,0.75f,true);
		//�����put��ʹ��HashMap��put����������Entry��before��after����������أ���ΪHashMap��Node����
		//��û��before��after���ԣ�����ʹ����ģ��ģʽ��HashMap��put�����е�newNodeʵ������new LinkedHashMap��entry
		//������LinkedHashMap��newNode��linkNodeLast��LinkedHashMap��head��tail���и�ֵ
		map.put("aa", "aa");
		map.put("aa3", "aa3");
		map.put("aa2", "aa2");
		map.put("aa", "aa1");
		Entry<String,String> entry = map.head;
		Entry<String,String> next = null;
//		System.out.println(map.get("aa2"));   ������accessOrderΪtrue����ôget����Ҳ��ѵ�ǰ�ڵ�����������
		System.out.println(entry);
		while((next = entry.after)!=null) {
			System.out.println(next.key+":"+next.value);
			entry = next;
		}
	}
	//Ĭ�� accessOrder Ϊfasle������Ԫ�صĲ���˳�����ԣ�aa����ͷ�ڵ�
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
