package com.dessert.tools;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author jlyu
 * @version 1.1.1
 */
public class ObjectHelper {

	private  Logger logger = Logger.getLogger(ExcelDataProvider.class.getName());
	
	
	/** 
	 * Uploading user pictures using AutoIt.
	 * 
	 * @param autoFileName autoIt script file
	 * @param imgName pic file 
	 * @param objTitle the identical object that autoIt is dealing with
	 */
	public void uploadUserPic(String autoFileName, String imgName, String objTitle){
		
		//Get the path of img file and autoIt file
		String autoItFile = autoItPath(autoFileName);
        String picFile = imgPath(imgName);
        
		Runtime uploadImage = Runtime.getRuntime();
		try {
			this.logger.info("Preparing uploading pics...");
			uploadImage.exec(autoItFile + " \"" + objTitle + "\" " + "\"" + picFile + "\"");
			this.logger.info("Uploading finished! Waiting for picture to be displayed!");
		} catch (IOException e) {
			this.logger.error("Uploading pic encounter an IO exception ! Please make sure the path of files is correct !");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
	}
	
	/** 
	 * Uploading excel files using AutoIt.
	 * 
	 * @param autoFileName autoIt script file
	 * @param xlsName pic file 
	 * @param objTitle the identical object that autoIt is dealing with
	 */
	public void uploadExcel(String autoFileName, String xlsName, String objTitle){
		
		//Get the path of img file and autoIt file
		String autoItFile = autoItPath(autoFileName);
		String excel =xlsPath(xlsName);
		
		Runtime uploadImage = Runtime.getRuntime();
		try {
			uploadImage.exec(autoItFile + " \"" + objTitle + "\" " + "\"" + excel + "\"");
			this.logger.info("Uploading finished! Waiting for excel to be displayed!");
		} catch (IOException e) {
			this.logger.error("Uploading excel encounter an IO exception ! Please make sure the path of files is correct !");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/** 
	 * Downloading excel files using AutoIt.
	 * 
	 * @param autoFileName autoIt script file
	 * @param xlsName pic file 
	 * @param objTitle the identical object that autoIt is dealing with
	 */
	public void downloadExcel(String autoFileName, String xlsName, String objTitle, String alertTitle){
		
		//Get the path of img file and autoIt file
		String autoItFile = autoItPath(autoFileName);
		String excel = xlsPath(xlsName);
		
		Runtime uploadImage = Runtime.getRuntime();
		try {
			uploadImage.exec(autoItFile + " \"" + objTitle + "\" " + "\"" + excel + "\" " + "\"" + alertTitle + "\"");
			this.logger.info("Downloading finished!");
		} catch (IOException e) {
			this.logger.error("Downloading pic encounter an IO exception ! Please make sure the path of files is correct !");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * Get the path to autoIt file
	 * 
	 */
	public String autoItPath(String fileName){
		String userDir = System.getProperty("user.dir");
		String autoItPath =  userDir+"\\src\\test\\resources\\com\\dessert\\assist\\autoit\\"+fileName;
		return autoItPath;
		
	}
	
	/**
	 * Get the path to img files
	 * 
	 */
	public String imgPath(String fileName){
		String userDir = System.getProperty("user.dir");
		String autoItPath =  userDir+"\\src\\test\\resources\\com\\dessert\\data\\img\\"+fileName;
		return autoItPath;
	}
	
	/**
	 * Get the path to xls files
	 * 
	 */
	public String xlsPath(String fileName){
		String userDir = System.getProperty("user.dir");
		String autoItPath =  userDir+"\\src\\test\\resources\\com\\dessert\\data\\xls\\"+fileName;
		return autoItPath;
	}

	
}
