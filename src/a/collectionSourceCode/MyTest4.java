package a.collectionSourceCode;

import java.util.Set;

/**
 * HashSet��ʹ�ã�HashSet�ײ���ʹ��HashMap����LinkedHashMapʵ�ֵģ�HahsSet��ŵ���keyֵ�ļ��ϣ�
 * ����keyֵ��Ӧ��valueֵ����Hash����ĳ���PRESENT��Ҳ����new Object()��
 * ����֪��HashMap�Ǽ�ֵ�Դ洢������Ϊ����ӦHashMap�洢��HashSet������һ��PRESENT���������У���
 * ���еļ�����ͬһ��ֵ��PRESENT����
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
