package testbase;

import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import keywords.ApplicationKeywords;
import reports.ExtentManager;

//acceptable failure, critical failure, unexpected
public class BaseTest {
	
	public ApplicationKeywords app;
	public ExtentReports rep;
	public ExtentTest test;
	
	@BeforeTest(alwaysRun = true)
	public void beforeTest(ITestContext context) {
		System.out.println("----------Before Test-------------");
				
	
		// init the reporting for the test
				rep = ExtentManager.getReports();
				test =rep.createTest(context.getCurrentXmlTest().getName());
				test.log(Status.INFO, "Starting Test "+context.getCurrentXmlTest().getName());

				context.setAttribute("report", rep);
				context.setAttribute("test", test);
					
				
				// init and share it with all tests
				app = new ApplicationKeywords(); // 1 app keyword object for entire test -All @Test
		        app.setReport(test);
				
				app.openBrowser("Mozilla");
		        app.defaultLogin();		
				
				context.setAttribute("app", app);
				
			}
			
			@BeforeMethod(alwaysRun = true)
			public void beforeMethod(ITestContext context) {
				System.out.println("****Before Method****");
			    test = (ExtentTest)context.getAttribute("test");

				String criticalFailure = (String)context.getAttribute("criticalFailure");
				if(criticalFailure != null && criticalFailure.equals("Y")) {
					test.log(Status.SKIP, "Critical Failure in Prevoius Tests");
					throw new SkipException("Critical Failure in Prevoius Tests");// skip in testNG
				}
			    app = (ApplicationKeywords)context.getAttribute("app");
			    rep = (ExtentReports)context.getAttribute("report");

			}
			
			@AfterTest(alwaysRun = true)
			public void quit(ITestContext context) {
								
				rep = (ExtentReports)context.getAttribute("report");

				if(rep !=null)
					rep.flush();
			}
			

		}
