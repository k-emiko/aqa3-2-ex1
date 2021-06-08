package ru.netology;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.visible;

public class AuthCodePage {
    SelenideElement authCodeField = $("[data-test-id='code'] .input__control");

//    public void inputValidAuthCode(UserGenerator.User user) { //todo think how to best design the getting of auth code
//        authCodeField.setValue(user.getAuth_code());
//    }
    public void inputInvalidAuthCode(UserGenerator.User user) {
        authCodeField.setValue("foo");
    }

    public AuthCodePage() {
        authCodeField.shouldBe(visible);
    }
}
