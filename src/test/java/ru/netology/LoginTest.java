package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.data.CodeGenerator;
import ru.netology.data.UserGenerator;
import ru.netology.page.AuthCodePage;
import ru.netology.page.LoginPage;

import java.nio.file.Paths;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

@Testcontainers
public class LoginTest {
    private final UserGenerator.User user = UserGenerator.Registration.generateUser("en");

    private static String dbUrl;// = "jdbc:mysql://localhost:3306/app"; //in case this needs to be run on localhost with docker-compose
    private static String appUrl;// = "localhost:9999"; //in case this needs to be run on localhost with docker-compose
    static Network network = Network.newNetwork();
    static String invalidCredential = UserGenerator.generateInvalidCredentials();

    @Rule
    public static MySQLContainer dbCont =
            new MySQLContainer("mysql:latest");
    @Rule
    public static GenericContainer appCont =
            new GenericContainer(new ImageFromDockerfile("app-deadline")
                    .withDockerfile(Paths.get("artifacts/deadline/Dockerfile")));

    @BeforeAll //disable this method if this needs to be run on localhost with docker-compose
    static void setUpContainersAndBrowser() {
        dbCont
                .withDatabaseName("app")
                .withUsername("app")
                .withPassword("pass")
                .withNetwork(network)
                .withNetworkAliases("mysql")
                .withFileSystemBind("./artifacts/init/schema.sql", "/docker-entrypoint-initdb.d/schema.sql", BindMode.READ_ONLY)
                .withExposedPorts(3306)
                .start();
        dbUrl = dbCont.getJdbcUrl();
        appCont
                .withEnv("TESTCONTAINERS_DB_USER", "app")
                .withEnv("TESTCONTAINERS_DB_PASS", "pass")
                .withExposedPorts(9999)
                .withNetwork(network)
                .withNetworkAliases("app-deadline")
                .withCommand("java -jar app-deadline.jar -P:jdbc.url=jdbc:mysql://mysql:3306/app")
                .start();
        appUrl = appCont.getHost() + ":" + appCont.getMappedPort(9999);
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
