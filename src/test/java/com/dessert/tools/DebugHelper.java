package com.dessert.tools;

import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.dessert.pages.base.BasePage;
import com.opencsv.CSVReader;

/**
 * Used for debugging.
 *  
 * @author renta
 *
 *
 */
public class DebugHelper {
	
	/* 测试Selenium的 API*/
	public void testSeleniumApi(){
		
		/* 结论：如果findelement时，定位对应了多个element，它只会选择一个，，后面即使在它的基础上再使用findelements自己， 也不会找到原本的  
		 * 
		 * 如果网页的值格式为： <a> text1 <br/> text2 <a/>, getText 取到的是text1和text2
		 * */		
		
//	     WebDriver dirver = new FirefoxDriver();
//	     dirver.get("https://www.baidu.com");
//	     WebElement ele1 = dirver.findElement(By.xpath(".//form[@id='form']/span"));
//	     System.out.println(ele1.getAttribute("class"));
//	     List<WebElement> ele2 = ele1.findElements(By.xpath("."));
//	     System.out.println(ele2.size());
//	     for(WebElement e : ele2 ){
//	    	 System.out.println(e.getAttribute("class"));
//	     }
//		System.setProperty("webdriver.firefox.bin", "D:/firefox/firefox.exe");
//		
//	     WebDriver driver = new FirefoxDriver();
//	     
//	     JavascriptExecutor jse = (JavascriptExecutor)driver;  
//	     
//	     driver.get("file:///C:/Users/jp-1/Desktop/111.htm");
//	     WebElement  ele1= driver.findElement(By.id("start_right"));
//	     
//	     for(int i=0; i<20;i++){
//	    	 try{
//	    	 jse.executeScript("arguments[0].click();", ele1);  
//	    	ele1.click();
//	        driver.switchTo().alert().accept();
//	    	String text = driver.switchTo().alert().getText();
//	    	 System.out.println(text);
//	    	 }catch(Exception e){
//	    		e.printStackTrace();
//	    	 }
//	    	 
//	     }
	     
			/*结论：self::xx 可行 */  
			WebDriver dirver = new FirefoxDriver();
			dirver.get("https://www.baidu.com");
			WebElement ele1 = dirver.findElement(By.xpath(".//form[@id='form']/span"));
			WebElement ele2 = ele1.findElement(By.xpath("self::span[./input[@id='kw']]"));
			
			System.out.println(ele2.getAttribute("maxlength"));
	     
	     
	}  
	     
	
	/* 测试Java的 API 之一*/
	public void testJavaApi_1(){
		/*结论：更改控制台输出到文件 */  
//		try {
//			System.setOut(new PrintStream("e:/aaa.txt"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/*结论：获取所有的properties */  
//		System.out.println(System.getProperty("sun.boot.library.path"));
		
//		String s1 = "##￥￥$%888";
//		System.out.println(StringUtils.removePattern(s1, "\\￥"));
		
//		String s2 = "           深圳    13590182480    ";
//		String s22[] = StringUtils.split(s2);
//		System.out.println(s22[1]);
		
//		List<String> createTime = new ArrayList<String>();
//		createTime.add("1");
//		createTime.add("5");
//		createTime.add("3");
//		createTime.add("4");
//		createTime.add("8");
//		createTime.add("0");
//		
//		System.out.println(createTime.get(0));
//		System.out.println(createTime.get(1));
//		System.out.println(createTime.get(2));

	
//		String s = "2016-01-12 00:00:00";
//		System.out.println(s.length());
	
	
	}
	
	/*
	 * 测试框架内部用的API
	 */
	public void testFrameworkApi()  throws IOException{
//		RandomUtil ru = new RandomUtil();
//		String ori = "aaa";
//		for(int i=0 ; i< 20 ; i ++){
//			System.out.println(ori+ru.random(5));
//		}
		
		/*
		 * 结论：
		 */
//		Date currentDate = DateUtil.now();
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(currentDate);
//		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
//		String start = DateUtil.formatDatetime(calendar.getTime());
//		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+7);
//		String end = DateUtil.formatDatetime(calendar.getTime());	
//		System.out.println("Start..." + start + "  End..." + end);
		
		
		
		/*
		 * 结论：
		 * 1. readNext 读取csv文件的每一行，判断标志是回车（双引号外的）
		 * 2. readAll  返回LIST<String>，即所有行的所有数据
		 * 3. CSVReader 第一个构造参数代表每一行数据之间的分隔符；第二参数设定逃避字符，默认是双引号；第三个参数设定跳过头几行；
		 * 4. 
		 */
		final String ADDRESS_FILE = "src/test/java/com/juanpi/tools/addresses.csv";
		
		CSVReader reader = new CSVReader(new FileReader(ADDRESS_FILE), '\r');
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
//            System.out.println("Name: [" + nextLine[0] + "]\nAddress: [" + nextLine[1] + "]\nEmail: [" + nextLine[2] + "]");
        }

