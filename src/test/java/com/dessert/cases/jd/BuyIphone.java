package com.dessert.cases.jd;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.dessert.base.TestInitialization;


public class BuyIphone extends TestInitialization{
	@FindBy(xpath =".//*[@id='loginname']")
	private WebElement USERNAME;
	
	@FindBy(xpath =".//*[@id='nloginpwd']")
	private WebElement PASSWORD;
	
	@FindBy(xpath =".//*[@id='loginsubmit']")
	private WebElement SUBMIT;
	
	@FindBy(xpath =".//*[@id='categorys-2014']/div[2]/div[1]/div[2]/h3/a[1]")
	private WebElement CATEGORY;
	
	@FindBy(xpath =".//*[@id='category-item-2']/div[3]/dl[1]/dd/a[1]")
	private WebElement CATEGORYCHILD;
	
	
	@FindBy(xpath =".//em[text()='Apple iPhone 6s Plus (A1699) 64G 玫瑰金色 移动联通电信4G手机']")
	private WebElement IPHONE;
	
	@FindBy(xpath =".//*[@id='choose-color']/div[2]/div[2]/a")
	private WebElement SELECTCOLOR;
	
	@FindBy(xpath =".//*[@id='InitCartUrl']/b")
	private WebElement SHOOPING;
	
	@Test(dataProvider ="testData", description ="京东购买手机")//引用csv
	public void testLogin(String url,String uname,String pwd){
		
		PageFactory.initElements(seleniumUtil.driver, this);
		
		//登录
		basePage.open(url);
		basePage.waitForElementToLoad(USERNAME);
		basePage.typeAfterClear(USERNAME,uname);//清空之后输入
		basePage.typeAfterClear(PASSWORD,pwd);
		basePage.click(SUBMIT);
		
		basePage.waitForFixedSeconds(10);//等待10s
		
		//选择手机分类
		basePage.waitForElementToLoad(CATEGORY);
		basePage.mouseMoveClickToNewWindow(CATEGORY,CATEGORYCHILD);//点击手机分类
		
		basePage.waitForElementToLoad(IPHONE);
		
	    //选择iphone
		basePage.click(IPHONE);
		basePage.waitForFixedSeconds(10);//等待10s
		
		//选择颜色
		basePage.click(SELECTCOLOR);
		basePage.waitForElementToLoad(SHOOPING);
		basePage.click(SHOOPING);
		basePage.waitForFixedSeconds(10);	
		
	}	
}