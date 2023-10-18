package com.cst438;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EndToEndTestAssignments {
    public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/aaronmartinez/Downloads/chromedriver-mac-arm64/chromedriver";
    public static final String URL = "http://localhost:3000";
    public static final int SLEEP_DURATION = 1000; // 1 second.
    WebDriver driver;

    @Test
    public void editAssignmentTest() throws Exception {

        System.setProperty(
                "webdriver.chrome.driver",
                CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(ops);

        driver.get(URL + "/editAssignment/1");
        Thread.sleep(SLEEP_DURATION);

        try{
            WebElement assignmentDate = driver.findElement(By.name("date"));
            WebElement message = driver.findElement(By.id("gmessage"));
            WebElement submit = driver.findElement(By.id("submit"));
            WebElement assignmentName = driver.findElement(By.name("name"));

            assignmentName.clear();
            assignmentName.sendKeys("Design");
            assignmentDate.clear();
            assignmentDate.sendKeys("2021-10-30");
            Thread.sleep(SLEEP_DURATION);

            submit.click();
            Thread.sleep(SLEEP_DURATION);

            assertEquals("Assignment saved. ", message.getText());

            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            List<WebElement> elements = driver.findElements(By.xpath("//td"));
            Thread.sleep(SLEEP_DURATION);

            boolean foundName = false;
            boolean foundDate = false;

            for (WebElement we : elements) {
                if (we.getText().equals("Design")) {
                    foundName=true;
                }

                if(we.getText().equals("2021-10-30")){
                    foundDate = true;
                    break;
                }
            }

            assertThat(foundName).withFailMessage("The test assignment Name was not found.").isTrue();
            assertThat(foundDate).withFailMessage("The test assignment Date was not found.").isTrue();
        } catch (Exception ex) {
            throw ex;
        } finally {
            driver.quit();
        }
    }

    @Test
    public void addAssignmentTest() throws Exception {

        System.setProperty(
                "webdriver.chrome.driver",
                CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(ops);

        driver.get(URL + "/addAssignment");
        Thread.sleep(SLEEP_DURATION);

        try{
            WebElement message = driver.findElement(By.id("gmessage"));
            WebElement submit = driver.findElement(By.id("submit"));
            WebElement course = driver.findElement(By.name("courseId"));
            WebElement date = driver.findElement(By.name("dueDate"));
            WebElement name = driver.findElement(By.name("assignmentName"));

            name.sendKeys("Networks");
            date.sendKeys("2021-10-30");
            course.sendKeys("31045");
            Thread.sleep(SLEEP_DURATION);

            submit.click();
            Thread.sleep(SLEEP_DURATION);

            assertEquals("Assignment Added ", message.getText());

            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            boolean foundName = false;
            boolean foundDate = false;
            boolean foundCourse = false;
            List<WebElement> elements2 = driver.findElements(By.xpath("//td"));
            for (WebElement we : elements2) {
                if (we.getText().equals("Networks")) {
                    foundName = true;
                }

                if(we.getText().equals("2021-10-30")){
                    foundDate = true;
                }

                if(we.getText().equals("CST 363 - Introduction to Database Systems")){
                    foundCourse = true;
                }
            }

            assertThat(foundName).withFailMessage("The test assignment Name was not found.").isTrue();
            assertThat(foundDate).withFailMessage("The test assignment Date was not found.").isTrue();
            assertThat(foundCourse).withFailMessage("The test course was not found.").isTrue();

        } catch (Exception ex) {
            throw ex;
        } finally {
            driver.quit();
        }
    }

    @Test
    public void deleteAssignmentTest() throws Exception {

        System.setProperty(
                "webdriver.chrome.driver",
                CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(ops);

        driver.get(URL);
        Thread.sleep(SLEEP_DURATION);

        try{
            List<WebElement> elements = driver.findElements(By.xpath("//td"));
            Thread.sleep(SLEEP_DURATION);

            boolean foundName = false;
            boolean foundDate = false;
            boolean foundCourse = false;

            for (WebElement we : elements) {
                if (we.getText().equals("Layers")) {
                    foundName = true;
                }

                if(we.getText().equals("CST 238 - Introduction to Networks")){
                    foundCourse = true;
                }

                if(we.getText().equals("2021-09-02")){
                    foundDate = true;
                }

            }

            assertThat(foundName).withFailMessage("The test assignment Name was not found.").isTrue();
            assertThat(foundDate).withFailMessage("The test assignment Date was not found.").isTrue();
            assertThat(foundCourse).withFailMessage("The test course was not found.").isTrue();

            driver.get(URL + "/deleteAssignment/6");
            Thread.sleep(SLEEP_DURATION);

            WebElement message = driver.findElement(By.id("gmessage"));
            WebElement NormalButton = driver.findElement(By.id("nDelete"));

            NormalButton.click();
            Thread.sleep(SLEEP_DURATION);

            assertEquals("Assignment Deleted. ", message.getText());

            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            List<WebElement> elements2 = driver.findElements(By.xpath("//td"));
            foundName = false;
            foundDate = false;
            foundCourse = false;
            for (WebElement we : elements2) {
                if (!(we.getText().equals("Layers"))) {
                    foundName=true;
                }

                if(!(we.getText().equals("2021-09-02"))){
                    foundDate = true;
                }

                if(!(we.getText().equals("CST 238 - Introduction to Networks"))){
                    foundCourse = true;
                    break;
                }
            }

            assertThat(foundName).withFailMessage("The test assignment Name was not found.").isTrue();
            assertThat(foundDate).withFailMessage("The test assignment Date was not found.").isTrue();
            assertThat(foundCourse).withFailMessage("The test course was not found.").isTrue();

        } catch (Exception ex) {
            throw ex;
        } finally {
            driver.quit();
        }
    }
}
