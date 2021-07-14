package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AuthCodePage {
    private SelenideElement authCodeField = $("[data-test-id='code'] .input__control");
    private SelenideElement notification = $(".notification");
    private SelenideElement button = $(".button");

    public DashBoardPage inputValidAuthCode(String code) {
        authCodeField.setValue(code);
        button.click();
        return new DashBoardPage();
    }

    public void inputInvalidAuthCode(String code) {
        authCodeField.sendKeys(Keys.CONTROL + "A");
        authCodeField.sendKeys(Keys.BACK_SPACE);
        authCodeField.setValue(code);
        button.click();
    }

    public AuthCodePage() {
        authCodeField.shouldBe(visible);
    }

    public void assertMultipleInvalidCodeInputs() {
        notification.shouldHave(Condition.text("попыток ввода"));
    }
}
