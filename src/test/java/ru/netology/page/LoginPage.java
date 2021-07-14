package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    SelenideElement loginField = $("[data-test-id='login'] .input__control");
    SelenideElement passwordField = $("[data-test-id='password'] .input__control");
    SelenideElement button = $(".button");
    private SelenideElement notification = $(".notification .notification__content");

    public void login(String login, String password) {
        loginField.setValue(login);
        passwordField.setValue(password);
        button.click();
    }

    public void assertInvalidLoginError() {
        notification.shouldHave(Condition.text("Неверно указан логин или пароль"));
    }
}
