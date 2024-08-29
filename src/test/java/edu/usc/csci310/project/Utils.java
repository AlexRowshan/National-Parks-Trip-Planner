package edu.usc.csci310.project;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class Utils {
    private static final String DB_URL = "jdbc:sqlite:camp.db";

    private static final String ROOT_URL = "https://localhost:8080/";

    public static void clearDb() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement deletePstmt = conn.prepareStatement("DELETE FROM user_entity")) {
            deletePstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear and setup the database", e);
        }
    }

    private static String getJwtTokenFromServer(String username) throws IOException {
        // Create a trust strategy that accepts all certificates
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;

        // Create an SSLContext that trusts all certificates
        SSLContext sslContext;
        try {
            sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (Exception e) {
            throw new IOException("Failed to create SSL context", e);
        }

        // Create an SSL socket factory with the all-trusting SSLContext
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        // Create an HttpClient that uses the all-trusting SSL socket factory
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build()) {
            HttpPost request = new HttpPost(ROOT_URL + "api/login");

            // Create the request body as a JSON object
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", username);
            requestBody.put("password", "Aa1");

            // Set the request body as a StringEntity
            StringEntity requestEntity = new StringEntity(requestBody.toString(), ContentType.APPLICATION_JSON);
            request.setEntity(requestEntity);

            // Send the request and retrieve the response
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            }
        } catch (JSONException e) {
            System.out.println("Unable to get JWT token by an extra login");
            System.out.println("Error: " + e);
        }
        return null;
    }

    public static void setSessionStorage(String username, WebDriver driver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        String encodedUsername = Base64.getEncoder().encodeToString(username.getBytes());
        jsExecutor.executeScript("window.sessionStorage.setItem('username', arguments[0]);", encodedUsername);
        String jwtToken;
        try {
            jwtToken = getJwtTokenFromServer(username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("JWT Token: " + jwtToken);
        jsExecutor.executeScript("window.sessionStorage.setItem('token', arguments[0]);", jwtToken);
    }

    public static void createUser(WebDriver driver, WebDriverWait wait, String username, String password) {
        driver.get(ROOT_URL + "create-user");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-box")));
        driver.findElement(By.id("username-box")).sendKeys(username);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password-box")));
        driver.findElement(By.id("password-box")).sendKeys(password);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirm-password-box")));
        driver.findElement(By.id("confirm-password-box")).sendKeys(password);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("create-account-button")));
        driver.findElement(By.className("create-account-button")).click();
    }

    public static void createUsers(WebDriver driver, WebDriverWait wait) {
        Utils.createUser(driver, wait, "a", "Aa1");
        Utils.createUser(driver, wait, "b", "Aa1");
        Utils.createUser(driver, wait, "c", "Aa1");
        Utils.createUser(driver, wait, "d", "Aa1");
    }

    public static void createCompareUsers(WebDriver driver, WebDriverWait wait) {
        Utils.createUser(driver, wait, "Person1", "Aa1");
        Utils.createUser(driver, wait, "Person2", "Aa1");
        Utils.createUser(driver, wait, "Person3", "Aa1");
        Utils.createUser(driver, wait, "Person4", "Aa1");
        Utils.createUser(driver, wait, "d", "Aa1");
    }

    public static void populateFavorites() {
        String encodedA = Base64.getEncoder().encodeToString("a".getBytes());
        String encodedB = Base64.getEncoder().encodeToString("b".getBytes());
        String encodedC = Base64.getEncoder().encodeToString("c".getBytes());
        String encodedD = Base64.getEncoder().encodeToString("d".getBytes());

        System.out.println("Encoded D: " + encodedD);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement aFavStatement = conn.prepareStatement("UPDATE user_entity SET favorites = '1yose,2jomu,3abli' WHERE username = '" + encodedA + "';");
             PreparedStatement bFavStatement = conn.prepareStatement("UPDATE user_entity SET favorites = '1yose,2jomu' WHERE username = '" + encodedB + "';");
             PreparedStatement cFavStatement = conn.prepareStatement("UPDATE user_entity SET favorites = '1yose,2jomu,3abli,4acad,5alca,6alpo,7badl,8band,9beol,10bibe,11bica,12blri,13blrv,14brcr,15cari,16casa,17cbpo,18chat,19choh,20crla,21cuva' WHERE username = '" + encodedC + "';")) {
            aFavStatement.executeUpdate();
            bFavStatement.executeUpdate();
            cFavStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add the favorites in the database", e);
        }
    }

    public static void populateCompare() {
        String encodedA = Base64.getEncoder().encodeToString("Person1".getBytes());
        String encodedB = Base64.getEncoder().encodeToString("Person2".getBytes());
        String encodedC = Base64.getEncoder().encodeToString("Person3".getBytes());
        String encodedE = Base64.getEncoder().encodeToString("Person4".getBytes());
        String encodedD = Base64.getEncoder().encodeToString("d".getBytes());
        System.out.println("Encoded D: " + encodedD);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement aFavStatement = conn.prepareStatement("UPDATE user_entity SET favorites = '1yose,2jomu,3abli,4acad', is_private = 0 WHERE username = '" + encodedA + "';");
             PreparedStatement bFavStatement = conn.prepareStatement("UPDATE user_entity SET favorites = '1yose,2jomu,3abli,4alca', is_private = 0 WHERE username = '" + encodedB + "';");
             PreparedStatement cFavStatement = conn.prepareStatement("UPDATE user_entity SET favorites = '1yose,2jomu,3abli,4alca', is_private = 1 WHERE username = '" + encodedC + "';");
             PreparedStatement eFavStatement = conn.prepareStatement("UPDATE user_entity SET favorites = '', is_private = 0 WHERE username = '" + encodedE + "';")){

            aFavStatement.executeUpdate();
            bFavStatement.executeUpdate();
            cFavStatement.executeUpdate();
            eFavStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add the favorites for comparison in the database", e);
        }
    }
}


