package com.juanpi.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/**
 * @author jlyu
 * 
 * 对CVS文件进行操作的工具类, 包括读写等.
 * 
 */
public class CSVDataProvider {

	private Logger logger;
	
	public CSVDataProvider(){
		this.logger = Logger.getLogger(CSVDataProvider.class.getName());
	}
	
	
	/**
	 * Read data from CSV files. The CSV files are placed under directory and names end with Data.
	 * Method name of test case should match csv file name excluding the last "Data" string. 
	 * ex: if method name =  frontAccount,  then csv file name should be frontAccountData.csv
	 * 
	 * Except for the 1st line of CSV file, all lines will be read and returned.
	 * 
	 * 将CSV文件中的数据一行一行的读取出来，每一行为一个字符串数组，然后插入到一个Object二维数组中
	 * 
	 * @param 
	 *      classPath test method name  约定：表名Data前面部分为需要参数化的测试方法名
	 * @return 
	 *      allData 返回表中所有数据
	 * @throws 
	 *      Exception
	 * 
	 */
	public Object[][] readCSVData(String classPath) {
		
		String filePath = "src/test/resources/" + classPath + "Data.csv";
		Object[][] allData = new Object[0][];
		CSVReader reader = null;
		try {
			reader = new CSVReader(new InputStreamReader(FileUtils.openInputStream(new File(filePath)),"UTF-8"), ',', '\"', 1);  		//忽略第一行数据
			List<String[]> allElements = reader.readAll();
			int preSize = allElements.size();
			int i = 0;
			while(i < allElements.size()){                              //去除注释行
				if(allElements.get(i)[0].equals("###")){
					allElements.remove(i);
					continue;
				}else{
					i++;
				}
			}
			int afterSize = allElements.size();
			
			this.logger.info("本用例共有<"+preSize+">行测试数据, 已经注释掉<"+(preSize-afterSize)+">行，剩余<"+afterSize+">行测试数据.");
			
			allData = new Object[allElements.size()][];
			for (int j = 0; j < allElements.size(); j++) {
				allData[j] = (Object[]) allElements.get(j);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return allData;
	}
		
	public void writeCSVData(String csvFile, List<String[]> allElements){
		CSVWriter writer = null;
		try{
			writer =  new CSVWriter(new OutputStreamWriter(FileUtils.openOutputStream(new File(csvFile)),"UTF-8"), ','); 
			writer.writeAll(allElements);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
}		
