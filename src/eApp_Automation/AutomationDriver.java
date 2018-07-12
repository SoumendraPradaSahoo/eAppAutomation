package eApp_Automation;

import utility.Log;
import java.io.IOException;
import java.util.Properties;

public class AutomationDriver {
	static int testCaseNo;
	private static int TimeOutSeconds; // = 30; //Time to wait for the elements to be available
	private static String FILE_NAME; //= "D:/eApp Automation/eApp Test Data.xlsx"; //Path for Test Data
	private static String FieldIdentifier_Loc; //= "D:/eApp Automation/Object/PageIdentifier.xlsm"; //Path for Field Identifier Data
	private static String Report_Path; //= "D:/eApp Automation/Reports"; //Path for the report file to be generated
	private static String Screenshot_Path; //= "D:/eApp Automation/Screenshots"; //Path for the screenshots to be generated
	private static String eApp_URL; //= "http://20.15.86.50:8080/eApps/"; //Eapp URL
	private static String agent_ID; //= "ssahoo43"; //Agent ID
	private static String agent_PWD; //= "vilink"; //Password
	private static String browser_type; //Chrome or IE
	static int ColumnNo;
	static String[] objects;
	
	public static void main(String[] args) throws IOException{
		//Read Property File
		Properties p = new Properties();
		try {
			p=ReadConfigFile.getObjectRepository();
		} catch (IOException e) {
			Log.error("Error in reading property file: " + e.toString());
			e.printStackTrace();
			return;
		}
		FILE_NAME = p.getProperty("TestData_Filename");
		FieldIdentifier_Loc = p.getProperty("FieldIdentifier_Filename");
		Report_Path = p.getProperty("Report_Path");
		Screenshot_Path = p.getProperty("Screenshot_Path");
		browser_type = p.getProperty("Browser");
		eApp_URL = p.getProperty("URL");
		agent_ID = p.getProperty("agent_ID");
		agent_PWD = p.getProperty("agent_PWD");
		TimeOutSeconds = Integer.parseInt(p.getProperty("time_out_Second"));
		//Property File Reading Ends
		
		
		//Login to Application
		try {
			Log.info("Initializing Browser for Automation");
			FunctionLibrary.login(browser_type, eApp_URL, agent_ID, agent_PWD, TimeOutSeconds);
			Log.info("Browser Initialization Successfull");
		} catch (Exception e) {
			Log.error("Error in calling FunctionLibrary.login in AutomationDriver class");
			e.printStackTrace();
			Log.error(e.getMessage());
			 return;
		}		//Login ends
		
		int total_TestCases = 0;
		//Get no of test cases to be executed
		try {
			total_TestCases = ReadTestData.getTestCases(FILE_NAME);
		} catch (IOException e) {
			Log.error("Error in calling ReadTestData.getTestCases in AutomationDriver class");
			e.printStackTrace();
			Log.error(e.getMessage());
			return;
		}
		
		//Get total no of rows in TestData sheet
		int totalnoofrows = 0;
		try{
			totalnoofrows= ReadTestData.getTotalNoOfRows(FILE_NAME);
		}
		catch (Exception e)
			{
			Log.error("Error in calling ReadTestData.getTotalNoOfRows in AutomationDriver class");
			e.printStackTrace();
			Log.error(e.getMessage());
			return;
			}
		
		//Initializing report in the 1st test case
		Log.info("Initialization Report");
		Report.InitializeReport(Report_Path);
		Log.info("Report Initialization Successfull");
		
		//Initializing Screenshot path in the 1st test case
			Log.info("Initializing Screenshot path");
			FunctionLibrary.setScreenShotPath(Screenshot_Path);
			Log.info("Report Initialization Successfull");
		
		//Starting execution of test cases
		for(int i=1; i <= total_TestCases; i++)
		{
			
			String[] testcaseid = ReadTestData.getExecutable(FILE_NAME, i);
			boolean freshcase = true;
			int testcasecolumnno = -1;
			if (testcaseid[1].equalsIgnoreCase("YES")) //Only test cases have Yes will be executed
			{
				Report.CreateTest(testcaseid[0]);
				testcasecolumnno = ReadTestData.getTestCaseColumnNo(FILE_NAME,testcaseid[0]);
				System.out.println("Starting Test Case " + testcaseid[0] );
				Log.info("Starting Test Case " + testcaseid[0]);
				int startRowOfTestCase = 2;
				Boolean continuestep = false;
				String data = "";
				do{				
				objects = ReadTestData.getTestCaseData(FILE_NAME, testcasecolumnno, startRowOfTestCase);
				data = objects[3];
				int[] rowid = {startRowOfTestCase, testcasecolumnno}; //Setting the row id for the test data to be written into the file
				if (!(data.trim().equalsIgnoreCase("END")))
					continuestep = true;
				else
					continuestep = false;
				if (!(data.equalsIgnoreCase("SKIP")) && continuestep )
				{
				FunctionLibrary.setTestCaseNoandStep(testcaseid[0], startRowOfTestCase); // Setting the TestCase id and row no in FuncationLibrary class
				FunctionLibrary.executeStep(objects, rowid ,FieldIdentifier_Loc,freshcase);
				freshcase=false;
				}
				startRowOfTestCase++;
				} while ((continuestep) && (startRowOfTestCase <= totalnoofrows) );
				System.out.println("Execution Completed for Test Case " + testcaseid[0] );
				Log.info("Execution Completed for Test Case " + testcaseid[0]);
			}
		}
		
		//Building the report
		Report.BuildReport();
		Log.info("Report Building Completed");
		
		//logout of application
		try{
		Log.info("Reporfiming Logout");
		FunctionLibrary.logout();
		Log.info("Logout Successfull");
		}
		catch (Exception e){
			e.printStackTrace();
			Log.error("Logout Unsuccessfull " + e.getMessage());
			 return;}
}
}