package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DashBoardPage {

    private final SelenideElement heading = $("[data-test-id='dashboard'].heading");

    public DashBoardPage() {
        heading.shouldHave(Condition.exactText("Личный кабинет"));
    }
}
