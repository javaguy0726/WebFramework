package com.juanpi.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for creating random numbers.</p>
 * 
 * @author renta
 * @version 1.0.1
 */
public class RandomUtil {

	private static final Random random = new Random();

	/**
	 * 生成num个长度为length的字符串（字符串各不相同）,字符串只包含字母
	 * 
	 * @param length 字符串的长度
	 * @param num  字符串的个数
	 * @return
	 */
	public static String[] random(final int length, final int num) {
		return new RandomUtil().buildRandom(length, num);
	}

	/**
	 * 生成长度为length的字符串,字符串只包含数字
	 * 
	 * @param length  字符串的长度
	 * @return
	 */
	public static String random(final int length) {
		return new RandomUtil().buildRandom(length);
	}

	/**
	 * 生成num个长度为length的字符串，组成如 123-123-123 格式(只包含数字)
	 * 
	 * @param length
	 * @param num
	 * @return
	 * @throws BaseException
	 *
	 */
	public static String randomBunch(int length, int num) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < num; i++) {
			str.append(RandomUtil.random(length));
			if (i != num - 1)
				str.append("-");
		}
		return str.toString();
	}

	/**
	 * 生成num个长度为length的字符串（字符串各不相同）,字符串只包含字母
	 * 
	 * @param length
	 *            字符串的长度
	 * @param num
	 *            字符串的个数
	 * @return
	 */
	private String[] buildRandom(final int length, final int num) {
		if (num < 1 || length < 1) {
			return null;
		}
		Set<String> tempRets = new HashSet<String>(num); // 存放临时结果，以避免重复值的发生

		// 生成num个不相同的字符串
		while (tempRets.size() < num) {
			tempRets.add(buildRandom(length));
		}

		String[] rets = new String[num];
		rets = tempRets.toArray(rets);
		return rets;
	}

	/**
	 * 返回指定位数的以内的随机整数
	 * 
	 * @param length
	 * @return
	 */
	public static int buildIntRandom(final int length) {
		String maxStr = StringUtils.rightPad("1", length + 1, '0'); 
		long max = Long.parseLong(maxStr);  
		long i = Math.abs(random.nextLong()) % max;    
		String rand = String.valueOf(i);
		return Integer.parseInt(rand);
	}

	/**
	 * 取小于指定数字范围内的整数
	 * 
	 * @param number
	 * @return
	 */
	public static int buildIntRandomBy(final int number) {
		return (int) (Math.random() * number);
	}

	/**
	 * Get sub-list from the original list and make sure the sub-list size is num.
	 * 
	 * @param <E>
	 * 
	 * @param list
	 * @param num sub list size
	 * @return sub list
	 */
	public static <E> List<E> randomSubList(List<E> list, int num){
		 List<E> ran =  new ArrayList<E>();
		 for(int i=0; i<num; i++){
			 E obj = list.get(random.nextInt(list.size()));
			 if(!ran.contains(obj)){
				 ran.add(obj);
			 }
		 }
		 return ran;
	}
	
	/**
	 * Get random element of list. <p>
	 * 获取元素列表中的某一个
	 * 
	 * @param list
	 * @param num
	 * @return
	 */
	public static <E> E randomElement(List<E> list){

		return list.get(random.nextInt(list.size()));
		
	}
	
	/**
	 * 生成长度为length的字符串,字符串只包含数字
	 * 
	 * @param length
	 *            字符串的长度
	 * @return
	 */
	private String buildRandom(final int length) {
		// 长度为length的最多整数
		String maxStr = StringUtils.rightPad("1", length + 1, '0');
		// System.out.println("maxStr=" + maxStr);
		long max = Long.parseLong(maxStr);
		long i = random.nextLong(); // 取得随机数
		// System.out.println("befor i=" + i);
		i = Math.abs(i) % max; // 取正数，并限制其长度
		// System.out.println("after i=" + i);
		String value = StringUtils.leftPad(String.valueOf(i), length, '0');
		// System.out.println("length=" + length);
		// System.out.println("value=" + value);
		return value;
	}
	
	
	
}
