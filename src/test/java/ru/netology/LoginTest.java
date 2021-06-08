package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginTest {
    private SelenideElement heading = $("[data-test-id='dashboard'].heading");
    UserGenerator.User vasya = new UserGenerator.User("vasya", "qwerty123");
    UserGenerator.User user = UserGenerator.Registration.generateUser("ru");

    @BeforeEach
    public void setUp() throws SQLException {
        open("http://localhost:9999");
        val runner = new QueryRunner();
        val dataSQL = "INSERT INTO users(login, password) VALUES (?, ?);";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );

        ) {
            runner.update(conn, dataSQL, user.getLogin(), user.getPassword());
        }
    }

    @Test
    public void loginHappyPathTest() {
        LoginPage loginPage = new LoginPage();
        loginPage.authorizeWithValidCredentials(user);




        //heading.shouldHave(Condition.exactText("Личный кабинет"));
    }

    @Test
    public void threeIncorrectPasswordInputs(){

    }
}
