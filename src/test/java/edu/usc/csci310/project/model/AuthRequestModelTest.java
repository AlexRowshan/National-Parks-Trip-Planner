package edu.usc.csci310.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthRequestModelTest {

    @Test
    void testDefaultConstructor() {
        AuthRequestModel authRequestModel = new AuthRequestModel();
        assertNotNull(authRequestModel);
    }

    @Test
    void testParameterizedConstructor() {
        String userName = "testUser";
        String password = "testPassword";
        AuthRequestModel authRequestModel = new AuthRequestModel(userName, password);

        assertNotNull(authRequestModel);
        assertEquals(userName, authRequestModel.getUserName());
        assertEquals(password, authRequestModel.getPassword());
    }

    @Test
    void testGetUserName() {
        String userName = "testUser";
        AuthRequestModel authRequestModel = new AuthRequestModel(userName, "testPassword");

        String result = authRequestModel.getUserName();

        assertEquals(userName, result);
    }

    @Test
    void testSetUserName() {
        String userName = "testUser";
        AuthRequestModel authRequestModel = new AuthRequestModel();

        authRequestModel.setUserName(userName);

        assertEquals(userName, authRequestModel.getUserName());
    }

    @Test
    void testGetPassword() {
        String password = "testPassword";
        AuthRequestModel authRequestModel = new AuthRequestModel("testUser", password);

        String result = authRequestModel.getPassword();

        assertEquals(password, result);
    }

    @Test
    void testSetPassword() {
        String password = "testPassword";
        AuthRequestModel authRequestModel = new AuthRequestModel();

        authRequestModel.setPassword(password);

        assertEquals(password, authRequestModel.getPassword());
    }
}