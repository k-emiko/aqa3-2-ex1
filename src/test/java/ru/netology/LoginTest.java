package ru.netology;

import com.codeborne.selenide.Configuration;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
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
import ru.netology.data.UserGenerator;
import ru.netology.page.AuthCodePage;
import ru.netology.page.LoginPage;

import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

@Testcontainers
public class LoginTest {
    private final UserGenerator.User user = UserGenerator.Registration.generateUser("en");

    private static String dbUrl;
    private static String appUrl;
    static Network network = Network.newNetwork();

    @Rule
    public static MySQLContainer dbCont =
            (MySQLContainer) new MySQLContainer("mysql:latest")
                    .withDatabaseName("app")
                    .withUsername("app")
                    .withPassword("pass")
                    .withNetwork(network)
                    .withNetworkAliases("mysql")
                    .withFileSystemBind("./artifacts/init/schema.sql", "/docker-entrypoint-initdb.d/schema.sql", BindMode.READ_ONLY)
                    .withExposedPorts(3306);
    @Rule
    public static GenericContainer appCont =
            new GenericContainer(new ImageFromDockerfile("app-deadline")
                    .withDockerfile(Paths.get("artifacts/deadline/Dockerfile")))
                    .withEnv("TESTCONTAINERS_DB_USER", "app")
                    .withEnv("TESTCONTAINERS_DB_PASS", "pass")
                    .withExposedPorts(9999)
                    .withNetwork(network)
                    .withNetworkAliases("app-deadline");

    @BeforeAll
    static void headless() {
        dbCont.start();
        dbUrl = dbCont.getJdbcUrl();
        appCont
                .withCommand("java -jar app-deadline.jar -P:jdbc.url=jdbc:mysql://mysql:3306/app")
                .start();
        appUrl = appCont.getHost() + ":" + appCont.getMappedPort(9999);
        Configuration.headless = true;
    }

    @BeforeEach
    public void setUp() throws SQLException {
        open("http://" + appUrl);
        val runner = new QueryRunner();
        val dataSQL = "INSERT INTO users(login, password, id) VALUES (?, ?, ?);";
        try (
                val conn = DriverManager.getConnection(
                        dbUrl, "app", "pass")
        ) {
            runner.update(conn, dataSQL, user.getLogin(), user.getPasswordDb(), user.getId());
        }
    }

    @AfterEach
    public void cleanUp() throws SQLException {
        val runner = new QueryRunner();
        val dataSQL = "DElETE FROM users;";
        try (
                val conn = DriverManager.getConnection(
                        dbUrl, "app", "pass")
        ) {
            runner.execute(conn, "SET FOREIGN_KEY_CHECKS = 0;");
            runner.update(conn, dataSQL);
            runner.execute(conn, "SET FOREIGN_KEY_CHECKS = 1;");
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
                        dbUrl, "app", "pass"
                )

        ) {
            String userId = runner.query(conn, idSQL, new ScalarHandler<>(), user.getLogin());
            authCode = runner.query(conn, dataSQL, new ScalarHandler<>(), userId);
        }
        authCodePage.inputValidAuthCode(authCode);
    }

    @Test
    public void loginInvalidLoginTest() {
        LoginPage loginPage = new LoginPage();
        loginPage.authorizeWithInvalidLogin(user);
        loginPage.assertInvalidLoginError();
    }

    @Test
    public void loginInvalidLoginPassword() {
        LoginPage loginPage = new LoginPage();
        loginPage.authorizeWithInvalidPassword(user);
        loginPage.assertInvalidLoginError();
    }

    @Test
    public void loginInvalidCredentialsTest() {
        LoginPage loginPage = new LoginPage();
        loginPage.authorizeWithInvalidCredentials();
        loginPage.assertInvalidLoginError();
    }

    @Test
    public void threeIncorrectPasswordInputs() {
        LoginPage loginPage = new LoginPage();
        AuthCodePage authCodePage = loginPage.authorizeWithValidCredentials(user);
        authCodePage.assertThreeInvalidCodeInputs();
    }
}
