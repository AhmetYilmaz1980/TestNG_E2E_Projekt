package com.e2e.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.e2e.utilities.BrowserUtils;
import com.e2e.utilities.ConfigurationReader;
import com.e2e.utilities.Driver;
import com.github.javafaker.Faker;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.time.Duration;

public class TestBase {

   Faker faker;
   protected WebDriver driver;
   protected WebDriverWait wait;
   protected Actions actions;
   protected ExtentReports report;
   protected ExtentHtmlReporter htmlReporter;
   protected ExtentTest extentLogger;

   @BeforeTest
   public void setUpTest(){
      report = new ExtentReports();

      String projectPath = System.getProperty("user.dir");
      String reportPath = projectPath+"/test-output/report.html";

//        String date = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//        String reportPath = projectPath+"/test-output/report"+date+".html";

      htmlReporter = new ExtentHtmlReporter(reportPath);

      report.attachReporter(htmlReporter);

      htmlReporter.config().setReportName("Smoke Test");

      report.setSystemInfo("Environment","Test");
      report.setSystemInfo("Browser", ConfigurationReader.get("browser"));
      report.setSystemInfo("OS", System.getProperty("os.name"));
      report.setSystemInfo("tester", System.getProperty("user.name"));
      report.setSystemInfo("PO", "Figen Hanım");

   }
   @AfterTest
   public void tearDownTest(){
      report.flush();
   }

   @BeforeMethod
   public void setUp() {
      driver = Driver.get();
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
      driver.manage().window().setPosition(new Point(-1000,0));
      driver.manage().window().maximize();
      wait = new WebDriverWait(driver,Duration.ofSeconds(10));
      actions = new Actions(driver);
      faker = new Faker();
      //  driver.get(ConfigurationReader.get("url"));
   }

   @AfterMethod
   public void tearDown(ITestResult result) throws IOException {
      if (result.getStatus() == ITestResult.FAILURE){
         //başarısız testin adını alalım
         extentLogger.fail(result.getName());
         //ekran görüntüsünü alalım
         String screenshotPath = BrowserUtils.getScreenshot(result.getName());
         //screenshot'ı rapora ekleyelim
         extentLogger.addScreenCaptureFromPath(screenshotPath);
         //ayrıca log kayıtlarını da ekleyelim (exception)
         extentLogger.fail(result.getThrowable());
      }
      BrowserUtils.waitFor(2);
      Driver.closeDriver();
   }
}
