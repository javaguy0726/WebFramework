package com.dessert.cases.jd;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.dessert.base.TestInitialization;



public class ShoppingEvaluation extends TestInitialization{
	@FindBy(xpath =".//*[@id='loginname']")
	private WebElement USERNAME;
	
	@FindBy(xpath =".//*[@id='nloginpwd']")
	private WebElement PASSWORD;
	
	@FindBy(xpath =".//*[@id='loginsubmit']")
	private WebElement SUBMIT;
	
	@FindBy(xpath =".//div[@class='comt-plist'][1]//a[text()='点击评价']")
	private WebElement EVALUATION;
	
	@FindBy(xpath = ".//a[contains(@class, 'star3')]")
	private WebElement SCORE;
    
	@FindBy(xpath = ".//span[text()='商品质量']")
	private WebElement QUALITY;
	
	@FindBy(xpath = ".//div[@class='comt-plist'][1]//textarea")
	private WebElement TEXT;
	
	@FindBy(xpath = ".//*[@id='pickbutton_12273924674_2273429']")
	private WebElement PICTURE;

	
	@Test(dataProvider ="testData", description ="编写提交商品评价")//引用csv
	public void testLogin(String url,String uname,String pwd){
		
		PageFactory.initElements(seleniumUtil.driver, this);
		
		
		//登录
		basePage.open(url);
		basePage.waitForElementToLoad(USERNAME);
		basePage.typeAfterClear(USERNAME,uname);//清空之后输入
		basePage.typeAfterClear(PASSWORD,pwd);
		basePage.click(SUBMIT);
						
		basePage.waitForFixedSeconds(10);//等待10s
		basePage.click(EVALUATION);//点击评价
		basePage.waitForElementToLoad(TEXT);
		
		basePage.click(SCORE);//点击评分
		basePage.waitForFixedSeconds(10);//等待10s
		basePage.jsMouseSingleClick(QUALITY);//点击商品质量
		basePage.typeAfterClear(TEXT,"吃起来还不错");//输入文字评价
		
		
		for(int i =0; i<3 ; i++){
			basePage.click(PICTURE);//点击图片弹框
			basePage.uploadPicByAutoIt("uploadIMG.exe","guazi.jpg","文件上传");};//循环上传图片
		
			basePage.waitForFixedSeconds(10);//等待10s	
		
		//@FindBy(xpath = ".//div[@class='comt-plist'][1]//a[text()='发表评价']")
		//private WebElement SUBMIT;
		
	

	}
}
