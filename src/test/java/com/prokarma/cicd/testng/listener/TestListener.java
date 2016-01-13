package com.prokarma.cicd.testng.listener;

import java.io.IOException;
import java.net.URISyntaxException;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.prokarma.update.UpdateTestCaseResult;

public class TestListener implements ITestListener {

	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub
		System.out.println("Test case finish ......................");
	}

	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub
		
		System.out.println("Test case Start ......................");

	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	public void onTestFailure(ITestResult arg0) {
		// TODO Auto-generated method stub
		System.out.println("Test case failure ......................");
		String workSpaceRefNumber=arg0.getTestClass().getXmlTest().getSuite().getParameter("workspaceRefNumber");
		String projectRefNumber=arg0.getTestClass().getXmlTest().getSuite().getParameter("projectRefNumber");
		String tcNumber=arg0.getTestClass().getXmlTest().getParameter("tcNumber");
		updateTestResult(workSpaceRefNumber,projectRefNumber,tcNumber,"Fail");

	}

	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	public void onTestStart(ITestResult arg0) {
		// TODO Auto-generated method stub

	}

	public void onTestSuccess(ITestResult arg0) {
		// TODO Auto-generated method stub
		System.out.println("Test case Pass ......................");
		String workSpaceRefNumber=arg0.getTestClass().getXmlTest().getSuite().getParameter("workspaceRefNumber");
		String projectRefNumber=arg0.getTestClass().getXmlTest().getSuite().getParameter("projectRefNumber");
		String tcNumber=arg0.getTestClass().getXmlTest().getParameter("tcNumber");
		updateTestResult(workSpaceRefNumber,projectRefNumber,tcNumber,"Pass");

	}
	
	private void updateTestResult(String workSpace,String project,String tcNumber,String status){
		UpdateTestCaseResult result=new UpdateTestCaseResult();
		
		try {
			result.updateTestCaseWithResult(workSpace, project, tcNumber, status);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
