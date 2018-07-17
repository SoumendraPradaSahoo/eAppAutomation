package eApp_Automation;

import utility.Log;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FunctionLibrary {
	static int TimeOutSeconds; //Time to wait for the elements to be available
	static WebDriver driver;
	static String[] identifiers;
	static By by;
	static By byClientMsg;
	static String variableLocator;
	static String ScreenShotPath;
	static String TestCaseID;
	static int TestStepNo;
	//static WebElement wbElement;
	//Login Function
	public static void login(String browser_type, String url, String uname, String pwd, int timeout)
	{  
		TimeOutSeconds = timeout;
		System.out.println("Starting Automation");  
		if (browser_type.equalsIgnoreCase("Chrome"))
		{
			System.setProperty("webdriver.chrome.driver", "D:/chromedriver_win32/chromedriver.exe");
			driver = new ChromeDriver();
		}
		if (browser_type.equalsIgnoreCase("IE"))
		{
			System.setProperty("webdriver.ie.driver", "D:/chromedriver_win32/IEDriverServer.exe");
			driver = new InternetExplorerDriver();
		}
		driver.manage().timeouts().pageLoadTimeout(TimeOutSeconds,TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);

		try
		{		
			driver.get(url);
			driver.manage().window().maximize();
			System.out.println("Launch Console Page Title: " + driver.getTitle());
			Thread.sleep(3000);  // Let the user actually see something!
			for(String winHandle : driver.getWindowHandles()){
				//System.out.println(winHandle);
				driver.switchTo().window(winHandle);
			}
			System.out.println("Driver changed to Login Console");
			System.out.println("Login Console Page Title: " + driver.getTitle());
			waitForAjax();
			new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
			driver.findElement(By.name("username")).sendKeys(uname);
			driver.findElement(By.name("password")).sendKeys(pwd);
			driver.findElement(By.name("_eventId__logon")).click();
		}
		catch (Exception e){
			Log.error("Error in Login");
			e.printStackTrace();
			Log.error(e.toString());
		}

	}

	//Logout Function
	public static void logout() {
		String[] currHandle= new String[5];
		int i=0;
		System.out.println("Went to logout java");
		for(String winHandle : driver.getWindowHandles()){
			currHandle[i++]=winHandle;
		}
		driver.switchTo().window(currHandle[1]);

		try{
			new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable((By.id("logout")))).click();
			System.out.println("Logout is clicked");
			//Handle Popup	
			Alert alert = driver.switchTo().alert();
			alert.accept();
			driver.switchTo().window(currHandle[0]);
			driver.quit();
		}
		catch (Exception e){
			Log.error("Error in Logout");
			e.printStackTrace();
			Log.error(e.getMessage());
		}
	}

	//FreshCase - Closing the already opened window
	public static void freshCase() {
		String[] currHandle = new String[driver.getWindowHandles().size()];
		int i=0;
		System.out.println("Went to FreshCase");
		for(String winHandle : driver.getWindowHandles()){
			currHandle[i++]=winHandle;
		}

		if (i>2)
			driver.close();	
		driver.switchTo().window(currHandle[1]);
	}

	public static void executeStep(String[] testData, int[] rowid, String identifier_fileName, boolean freshCase) 
	{
		//Cleaning additional window for fresh case
		if (freshCase)
			freshCase();

		String screenName = testData[0];
		String fieldName = testData[1];
		String step = testData[2];
		String data = testData[3];
		variableLocator = data;

		try {
			identifiers = ReadPageManager.getLocators(identifier_fileName, screenName, fieldName);
		}catch (Exception e)
		{
			Log.error("Error in getting identifier in executeStep in FunctionLibrary class");
			e.printStackTrace();
			Log.error(e.toString());
			return;
		}


		try {
			by = getByClass(identifiers[1], identifiers[2]);//setting by for field
			byClientMsg = getByClass(identifiers[3], identifiers[4]);//setting by for corresponding client side message
		}catch (Exception e){
			Log.error("Error in getByClass in executeStep in FunctionLibrary class");
			e.printStackTrace();
			Log.error(e.toString());
			return;}

		switch (step.toUpperCase())
		{
		case "SETVALUE":
			setValue(identifiers[0], data);
			break;
		case "GETVALUE":
			getValue(identifiers[0], rowid);
			break;
		case "CLICK":
			clickButton();
			break;
		case "VERIFYCLIENTMESSAGE":
			try {
				verifyClientMsg(data);
			} catch (IOException e) {
				Log.error("Error in verifyClientMsg Function in FunctionLibrary Class: " + e.toString());
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	public static void setValue(String fieldtype, String data)
	{
		try {
			WebElement wbElement;
			Select temp_ddlb;
			List<WebElement> wbElementList;

			switch (fieldtype.toUpperCase()) {
			case "TEXTBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				wbElement.sendKeys(data);
				wbElement.sendKeys(Keys.TAB);
				waitForAjax();
				break;
			case "DDLB":
				temp_ddlb= new Select(new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by)));	
				temp_ddlb.selectByVisibleText(data);
				waitForAjax();
				break;
			case "RADIOOPTION":
				wbElementList= new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(driver.findElements(by)));
				setRadioOptions(wbElementList, data);
				waitForAjax();
				break;
			case "CHECKBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(by));
				if (data.equalsIgnoreCase("YES")){//Checking if Select or not to select
					if ( !(wbElement.isSelected()) )
					{
						wbElement.click();
					}}
				if (data.equalsIgnoreCase("NO")){//Checking if Select or not to select
					if (wbElement.isSelected())
					{
						wbElement.click();
					}
				}
				waitForAjax();
				break;
			default:
				break;

			}}
		catch (Exception e)
		{
			Log.error("Error in setValue for field type '" + fieldtype + "' and data '"+ data + "' in FunctionLibrary class");
			e.printStackTrace();
			Log.error(e.toString());
		}

	}

	public static void getValue(String fieldtype, int[] rowid)
	{
		try {
			WebElement wbElement;
			Select temp_ddlb;
			List<WebElement> wbElementList;
			String tempdata = "";
			switch (fieldtype.toUpperCase()) {
			case "TEXTBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
				tempdata = wbElement.getAttribute("value");
				break;
			case "DDLB":
				temp_ddlb= new Select(new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by)));	
				tempdata = temp_ddlb.getFirstSelectedOption().getText();
				break;
			case "RADIOOPTION":
				wbElementList= new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.visibilityOfAllElements(driver.findElements(by)));
				tempdata = getRadioOptions(wbElementList);
				break;
			case "CHECKBOX":
				wbElement = new WebDriverWait(driver, TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(by));
				if (wbElement.isSelected())
					tempdata = "Yes";
				else
					tempdata = "No";
				break;
			default:
				break;
			}

			Log.info("Writing test data '" + tempdata + "' into cell (" +rowid[0] + "," + rowid[1] + ")");
			WriteTestData.setTestData(rowid, tempdata);
			Report.PutInfo("Writing data '" + tempdata + "' into cell (" +rowid[0] + "," + rowid[1] + ") is successfull");
		}		

		catch (Exception e)
		{
			Log.error("Error in getValue for field type '" + fieldtype + "' and row id (" +rowid[0] + "," + rowid[1] + ") in FunctionLibrary class");
			e.printStackTrace();
			Log.error(e.toString());
		}
	}

	public static void clickButton()
	{
		try{
			WebElement wbElement;
			wbElement = new WebDriverWait(driver,TimeOutSeconds).until(ExpectedConditions.elementToBeClickable(by));
			wbElement.click();
			waitForAjax();

			//Switch to new page if new page opens
			for(String winHandle : driver.getWindowHandles()){
				System.out.println(winHandle);
				driver.switchTo().window(winHandle);
			}
		}
		catch(Exception e)
		{
			Log.error("Error in clickButton in FunctionLibrary class");
			e.printStackTrace();
			Log.error(e.toString());
		}
	}

	public static void verifyClientMsg(String message) throws IOException
	{
		String report_text = "";
		String actual_message = "";
		report_text = "Verification of client side message, Expected: " + message + " , Actual: " ;
		try{
			WebElement wbElement;
			wbElement = new WebDriverWait(driver,2).until(ExpectedConditions.visibilityOfElementLocated(byClientMsg));
			waitForAjax();
			actual_message = wbElement.getText();
			report_text = report_text + actual_message;

			if (actual_message.equals(message)) {
				Report.PutPass(report_text);
				//System.out.println("Last Name Pass"); 
			}
			else
			{
				Report.PutFail(report_text);
				//System.out.println("Last Name Fail");
			}

		}
		/*catch(Exception e)
		{
			Log.error("Error in verifyClientMsg in FunctionLibrary class " + e.toString());
			Log.error("Error in verifying Client Side message '" + message + "'");
			Report.PutFail(report_text);
			e.printStackTrace();
		}*/
		catch(Exception e){
			if (message=="")
				Report.PutPass("Verification of client side message, Expected: No Messsage, Actual: No Message");	
			else {
				Log.error("Error in verifyClientMsg in FunctionLibrary class " + e.toString());
				Log.error("Error in verifying Client Side message '" + message + "'");
				Report.PutFail(report_text);
				e.printStackTrace();
			}
		}
	}


	public static By getByClass(String identifier, String locator){

		By temp = null;
		if (locator.contains("+")) {
			String[] tempString = locator.split("\\+");
			//System.out.println("tempString length " + tempString.length);

			if (tempString.length == 2)
				locator=tempString[0] + variableLocator;
			if (tempString.length == 3)
				locator=tempString[0] + variableLocator + tempString[2];
		}	
		switch (identifier.toUpperCase())
		{
		case "NAME":
			temp = By.name(locator);
			break;
		case "ID":
			temp = By.id(locator);
			break;
		case "XPATH":
			temp = By.xpath(locator);
			break;
		default:
			break;

		}
		return temp;
	}

	//Wait method for Ajax call to be completed
	public static void waitForAjax() {
		new WebDriverWait(driver, TimeOutSeconds).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				JavascriptExecutor js = (JavascriptExecutor) d;
				return (Boolean) js.executeScript("return jQuery.active == 0");
			}
		});
	}

	//Method to set the Radio Options
	public static void setRadioOptions(List<WebElement> radio_element, String dataValue)
	{
		WebElement temp;
		if (dataValue != "") {
			try{
				for(int i=0;i<radio_element.size();i++){
					radio_element.get(i).getLocation();	
					temp = radio_element.get(i).findElement(By.xpath("following-sibling::label"));
					//System.out.println("Value of radio element " + temp.getText());
					if (temp.getText().equalsIgnoreCase(dataValue))
					{
						pressTab();
						radio_element.get(i).click();
						//System.out.println("Radio option is clicked throuhg webdriver");
						return;
					}}
				Report.PutFail("Not able to select the radio option as " + dataValue);
			}
			catch(Exception e)
			{
				Log.error("Error in setRadioOptions in FunctionLibrary class");
				e.printStackTrace();
				Log.error(e.toString());
			}}
	}

	public static String getRadioOptions(List<WebElement> radio_element)
	{
		WebElement temp;
		try{
			for(int i=0;i<radio_element.size();i++){
				if (radio_element.get(i).isSelected()) {
					radio_element.get(i).getLocation();	
					temp = radio_element.get(i).findElement(By.xpath("following-sibling::label"));
					//System.out.println("Radio Option is " + temp.getText());
					return temp.getText();
				}
			}

			System.out.println("None of the Radio Option is selected");
			return "";
		}

		catch(Exception e)
		{
			Log.error("Error in getRadioOptions in FunctionLibrary class");
			e.printStackTrace();
			Log.error(e.toString());
			return "";
		}

	}

	public static void setScreenShotPath(String screenshotPath){
		ScreenShotPath = screenshotPath;
	}


	public static void setTestCaseNoandStep(String TestCaseId, int stepNo){
		TestCaseID = TestCaseId;
		TestStepNo = stepNo;
	}

	public static String captureScreen(){
		String ScreenShotFileName;
		//Setting the Time Instance for the File Name
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		String temp = dateFormat.format(date);
		temp = temp.replace("/", "_");
		temp = temp.replace(" ", "_");
		temp = temp.replace(":", ""); //Report File Name ends
		ScreenShotFileName = TestCaseID + "_" + TestStepNo + "_" + temp + ".png";
		try {
			File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(src, new File(ScreenShotPath + "/" + ScreenShotFileName));
			return ScreenShotPath + "/" + ScreenShotFileName;
		}
		catch(Exception e){
			Log.error("Error in captureScreen in FunctionLibrary class: " + e.toString());
			e.printStackTrace();
			return "";
		}
	}

	public static void pressTab(){
		Actions mouse = new Actions(driver);
		mouse.sendKeys(Keys.TAB).build().perform();
		waitForAjax();
	}
}

