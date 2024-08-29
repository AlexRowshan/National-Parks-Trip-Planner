package edu.usc.csci310.project;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyStepdefsLogin {

    private static final String ROOT_URL = "https://localhost:8080/";
    private static WebDriver driver;
    private static WebDriverWait wait;
    private boolean isUserCreated = false;

    public static void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors", "--ignore-ssl-errors");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Given("I have a database cleared and users created for login")
    public void iHaveADatabaseClearedForLogin() {
        setUp();
        Utils.clearDb();
        Utils.createUsers(driver, wait);
    }

    @After
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @When("I enter the username {string}")
    public void iEnterTheUsername(String string) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/input[1]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/input[1]")).sendKeys(string);
    }

    @And("I enter the password {string}")
    public void iEnterThePassword(String string) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/input[2]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/input[2]")).sendKeys(string);
    }

    @And("I press the Login button")
    public void iPressTheLoginButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/div/button[1]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/div/button[1]")).click();
    }

    @Then("I should fail to login")
    public void iShouldFailToLogin() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
        assertTrue(driver.getPageSource().contains("UserEntity not found."));
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        driver.get(ROOT_URL);
    }

    @Then("I should fail to login with empty username message")
    public void iShouldFailToLoginWithEmptyUsernameMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
        assertTrue(driver.getPageSource().contains("Please fill in all fields."));
    }

    @Then("I should fail to login with empty password message")
    public void iShouldFailToLoginWithEmptyPasswordMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
        assertTrue(driver.getPageSource().contains("Please fill in all fields."));
    }

    @Then("I should fail see an incorrect password message")
    public void iShouldFailToLoginWithPassword() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
        assertTrue(driver.getPageSource().contains("Incorrect password"));
    }

    @When("I press Tab")
    public void iPressTab() {
        Actions tab = new Actions(driver);
        tab.sendKeys(Keys.TAB).perform();
    }

    @Then("I should be able to type in the username box")
    public void iShouldBeAbleToTypeInTheUsernameBox() {
        WebElement usernameInputBox = driver.findElement(By.id("username-input"));
        usernameInputBox.sendKeys("TestUsername321");
        wait.until(ExpectedConditions.attributeToBe(usernameInputBox, "value", "TestUsername321"));
        String testUsername = usernameInputBox.getAttribute("value");
        assertEquals(testUsername, "TestUsername321");
    }

    @Given("I created a user Tommy Trojan123 and I'm on the login page")
    public void iCreatedAUserTommyTrojan123AndIMOnTheLoginPage() {
        if (!isUserCreated) {
            driver.get(ROOT_URL+"create-user");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/input[1]")));
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/input[1]")).sendKeys("Tommy");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/input[2]")));
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/input[2]")).sendKeys("Trojan123");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/input[3]")));
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/input[3]")).sendKeys("Trojan123");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/form/div/button[1]")));
            driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/form/div/button[1]")).click();
            driver.get(ROOT_URL);
            isUserCreated = true;
        }
    }

    @Then("I should go to the search page from the login page")
    public void iShouldGoToTheSearchPageFromTheLoginPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("centered-container-parks")));
        assertEquals(ROOT_URL+"search", driver.getCurrentUrl());
    }

    @Then("I should see user not found message")
    public void iShouldSeeUserNotFoundMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
        assertTrue(driver.getPageSource().contains("UserEntity not found."));
    }

    @Given("I am on the login page using HTTP")
    public void iAmOnTheLoginPageUsingHTTP() {
        driver.get("http://localhost:8080/");
    }

    @Then("I should see a SSL error for login")
    public void iShouldSeeATLSSSLError() {
        assertTrue(driver.getPageSource().contains("Bad Request"));
    }

    @Then("I should fail and see an account locked out message")
    public void iShouldFailAndSeeAnAccountLockedOutMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
        assertTrue(driver.getPageSource().contains("Your account has been locked for 30 seconds due to multiple failed login attempts."));
    }

    @Then("I should fail and see a temporary lockout message")
    public void iShouldFailAndSeeATemporaryLockoutMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));
        assertTrue(driver.getPageSource().contains("Your account is temporarily locked due to multiple failed login attempts. Please try again later."));
    }

    @When("I wait {int} seconds")
    public void iWaitSeconds(int arg0) {
        try {
            Thread.sleep(arg0 * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Error waiting for " + arg0 + " seconds");
        }
    }

    @And("I confirm the message")
    public void iConfirmTheMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-confirm-button")));
        driver.findElement(By.className("modal-confirm-button")).click();
    }
}
