package test;

import lombok.val;
import org.junit.jupiter.api.*;
import data.DataHelper;
import page.DashboardPage;
import page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static data.DataHelper.*;


public class MoneyTransferTest {
    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    void shouldTransferFromCardToCardPositiveTest() {
        val LoginPage = new LoginPage();
        val authInfo = getAuthInfo();
        val verificationPage = LoginPage.validLogin(authInfo);
        val verificationCode = getVerificationCodeFor(authInfo);
        val dashboardPage = verificationPage.validVerify(verificationCode);
        val firstCardBalance = dashboardPage.getCardBalance(getFirstCardNumber().getCardNumber());
        val secondCardBalance = dashboardPage.getCardBalance(getSecondCardNumber().getCardNumber());
        val transferPage = dashboardPage.depositToFirstCard();
        int amount = 1_000;
        transferPage.transferMoney(amount, getSecondCardNumber());
        val expectedFirstCardBalanceAfter = firstCardBalance + amount;
        val expectedSecondCardBalanceAfter = secondCardBalance - amount;
        Assertions.assertEquals(expectedFirstCardBalanceAfter, dashboardPage.getCardBalance(getFirstCardNumber().getCardNumber()));
        Assertions.assertEquals(expectedSecondCardBalanceAfter, dashboardPage.getCardBalance(getSecondCardNumber().getCardNumber()));

    }

    @Test
    void shouldZeroSumTransferNegativeTest() {
        val LoginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = LoginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        val dashboardPage = verificationPage.validVerify(verificationCode);
        val transferPage = dashboardPage.depositToSecondCard();
        int amount = 0;
        transferPage.transferMoney(amount, DataHelper.getFirstCardNumber());
        transferPage.emptyAmountField();

    }

    @Test
    void shouldAmountMoreBalanceIsNegativeTest() {
        val LoginPage = new LoginPage();
        val authInfo = DataHelper.getAuthInfo();
        val verificationPage = LoginPage.validLogin(authInfo);
        val verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        val dashboardPage = verificationPage.validVerify(verificationCode);
        val secondCardBalance = dashboardPage.getCardBalance(getSecondCardNumber().getCardNumber());
        val transferPage = dashboardPage.depositToFirstCard();
        int amount = DashboardPage.formatWithoutMinusIssue(secondCardBalance);
        transferPage.transferMoney(amount, DataHelper.getSecondCardNumber());
        transferPage.amountMoreThanBalance();

    }

    @Test
    void shouldTransferFromCardToSameCardNegativeTest() {
        val LoginPage = new LoginPage();
        val authInfo = getAuthInfo();
        val verificationPage = LoginPage.validLogin(authInfo);
        val verificationCode = getVerificationCodeFor(authInfo);
        val dashboardPage = verificationPage.validVerify(verificationCode);
        val transferPage = dashboardPage.depositToFirstCard();
        int amount = 1_000;
        transferPage.transferMoney(amount, getFirstCardNumber());
        transferPage.enterAnotherCard();

    }
}