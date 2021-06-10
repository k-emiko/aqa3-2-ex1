package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@Testcontainers
public class LoginTest {
    private SelenideElement heading = $("[data-test-id='dashboard'].heading");
    private UserGenerator.User user = UserGenerator.Registration.generateUser("en");

    @BeforeAll
    static void headless() {
        Configuration.headless = true;
    }

    @BeforeEach
    public void setUp() throws SQLException {
        open("http://localhost:9999");
        val runner = new QueryRunner();
        val dataSQL = "INSERT INTO users(login, password, id) VALUES (?, ?, ?);";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass");
        ) {
            runner.update(conn, dataSQL, user.getLogin(), user.getPasswordDb(), user.getId());
        }
    }


    @Test
    public void loginHappyPathTest() throws SQLException {
        LoginPage loginPage = new LoginPage();
        AuthCodePage authCodePage = loginPage.authorizeWithValidCredentials(user);
        String authCode;
        val runner = new QueryRunner();
        val idSQL = "SELECT id FROM users WHERE login=?;";
        val dataSQL = "SELECT code FROM auth_codes WHERE user_id=? AND created=(select max(created) from auth_codes)";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );

        ) {
            String userId = runner.query(conn, idSQL, new ScalarHandler<>(), user.getLogin());
            authCode = runner.query(conn, dataSQL, new ScalarHandler<>(), userId);
        }
        authCodePage.inputValidAuthCode(authCode);
        heading.shouldHave(Condition.exactText("Личный кабинет"));
    }

    @Test
    public void threeIncorrectPasswordInputs() {
        LoginPage loginPage = new LoginPage();
        AuthCodePage authCodePage = loginPage.authorizeWithValidCredentials(user);
        authCodePage.assertThreeInvalidCodeInputs();
    }
}