        // Try writing it back out as CSV to the console
        CSVReader reader2 = new CSVReader(new FileReader(ADDRESS_FILE), ',', '\"',2);
        List<String[]> allElements = reader2.readAll();
        System.out.println(allElements.size());
        for(String[] s1: allElements){
        	System.out.println(s1.length);
        	for(String s2: s1){
        		System.out.println(s2);
        	}
        }
//        StringWriter sw = new StringWriter();
//        CSVWriter writer = new CSVWriter(sw);
//        writer.writeAll(allElements);
//
//        System.out.println("\n\nGenerated CSV File:\n\n");
//        System.out.println(sw.toString());
		
	}
	
	/*
	 * 测试Java Api之二
	 */
	public void testJavaApi_2(){
		/*
		 * 结论：
		 * putIfAbsent(A,B) 如果A的值为空，就插入B； 如果A有值了，后续再操作均不起作用
		 * 
		 * getOrDefault(A,B) 如果A没有值，B值赋给A； 如果A有值，忽略B
		 * 
		 */
		Map<Object, Object>  map = new HashMap<>();
		map.putIfAbsent("a", "1"); 
		map.putIfAbsent("a", "2");
		
		map.put("b", "1");
		map.put("b", "2");
		
		System.out.println(map.getOrDefault("b", "3"));
		System.out.println(map.getOrDefault("c", "3"));
		
		
		
	}
	
	
	
	/*
	 * 结论：
	 * 1. readNext 读取csv文件的每一行，判断标志是回车（双引号外的）
	 * 2. readAll  返回LIST<String>，即所有行的所有数据
	 * 3. CSVReader 第一个构造参数代表每一行数据之间的分隔符；第二参数设定逃避字符，默认是双引号；第三个参数设定跳过头几行；
	 * 4. 
	 */
	public void test8(){
		String time = "2015-1-1 18:10:45";
		
		Pattern p = Pattern.compile(":\\d{2}$"); 
		Matcher m=p.matcher(time);
		
		System.out.println(m.pattern());
		System.out.println(m.find());
		System.out.println(m.start());
		System.out.println(m.group());
		System.out.println(time.replaceAll(":\\d{2}$", ":00"));
		
		
		
	}
	/**
	 * 结论：
	 * 1. DbUtil的query 和 update方法执行正常
	 */
	public void test9(){
		DbUtil db = new DbUtil();
		db.buildConnection();
//		List<Map<String, Object>> result = db.query("select * from js_sorder_info LIMIT 5");
//		for(Iterator<Map<String, Object>> li = result.iterator(); li.hasNext();){
//			System.out.println("--------------");  
//            Map<String, Object> m = li.next();  
//            for (Iterator<Entry<String, Object>> mi = m.entrySet().iterator(); mi.hasNext();) {  
//                Entry<String, Object> e = mi.next();  
//                System.out.println(e.getKey() + "=" + e.getValue());  
//            }  
//		}
		
		System.out.println("++++++++++" + db.update("UPDATE js_sorder_info SET soi_pay_status='20' ,soi_pay_time=? WHERE soi_no=? ", "2015-10-14 18:21:49", "314448179849759"));
		System.out.println("++++++++++" + db.update("UPDATE js_sorder_goods SET sg_pay_status='20' WHERE sg_order_no=?", "314448179849759"));
		
		db.closeConnection();
	}
	
	//ping 局域网所有ip
	public void test10() {
		 String ip = "192.168.1.";
		 boolean bool = false;
		 for (int i = 1 ; i < 256; i++)
		 {
			 String host = ip+i;
			 InetAddress ia = null;
			try {
				ia = InetAddress.getByName(host);
				try {
					bool = ia.isReachable(1500);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 if (bool)
		 {
			 System.out.println("主机: "+host+" 的主机名是："+ ia.getHostName() + "   完整名称是：" +ia.getCanonicalHostName()); 
		 }

		 }
	}
	
	public void test11() {
		String origin =  "12313123123";
		List<String> testList = Arrays.asList(origin);
		for(String s : testList){
			System.out.println(s);
			System.out.println(s.length());
		}
	}
	
	// 转换字符为对应的ASC码
	public void test12() {
		String origin =  "A324sdsdg";
		int  ascNum;
		byte[] gc = origin.getBytes();
		for(byte b : gc){
			ascNum = (int) b;
			System.out.println(ascNum);
		}
		System.out.println(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	// 符号优先级
	public void test13() {
		boolean rtn = false;
		System.out.println(rtn = 2>3 && 3>2 );
	}
	
	// 获取时间戳
	public void test14() {
		
//		long timeMillis = DateUtil.millis();
//		
//		String curTime = timeMillis + "";
//		curTime = curTime.substring(0, 10);
//		
//		System.out.println(curTime);   //当前时间戳
//		
//		Date date = new Date();
//		System.out.println(date.getTime());  //当前时间戳
//		
//		Date date1= DateUtil.getRelativeDay(-5);
//		
//		Date date2 = DateUtil.now();
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(date2);
//		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-5);
//		
//		date2 = calendar.getTime();
//		
//		System.out.println("date1: "+ date1+ "  date2:  "+date2);        //两种得到指定天数的方式
//		
//		Date date3= DateUtil.getRelativeDay(-10);
//		Date date4= DateUtil.getRelativeDay(-30);
//		
//		System.out.println("date3: "+ date3.getTime()+ "  date4:  "+date4.getTime());   //获取指定天数前的时间戳
//		
//		String timeString = String.valueOf(date3.getTime());
//		timeString = timeString.substring(0,10);
//		System.out.println(Long.parseLong(timeString));
		
		
		
		//-----------
		String start = "2016-01-12";
		try {
			System.out.println(DateUtil.parseDate(start));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	//测试 BigDecimal
	public void test15() {
		BigDecimal bg = new BigDecimal("112313123123123");
		System.out.println(bg.toPlainString());
		System.out.println(bg.toEngineeringString());
		System.out.println(bg.toString());
		
		
		String s = "订单号：314484149390385";
		String ss[] = s.split("：");
		System.out.println(ss[1]);
		System.out.println(s.substring(4));
	}
	
	//测试String api
	public void test16(){
//	     WebDriver dirver = new FirefoxDriver();
//	     dirver.get("https://www.baidu.com");
//	     String s = dirver.getCurrentUrl();
//	     
//	     System.out.println(s);
//	     System.out.println(s.substring(8,11));
	     
	     String s = "800x800.jpg";
//	    
//	     if(StringUtils.contains(s, "-")){
//	    	 int start = StringUtils.indexOf(s, "-");
//	    	 int end = StringUtils.indexOf(s, ".");
//	    	 String s2 = StringUtils.substring(s, start, end);
//	    	 s = StringUtils.replace(s, s2, "");
//	     }
	     
		s = s.substring(0,s.indexOf(".")+4);
		
	     System.out.println(s);
	}
	
	//测试数据库连接
	public void test17(){
		DbUtil dbUtil = new DbUtil("//192.168.143.233:3306/js_order", "juanpi", "juanpi");
		dbUtil.buildConnection();
	}
	
	//测试远程关闭pi
	public void test18(){
		SshShellUtil shell = new SshShellUtil("192.168.1.137", "pi", "123", "utf-8");
		shell.connect();
		shell.executeCommand("sudo shutdown -h now");
	}

	//远程执行环境下的shell脚本
	public void test19(){
    	SshShellUtil shell = new SshShellUtil("192.168.143.168", "www",  "www", "UTF-8"); 
        System.out.println(shell.executeCommand("cd /opt/htdocs/byd/shell;"
        		+ "source /opt/htdocs/byd/shell/php_env.sh; " 
        		+ "cp /home/www/config.php /opt/htdocs/byd/shell/SHELL/Conf/;"
        		+ "php shell.php PopsettlementFirst/index/uid/20603513; "
        		+ "php shell.php PopsettlementFirst/index/uid/20603513"
        		));
	}
	
	//测试list排序
	public void test20(){
		List<String>  l = new ArrayList<String>();
		l.add("0.1232");
		l.add("0.001");
		l.add("122344");
		l.add("0.1232");
		l.add("0.5");
		l.add("0.1232");
		l.add("122344");
		l.add("0.5");
		l.add("-4");
	 
//	        List<String> listWithoutDup = new ArrayList<String>(new HashSet<String>(l));
//	        System.out.println("list with dup:"+ l);
//	        System.out.println("list without dup:"+ listWithoutDup);
//	        
//	        Collections.sort(listWithoutDup, new Comparator<String>(){
//	            public int compare(String s1, String s2) {
//	                return Float.valueOf(s1).compareTo(Float.valueOf(s2));
//	            }
//	        });
		
	        System.out.println("最小值："+ Collections.min(l, new Comparator<String>(){
	            public int compare(String s1, String s2) {
                return Float.valueOf(s1).compareTo(Float.valueOf(s2));
            }
        }));
	        
	        
	}
	
	public void test21() {
		while (true) {
			System.out.println(RandomUtil.buildIntRandomBy(-5));
		}
	}
	
	public void test22(){
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ip);
		
	}
	public static void main(String[] args) throws IOException {
		DebugHelper  debug = new DebugHelper();
		debug.test22();
	}
	
}
