package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.UserGenerator;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    SelenideElement loginField = $("[data-test-id='login'] .input__control");
    SelenideElement passwordField = $("[data-test-id='password'] .input__control");
    SelenideElement button = $(".button");
    private SelenideElement notification = $(".notification");

    public AuthCodePage authorizeWithValidCredentials(UserGenerator.User user) {
        loginField.setValue(user.getLogin());
        passwordField.setValue(user.getPasswordUi());
        button.click();
        return new AuthCodePage();
    }

    public void authorizeWithInvalidLogin(UserGenerator.User user) {
        loginField.setValue("foo");
        passwordField.setValue(user.getPasswordUi());
        button.click();
    }

    public void authorizeWithInvalidPassword(UserGenerator.User user) {
        loginField.setValue(user.getLogin());
        passwordField.setValue("foo");
        button.click();
    }

    public void authorizeWithInvalidCredentials() {
        loginField.setValue("foo");
        passwordField.setValue("foo");
        button.click();
    }

    public void assertInvalidLoginError() {
        notification.shouldHave(Condition.text("Неверно указан логин или паоль"));
    }
}
