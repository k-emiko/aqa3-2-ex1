package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.data.CodeGenerator;
import ru.netology.data.UserGenerator;
import ru.netology.page.AuthCodePage;
import ru.netology.page.LoginPage;

import java.io.File;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

@Testcontainers
public class LoginTest {
    private final UserGenerator.User user = UserGenerator.Registration.generateUser("en");

    private static String dbUrl;// = "jdbc:mysql://localhost:3306/app"; //in case this needs to be run on localhost with docker-compose
    private static String appUrl;// = "localhost:9999"; //in case this needs to be run on localhost with docker-compose
    static String invalidCredential = UserGenerator.generateInvalidCredentials();

    @Rule
    public static DockerComposeContainer dcContainer = new DockerComposeContainer(new File("artifacts/docker-compose.yml"))
            .withExposedService("mysql", 3306)
            .withExposedService("app-deadline", 9999);

    @BeforeAll //disable this method if this needs to be run on localhost with docker-compose
    static void setUpContainersAndBrowser() {
        dcContainer.start();
        dbUrl = "jdbc:mysql://" + dcContainer.getServiceHost("mysql", 3306) + ":" + dcContainer.getServicePort("mysql", 3306) + "/app";
        appUrl = dcContainer.getServiceHost("app-deadline", 9999) + ":" + dcContainer.getServicePort("app-deadline", 9999);
        Configuration.headless = true;
    }

    @BeforeEach
    public void setUp() throws SQLException {
        open("http://" + appUrl);
        DBHelper.setUp(dbUrl, user);
    }

    @AfterEach
    public void cleanUp() throws SQLException {
        DBHelper.cleanUp(dbUrl);
    }

    @Test
    public void loginHappyPathTest() throws SQLException {
        LoginPage loginPage = new LoginPage();
        loginPage.login(user.getLogin(), user.getPasswordUi());
        AuthCodePage authCodePage = new AuthCodePage();
        String authCode = DBHelper.getCode(dbUrl, user);
        authCodePage.inputValidAuthCode(authCode);
    }

    @Test
    public void loginInvalidLoginTest() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(invalidCredential, user.getPasswordUi());
        loginPage.assertInvalidLoginError();
    }

    @Test
    public void loginInvalidLoginPassword() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(user.getLogin(), invalidCredential);
        loginPage.assertInvalidLoginError();
    }

    @Test
    public void loginInvalidCredentialsTest() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(invalidCredential, invalidCredential);
        loginPage.assertInvalidLoginError();
    }

    @Test
    public void blockAfterThreeIncorrectInputs() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(user.getLogin(), user.getPasswordUi());
        AuthCodePage authCodePage = new AuthCodePage();
        for (int i = 0; i < 4; i++) {
            authCodePage.inputInvalidAuthCode(CodeGenerator.generateInvalidCode());
        }
        authCodePage.assertMultipleInvalidCodeInputs();
    }

}
