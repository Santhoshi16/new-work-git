package keywords;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import reports.ExtentManager;

import org.testng.Reporter;
import org.testng.asserts.SoftAssert;


public class GenericKeywords {
//applicable on any webpage
	
	public WebDriver driver;
	public Properties prop;
	public Properties envProp;
	public ExtentTest test; //to log inside keywords
	public SoftAssert softAssert; //to report failure in both keywords layer and test layer
	
	
	
	public void openBrowser (String browser) {
		log ("Opening the browser "+browser);
		if(browser.equals("Chrome")){
			driver=new ChromeDriver();
		}else if(browser.equals("Mozilla")){
			driver=new FirefoxDriver();
			}else if(browser.equals("Edge")){
				driver=new EdgeDriver();
				}
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		//driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
		
	}
	public void navigate (String urlKey) {
		log("Navigating to "+urlKey);
		driver.get(envProp.getProperty(urlKey));
	
	}
	public void click (String locatorKey) {
		log("Clicking on "+locatorKey);
		getElement(locatorKey).click();
	}
	public void type (String locatorKey, String data) {
		log("Typing in "+locatorKey+". Data "+data);
         getElement(locatorKey).sendKeys(data);
		
	}
	public void clear(String locatorKey) {
		log("Clearing text field "+ locatorKey);
		getElement(locatorKey).clear();
	}
	
	public void clickEnterButton(String locatorKey) {
		log("Clinking enter button");
		getElement(locatorKey).sendKeys(Keys.ENTER);
	}
	
	public void selectByVisibleText(String locatorKey, String data) {
		Select s = new Select(getElement(locatorKey));
		s.selectByVisibleText(data);
	}
	
	
	public String getText(String locatorKey) {
		return getElement(locatorKey).getText();
	}
 
	//central function to extract elements
       public WebElement getElement (String locatorKey) {
    	   //check the presence
    	   if(!isElementPresent(locatorKey)){
    		   //report failure
    		   log("Element not present "+locatorKey);
    	   }
    	   //check the visibility

    	   if(!isElementVisible(locatorKey)){
    		   //report failure
    		   log("Element not visible "+locatorKey);
    	   }
	WebElement e = driver.findElement(getLocator(locatorKey));
	return e;
}
     public boolean isElementPresent(String locatorKey) {
    	 log("Checking Presence of "+locatorKey);
    	 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    	try {
    	 wait.until(ExpectedConditions.presenceOfElementLocated(getLocator(locatorKey)));
    	}catch (Exception e) {
    	return false;
    	}
    	 return true;
     }
     public boolean isElementVisible(String locatorKey) {
    	 log("Checking Visibility of "+locatorKey);

    	 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    	 try {
    	 wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(locatorKey)));
    	 }catch (Exception e) {
    	    	return false;
     	}
    	 
    	 return true;
     }
       
     public By getLocator(String locatorKey) {
 		By by=null;
 		
 		if(locatorKey.endsWith("_id"))
 			by = By.id(prop.getProperty(locatorKey));
 		else if(locatorKey.endsWith("_xpath"))
 			by = By.xpath(prop.getProperty(locatorKey));
 		else if(locatorKey.endsWith("_css"))
 			by = By.cssSelector(prop.getProperty(locatorKey));
 		else if(locatorKey.endsWith("_name"))
 			by = By.name(prop.getProperty(locatorKey));
 		
 		return by;
 	
 	} 
    
     
     //reporting functions
     
     public void log(String msg) {
    	 System.out.println(msg);
    	 test.log(Status.INFO, msg);
     }
     
     public void skipTest(String skipMsg) {
    	 System.out.println(skipMsg);
    	 test.log(Status.SKIP, skipMsg);
     }
     
     public void reportFailure(String failureMsg, boolean stopOnFailure) {
 		System.out.println(failureMsg);
          test.log(Status.FAIL, failureMsg); //fail in extent reports
   		takeScreenShot();//put the screenshot in reports
 		softAssert.fail(failureMsg); //fail in testng reports
 		if (stopOnFailure)
 	 		Reporter.getCurrentTestResult().getTestContext().setAttribute("criticalFailure", "Y"); //remaining @Test skip
 			assertAll();//report all failures
 	}
 	
 	public void assertAll() {
 		softAssert.assertAll();
 	} 
     
 	public void takeScreenShot(){
		// fileName of the screenshot
		Date d=new Date();
		String screenshotFile=d.toString().replace(":", "_").replace(" ", "_")+".png";
		// take screenshot
		File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			// get the dynamic folder name
			FileUtils.copyFile(srcFile, new File(ExtentManager.screenshotFolderPath+"//"+screenshotFile));
			//put screenshot file in reports
			test.log(Status.INFO,"Screenshot-> "+ test.addScreenCaptureFromPath(ExtentManager.screenshotFolderPath+"//"+screenshotFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
     
 	public void waitForPageToLoad(){
		JavascriptExecutor js = (JavascriptExecutor)driver;
		int i=0;
		// ajax status
		while(i!=10){
		String state = (String)js.executeScript("return document.readyState;");
		System.out.println(state);

		if(state.equals("complete"))
			break;
		else
			wait(2);

		i++;
		}
		// check for jquery status
		i=0;
		while(i!=10){
	
			Long d= (Long) js.executeScript("return jQuery.active;");
			System.out.println(d);
			if(d.longValue() == 0 )
			 	break;
			else
				 wait(2);
			 i++;
				
			}
		
		}
	
	public void wait(int time) {
		try {
			Thread.sleep(time*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void acceptAlert(){
		test.log(Status.INFO, "Switching to alert");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.alertIsPresent());
		try{
			driver.switchTo().alert().accept();
			driver.switchTo().defaultContent();
			test.log(Status.INFO, "Alert accepted successfully");
		}catch(Exception e){
				reportFailure("Alert not found when mandatory",true);
		}
		
	}

	// finds the row number of the data
	public int getRowNumWithCellData(String tableLocator, String data) {
		
		WebElement table = getElement(tableLocator);
		List<WebElement> rows = table.findElements(By.tagName("tr"));
		for(int rNum=0;rNum<rows.size();rNum++) {
			WebElement row = rows.get(rNum);
			List<WebElement> cells = row.findElements(By.tagName("td"));
			for(int cNum=0;cNum<cells.size();cNum++) {
				WebElement cell = cells.get(cNum);
				System.out.println("Text "+ cell.getText());
				if(!cell.getText().trim().equals("")) //if text is not blank	
					if(data.startsWith(cell.getText()))
						return(rNum+1);
			}
		}
		
		return -1; // if data is not found
	}
	

	public void quit() {
		driver.quit();
		
	}


        
     
}
