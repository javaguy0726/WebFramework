package com.dessert.cases.jd;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.dessert.base.TestInitialization;


public class NewAddressIdentify extends TestInitialization{

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
	
	@FindBy(xpath =".//*[@id='consigneeNameNote']")
	private List<WebElement> CONSIGNEENAMENOTE;
	
	@FindBy(xpath =".//*[@id='consigneeName']")
	private WebElement CONSIGNEENAME;
	
	@FindBy(xpath =".//*[@id='consigneeAddressNote']")
	private List<WebElement> CONSIGNADDRESSNOTE;
	
	@FindAll
	({@FindBy(xpath =".//*[@id='provinceDiv']"),
	  @FindBy(xpath =".//*[@id='cityDiv']"),
	  @FindBy(xpath =".//*[@id='countyDiv']"),
	  @FindBy(xpath =".//*[@id='townDiv']")
	})
	private List<WebElement> AREA;
	
	@FindBy(xpath =".//*[@id='consigneeAddress']")
	private WebElement CONSIGNEEADDRESS;
	
	@FindBy(xpath =".//*[@id='consigneeMobileNote']")
	private List<WebElement> CONSIGNMOBILENOTE;
	
	@FindBy(xpath =".//*[@id='consigneeMobile']")
	private WebElement CONSIGNEEMOBILE;
	
	@Test(dataProvider ="testData", description ="收货地址姓名校验")//引用csv
	public void testLogin(String url,String uname,String pwd){
		
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
		basePage.waitForElementToLoad(SAVEADDRESS);
		basePage.click(SAVEADDRESS);//保存收货地址
		assertUtil.assertElementsTextEquals(CONSIGNEENAMENOTE,"请您填写收货人姓名");//校验提示：请您填写收货人姓名
		basePage.type(CONSIGNEENAME,"shuotest");//输入收货人
		basePage.click(SAVEADDRESS);//保存收货地址
		assertUtil.assertElementsTextEquals(CONSIGNADDRESSNOTE, "请您填写收货人详细地址");//校验提示：请您填写收货人详细地址
		basePage.waitForFixedSeconds(10);//等待10s
		basePage.selectCascadingOptions(AREA,"湖北:武汉市:洪山区:城区");//选择所在地区
		basePage.type(CONSIGNEEADDRESS,"关谷大道");//输入详细地址
		basePage.click(SAVEADDRESS);//保存收货地址
		assertUtil.assertElementDisplayed(CONSIGNMOBILENOTE);//校验提示：请您填写收货人手机号码
		basePage.type(CONSIGNEEMOBILE,"1867296803");//输入错误电话
		assertUtil.assertElementDisplayed(CONSIGNMOBILENOTE);//校验提示：手机号码格式不正确
		basePage.waitForFixedSeconds(5);//等待5s
		basePage.typeAfterClear(CONSIGNEEMOBILE,"18672968031");//输入正确电话
		basePage.click(SAVEADDRESS);//保存收货地址
  
		basePage.waitForFixedSeconds(10);//等待10s		
	}	
}
