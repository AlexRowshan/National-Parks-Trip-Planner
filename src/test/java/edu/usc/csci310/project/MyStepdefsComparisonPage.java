package edu.usc.csci310.project;

import ch.qos.logback.core.net.SyslogOutputStream;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class MyStepdefsComparisonPage {
    private static final String ROOT_URL = "https://localhost:8080/";
    private WebDriver driver;
    private WebDriverWait wait;
    boolean storageSet = false;

    public void setUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors", "--ignore-ssl-errors");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(500));
    }
    @Given("I have a database cleared and users created for compare")
    public void iHaveADatabaseClearedAndUsersCreatedForCompare() {
        setUp();
        Utils.clearDb();
        Utils.createCompareUsers(driver, wait);
        Utils.populateCompare();
    }

    @After
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("I am logged in as user {string}")
    public void iAmLoggedInAsUser(String name) {
        driver.get(ROOT_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));
        driver.findElement(By.id("username-input")).sendKeys(name);
        driver.findElement(By.id("password-input")).sendKeys("Aa1");
        driver.findElement(By.className("login-button")).click();
        

        // Wait for the login process to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @And("I go to the compare page")
    public void iGoToTheComparePage() {
        System.out.println(" I am here");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[1]/div[2]/a[4]")));
        driver.findElement(By.xpath("/html/body/div/div/div/div[1]/div[2]/a[4]")).click();

    }

    @And("I add the user {string}")
    public void iAddTheUser(String name) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/input")));
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/input")).sendKeys(name);
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/button")).click();
        wait.until(ExpectedConditions.textToBe(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/div/span[1]"), name));

    }
    @When("I click compare")
    public void iClickCompare() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/button")));
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/button")).click();;

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("I should see {string} for the ratio of the first park card")
    public void iShouldSeeForTheRatioOfTheFirstPark(String expectedRatio) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/h2/div/span[1]")));
        String actualRatio = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/h2/div/span[1]")).getText();
        assertEquals(expectedRatio, actualRatio);
    }

    @When("I hover my mouse over the ratio")
    public void iHoverMyMouseOverTheRatio() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/h2/div/span[1]")));
        WebElement ratioElement = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/h2/div/span[1]"));
        Actions actions = new Actions(driver);
        actions.moveToElement(ratioElement).perform();

        // Wait for 1 second
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("I should see {string} as a pop up")
    public void iShouldSeeAsAPopUp(String expectedText) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/h2/div/span[2]")));
        String actualText = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/h2/div/span[2]")).getText();
        assertEquals(expectedText, actualText);
    }

    @When("I click the first park name")
    public void iClickTheFirstParkName() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/h2")));
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/h2")).click();

        // Wait for the park information to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("The park information should render")
    public void theParkInformationShouldRender() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/div/p[3]")));
        String description = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[2]/div/div/div/p[3]")).getText();
        assertTrue(description.contains("Description:"));
    }

    @Then("I should see an alert {string}")
    public void iShouldSeeAnAlert(String expectedAlertText) {
        System.out.println("Alert HERE");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String actualAlertText = alert.getText();
            assertEquals(expectedAlertText, actualAlertText);
            alert.accept();
        } catch (NoAlertPresentException e) {
            fail("No alert present");
        }
    }

    @And("I add the private user {string}")
    public void iAddThePrivateUser(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/input")));
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/input")).sendKeys(name);
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/button")).click();
    }

    @And("I add the unregistered user {string}")
    public void iAddTheUnregisteredUser(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/input")));
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/input")).sendKeys(name);
        driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div[1]/div/button")).click();
    }

    @Then("I should see {int} park cards")
    public void iShouldSeeParkCards(int expectedCount) {
        for (int i = 1; i <= expectedCount; i++) {
            String parkCardXPath = "/html/body/div/div/div/div[2]/div[2]/div/div[" + i + "]";

            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(parkCardXPath)));
            } catch (TimeoutException e) {
                fail("Park card with XPath " + parkCardXPath + " is not present");
            }
        }
    }


    @And("I click {string} on the navbar")
    public void iClickOnTheNavbar(String name) {
        String linkClass = name + "Link";
        try {
            WebElement navLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(linkClass)));
            navLink.click();
        } catch (TimeoutException e) {
            fail("Navigation link with class '" + linkClass + "' is not present or visible");
        }
    }

    @Then("I should be on the {string} page from compare")
    public void iShouldBeOnThePageFromCompare(String arg0) {
        assertTrue(driver.getCurrentUrl().contains(arg0.toLowerCase()));
    }

    @And("I click the Logout button for compare")
    public void iClickTheLogoutButtonForCompare() {

        System.out.println("Top top");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("LogoutButton")));
        driver.findElement(By.className("LogoutButton")).click();
        System.out.println("bot bot");


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @And("I should not be able to go back to the compare page")
    public void iShouldNotBeAbleToGoBackToTheComparePage() {
        driver.get(ROOT_URL + "compare");
        assertEquals(driver.getCurrentUrl(), ROOT_URL + "404");
    }

    @Then("I should be on the login page for compare")
    public void iShouldBeOnTheLoginPageForCompare() {
        assertEquals(ROOT_URL, driver.getCurrentUrl());

    }

}
//
//    @And("I have favorited {string} and {string} park with {string} as my top favorite")
//    public void iHaveFavoritedParks(String park1, String park2, String topFavorite) {
//    }
//
//    @And("my friend has favorited {string} and {string} with {string} as their top favorite and has public visibility")
//    public void myFriendHasFavoritedParks(String park1, String park2, String topFavorite) {
//    }
//
//    @And("I have entered my friend's username in the search bar")
//    public void iHaveEnteredMyFriendsUsernameInTheSearchBar() {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-box")));
//        WebElement searchBox = driver.findElement(By.className("search-box"));
//        searchBox.sendKeys("friend_username");
//    }
//
//    @When("I click the {string} button")
//    public void iClickTheButton(String buttonText) {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(), '" + buttonText + "')]")));
//        WebElement button = driver.findElement(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
//        button.click();
//    }
//
//    @Then("the parks should be listed starting with {string} as it is the best match based on being everyone's list and having the highest average rank, followed by {string} and {string} in any order")
//    public void theParksShouldBeListedInOrder(String bestMatch, String park1, String park2) {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-list")));
//        List<WebElement> parkList = driver.findElements(By.className("park-card"));
//        assertEquals(bestMatch, parkList.get(0).findElement(By.tagName("h2")).getText());
//        assertTrue(parkList.get(1).findElement(By.tagName("h2")).getText().equals(park1) ||
//                parkList.get(1).findElement(By.tagName("h2")).getText().equals(park2));
//        assertTrue(parkList.get(2).findElement(By.tagName("h2")).getText().equals(park1) ||
//                parkList.get(2).findElement(By.tagName("h2")).getText().equals(park2));
//    }
//
//    @And("my friend has private visibility")
//    public void myFriendHasPrivateVisibility() {
//    }
//
//    @Then("I should get an error message that at least one person has private visibility, preventing a best match comparison")
//    public void iShouldGetAnErrorMessageAboutPrivateVisibility() {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
//        WebElement errorMessage = driver.findElement(By.className("error-message"));
//        assertTrue(errorMessage.getText().contains("at least one person has private visibility"));
//    }
//
//    @Then("I should get a user not found error")
//    public void iShouldGetAUserNotFoundError() {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
//        WebElement errorMessage = driver.findElement(By.className("error-message"));
//        assertTrue(errorMessage.getText().contains("user not found"));
//    }
//
//    @And("my friend has public visibility but no favorites")
//    public void myFriendHasPublicVisibilityButNoFavorites() {
//    }
//
//    @Then("I should get an invalid comparison error as no best match can be determined")
//    public void iShouldGetAnInvalidComparisonError() {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
//        WebElement errorMessage = driver.findElement(By.className("error-message"));
//        assertTrue(errorMessage.getText().contains("invalid comparison"));
//    }
//
//    @And("my first friend with public visibility has favorited {string} and {string} with {string} as their top favorite")
//    public void myFirstFriendHasFavoritedParks(String park1, String park2, String topFavorite) {
//    }
//
//    @And("my second friend with public visibility has favorited {string} and {string} with {string} as their top favorite")
//    public void mySecondFriendHasFavoritedParks(String park1, String park2, String topFavorite) {
//    }
//
//    @And("I have entered both my friends' names in the search bar")
//    public void iHaveEnteredBothMyFriendsNamesInTheSearchBar() {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-box")));
//        WebElement searchBox = driver.findElement(By.className("search-box"));
//        searchBox.sendKeys("friend1_username, friend2_username");
//    }
//
//    @Then("the parks should be listed starting with {string} as the best match based on being on everyone's list and having the highest average rank, followed by the other parks in any order based on remaining preferences")
//    public void theParksShouldBeListedStartingWithAsTheBestMatch(String bestMatch) {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-list")));
//        List<WebElement> parkList = driver.findElements(By.className("park-card"));
//        assertEquals(bestMatch, parkList.get(0).findElement(By.tagName("h2")).getText());
//    }
//
//    @And("I have entered a name that doesn’t exist in the search bar")
//    public void iHaveEnteredANameThatDoesnTExistInTheSearchBar() {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-box")));
//        WebElement searchBox = driver.findElement(By.className("search-box"));
//        searchBox.sendKeys("nonexistent_user");
//    }
//
//    @And("I have favorited {string}")
//    public void iHaveFavorited(String arg0) {
//
//    }
//
//    @And("I have entered my friend’s name in the search bar")
//    public void iHaveEnteredMyFriendSNameInTheSearchBar() {
//    }
//
//    @And("I have favorited {string} as my top favorite")
//    public void iHaveFavoritedAsMyTopFavorite(String arg0) {
//
//    }
//
//    @Then("{string} should be listed first as the best match based on being on everyone’s list and having the highest average rank, followed by {string} and {string} in any order")
//    public void shouldBeListedFirstAsTheBestMatchBasedOnBeingOnEveryoneSListAndHavingTheHighestAverageRankFollowedByAndInAnyOrder(String arg0, String arg1, String arg2) {
//
//    }
//
//    @And("I have favorited {string}, {string}, and {string} with {string} as my top favorite")
//    public void iHaveFavoritedAndWithAsMyTopFavorite(String arg0, String arg1, String arg2, String arg3) {
//    }
//
//    @And("I have public visibility")
//    public void iHavePublicVisibility() {
//    }
