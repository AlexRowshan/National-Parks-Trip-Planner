package edu.usc.csci310.project;

import io.cucumber.java.After;
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

public class MyStepdefsSuggestPage {
    private static final String ROOT_URL = "https://localhost:8080/";
    private WebDriver driver = new ChromeDriver();
    private WebDriverWait wait;

    public void setUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors", "--ignore-ssl-errors");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(500));
    }

    boolean storageSet = false;

    @After
    public void closeDriver() {
        driver.quit();
    }

    @Given("I am on the friend suggest page")
    public void iAmOnTheFriendSuggestPage() {driver.get(ROOT_URL + "suggest");}

    @Given("I have a database cleared and users created for suggest")
    public void iHaveADatabaseClearedAndUsersCreatedForSuggest() {
        setUp();
        Utils.clearDb();
        Utils.createUsers(driver, wait);
        Utils.populateFavorites();
    }

    @Given("I click the logout button for suggest")
    public void iClickTheLogoutButtonForSuggest() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("LogoutButton")));
        driver.findElement(By.className("LogoutButton")).click();
    }

    @Then("I should be on the login page for suggest")
    public void iShouldBeOnTheLoginPageForSuggest() {
        assertEquals(ROOT_URL, driver.getCurrentUrl());
    }

    @And("I should not be able to go back to suggest")
    public void iShouldNotBeAbleToGoBackToSuggest() {
        driver.get(ROOT_URL + "suggest");
        assertEquals(driver.getCurrentUrl(), ROOT_URL + "404");
    }

    @Given("I am on the suggest page using HTTP")
    public void iAmOnTheSuggestPageUsingHTTP() {
        driver.get("http://localhost:8080/suggest");
    }

    @Given("I am on the park suggestion page")
    public void iAmOnTheParkSuggestionPage() {
        driver.get(ROOT_URL + "suggest");
    }

    @Then("I should see a SSL error for suggest")
    public void iShouldSeeASSLErrorForSuggest() {
        assertTrue(driver.getPageSource().contains("Bad Request"));
    }

    @When("I click the Suggest button")
    public void iClickTheButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("suggest-button")));
        driver.findElement(By.className("suggest-button")).click();
    }

    @And("I entered {string} username in the search bar")
    public void iEnteredUsernameInTheSearchBar(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tags-input")));
        driver.findElement(By.className("tags-input")).sendKeys(arg0);
    }

    @Then("the {string} park is displayed")
    public void theParkIsDisplayed(String arg0) {
        // wait until park-header
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/div[3]/div[1]/h3")));
        assertEquals(driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[3]/div[1]/h3")).getText(), arg0);
    }

    @And("I am logged in as user {string} for suggest")
    public void iAmLoggedInAsUserForSuggest(String arg0) {
        driver.get(ROOT_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));
        driver.findElement(By.id("username-input")).sendKeys(arg0);
        driver.findElement(By.id("password-input")).sendKeys("Aa1");
        driver.findElement(By.className("login-button")).click();

        // Wait for the login process to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @And("I am on the suggest page")
    public void iAmOnTheSuggestPage() {
        driver.get(ROOT_URL + "suggest");
    }

    @And("I set session storage for {string} for suggest")
    public void iSetSessionStorageForForSuggest(String arg0) {
        if (!storageSet) {
            Utils.setSessionStorage(arg0, driver);
            storageSet = true;
        }
    }

    @And("I go to the suggest page")
    public void iGoToTheSuggestPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("SuggestLink")));
        driver.findElement(By.className("SuggestLink")).click();
    }

    @Then("I should get an error message that my friend has private visibility, preventing a suggestion")
    public void iShouldGetAnErrorMessageThatMyFriendHasPrivateVisibilityPreventingASuggestion() {
        // Wait for an element with the class name "alert" to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert")));

        // Get the text of the element with the class name "alert"
        String alertMessage = driver.findElement(By.className("alert")).getText();

        // Assert that the alert message matches the expected text
        assertEquals("The entered username belongs to a private user. Cannot compare parks.", alertMessage);
    }

    @When("I click the Add User button")
    public void iClickTheAddUserButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("add-user-button")));
        driver.findElement(By.className("add-user-button")).click();
    }

    @When("I click the {string} button in the nav bar for suggest")
    public void iClickTheButtonInTheNavBarForSuggest(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(arg0+"Link")));
        driver.findElement(By.className(arg0+"Link")).click();
    }


    @Then("I should be on the {string} page from suggest")
    public void iShouldBeOnThePageFromSuggest(String arg0) {
        assertTrue(driver.getCurrentUrl().contains(arg0.toLowerCase()));
    }

}
