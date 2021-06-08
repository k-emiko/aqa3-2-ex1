package ru.netology;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    SelenideElement loginField = $("[data-test-id='login'] .input__control");
    SelenideElement passwordField = $("[data-test-id='password'] .input__control");
    SelenideElement button = $(".button");

    public AuthCodePage authorizeWithValidCredentials(UserGenerator.User user) {
        loginField.setValue(user.getLogin());
        passwordField.setValue(user.getPassword());
        button.click();
        return new AuthCodePage();
    }

    public void authorizeWithInvalidPassword(UserGenerator.User user) {
        loginField.setValue(user.getLogin());
        passwordField.setValue("foo");
        button.click();
    }
}
