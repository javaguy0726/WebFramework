package com.dessert.cases.jd;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.dessert.base.TestInitialization;

public class CreateNewAddress extends TestInitialization{

	@FindBy(xpath =".//*[@id='loginname']")
	private WebElement USERNAME;
	
	@FindBy(xpath =".//*[@id='nloginpwd']")
	private WebElement PASSWORD;
	
	@FindBy(xpath =".//*[@id='loginsubmit']")
	private WebElement SUBMIT;
	
	@FindBy(xpath =".//a[text()='我的订单']")
	private WebElement ORDER;
	
	@FindBy(xpath =".//a[text()='收货地址']")
	private WebElement ADDRESS;
	
	@FindBy(xpath =".//a[text()='新增收货地址']")
	private WebElement NEWADDRESS;
	
	@FindBy(xpath =".//a[text()='保存收货地址']")
	private WebElement SAVEADDRESS;
	
	@FindBy(xpath =".//*[@id='consigneeName']")
	private WebElement CONSIGNEENAME;
	
	@FindAll
	({@FindBy(xpath =".//*[@id='provinceDiv']"),
	  @FindBy(xpath =".//*[@id='cityDiv']"),
	  @FindBy(xpath =".//*[@id='countyDiv']"),
	  @FindBy(xpath =".//*[@id='townDiv']")
	})
	private List<WebElement> AREA;
	
	@FindBy(xpath =".//*[@id='consigneeAddress']")
	private WebElement CONSIGNEEADDRESS;
	
	@FindBy(xpath =".//*[@id='consigneeMobile']")
	private WebElement CONSIGNEEMOBILE;
	
	@FindBy(xpath =".//*[@id='consigneeEmail']")
	private WebElement CONSIGNEEEMAIL;
	
	@Test(dataProvider ="testData", description ="京东新增收货地址")//引用csv
	public void testLogin(String url,String uname,String pwd,String name,String zone,String address,String mobile,String email){
		
		PageFactory.initElements(seleniumUtil.driver, this);
		
		//登录
		basePage.open(url);
		basePage.waitForElementToLoad(USERNAME);
		basePage.typeAfterClear(USERNAME,uname);//清空之后输入
		basePage.typeAfterClear(PASSWORD,pwd);
		basePage.click(SUBMIT);
				
		basePage.waitForFixedSeconds(10);//等待10s
		
		//点击我的订单
		basePage.switchToNewWindow(ORDER);
		basePage.waitForElementToLoad(ADDRESS);
		
		//点击收货地址
		basePage.click(ADDRESS);
		basePage.waitForElementToLoad(NEWADDRESS);
		
		//点击新增收货地址
		basePage.click(NEWADDRESS);
		basePage.waitForFixedSeconds(10);//等待10s
		basePage.type(CONSIGNEENAME,name);//输入收件人
		basePage.selectCascadingOptions(AREA,zone);//选择所在地区
		basePage.type(CONSIGNEEADDRESS,address);//输入详细地址
		basePage.type(CONSIGNEEMOBILE,mobile);//输入电话
		basePage.type(CONSIGNEEEMAIL,email);//输入邮箱地址
		basePage.click(SAVEADDRESS);//保存收货地址
		basePage.waitForElementToLoad(NEWADDRESS);
		
	}
	
}
