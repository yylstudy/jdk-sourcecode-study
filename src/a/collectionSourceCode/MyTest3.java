package a.collectionSourceCode;

import java.util.Map;

import org.junit.Test;

/**
 * ��������Ҫ�Ѳ����Ԫ�ؽ��������ʱ�򣬾���ʱ����TreeMap��
 * ����������������ˡ�����
 * @author yyl-pc
 *
 */
public class MyTest3 {
	@Test
	/**
	 * ����TreeMap���Զ���key��������
	 */
	public void test1() {
		Map<String,String> map = new TreeMap<String,String>();
		map.put("aa", "aa");
		map.put("cc", "cc");
		map.put("bb", "bb");
		System.out.println(map);
	}
}
