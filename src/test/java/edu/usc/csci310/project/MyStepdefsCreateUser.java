package edu.usc.csci310.project;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyStepdefsCreateUser {
    private static final String ROOT_URL = "https://localhost:8080/";
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @After
    public void closeDriver() {
        if (driver != null){
            driver.quit();
        }
    }

    @Given("I am on the create user page")
    public void iAmOnTheCreateUserPage() {
        driver.get(ROOT_URL+"create-user");
    }

    @And("I put username {string} in Username box")
    public void iPutUsernameInUsernameBox(String string) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-box")));
        driver.findElement(By.id("username-box")).sendKeys(string);
    }

    @And("I put password {string} in Password box")
    public void iPutPasswordInPasswordBox(String string) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password-box")));
        driver.findElement(By.id("password-box")).sendKeys(string);
    }

    @And("I put password {string} in Confirm Password box")
    public void iPutPasswordInConfirmPasswordBox(String string) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirm-password-box")));
        driver.findElement(By.id("confirm-password-box")).sendKeys(string);
    }

    @When("I click Create User button")
    public void iClickCreateUserButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("create-account-button")));
        driver.findElement(By.className("create-account-button")).click();
    }

    @When("I click Cancel button")
    public void iClickCancelButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cancel-button")));
        driver.findElement(By.className("cancel-button")).click();
    }

    @And("I confirm Yes")
    public void iConfirmYes() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/div/div/div/button[1]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div/button[1]")).click();
    }

    @And("I say No")
    public void iSayNo() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/div/div/div/button[2]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div/div/div/button[2]")).click();
    }

    @Then("I should go to back to the login page")
    public void iShouldGoToBackToTheLoginPage() {
        String currentUrl = driver.getCurrentUrl();
        assertEquals(ROOT_URL, currentUrl);
    }

    @Then("I should stay on the create user page")
    public void iShouldStayOnTheCreateUserPage() {
        String currentUrl = driver.getCurrentUrl();
        assertEquals(ROOT_URL+"create-user", currentUrl);
    }

    @And("I go to the create user page")
    public void iGoToTheCreateUserPage() {
        driver.get(ROOT_URL+"create-user");
    }

    @Then("I should an error message for taken username")
    public void iShouldAnErrorMessageForTakenUsername() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(driver.getPageSource().contains("Username taken"));
    }

    @Then("I should see an error message for wrong password")
    public void iShouldSeeAnErrorMessageForWrongPassword() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(driver.getPageSource().contains("Passwords do not match."));
    }

    @Given("I am on the initial login page")
    public void iAmOnTheInitialLoginPage() {
        driver.get(ROOT_URL);
    }

    @And("I click the Create account button")
    public void iClickTheCreateAccountButton() {
        driver.findElement(By.xpath("/html/body/div/div/div/form/div/button[2]")).click();
    }

    @Then("I should see an error message for invalid password format")
    public void iShouldSeeAnErrorMessageForInvalidPasswordFormat() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertTrue(driver.getPageSource().contains("Password must contain at least one uppercase letter, one lowercase letter, and one number."));
    }

    @And("I put an invalid password {string} in Password box")
    public void iPutAnInvalidPasswordInPasswordBox(String string) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/input[2]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/input[2]")).sendKeys(string);
    }

    @And("I put the same invalid password {string} in Confirm Password box")
    public void iPutTheSameInvalidPasswordInConfirmPasswordBox(String string) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/input[3]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/input[3]")).sendKeys(string);
    }

    @Then("I should be on the search page from the create user page")
    public void iShouldBeOnTheSearchPageFromTheCreateUserPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-box")));
        String currentUrl = driver.getCurrentUrl();
        assertEquals(ROOT_URL+"search", currentUrl);
    }

    @Given("I have a database cleared for create user")
    public void iHaveADatabaseClearedForCreateUser() {
        Utils.clearDb();
    }

    @Given("I am on the create user page using HTTP")
    public void iAmOnTheCreateUserPageUsingHTTP() {
        driver.get("http://localhost:8080/create-user");
    }

    @Then("I should see a SSL error for create user")
    public void iShouldSeeASSLErrorForCreateUser() {
        assertTrue(driver.getPageSource().contains("Bad Request"));
    }
}
