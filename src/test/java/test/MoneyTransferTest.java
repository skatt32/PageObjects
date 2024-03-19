package test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.DashboardPage;
import page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static data.DataHelper.*;


public class MoneyTransferTest {
    DashboardPage dashboardPage;
    CardInfo firstCardInfo;
    CardInfo secondCardInfo;
    int firstCardBalance;
    int secondCardBalance;

    @BeforeEach
    void setUp() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(String.valueOf(verificationCode));
        firstCardInfo = getFirstCardInfo();
        secondCardInfo = getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));
        secondCardBalance = dashboardPage.getCardBalance(getMaskedNumber(secondCardInfo.getCardNumber()));
    }

    @Test
    void shouldTransferFromFirstToSecond() {
        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));
        var actualBalanceSecondCard = dashboardPage.getCardBalance(getMaskedNumber(secondCardInfo.getCardNumber()));
        assertAll(() -> assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard),
                () -> assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard));

    }
    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {

        var amount = generateInvalidAmount(secondCardBalance);
        var TransferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        TransferPage.makeTransfer(String.valueOf(amount),secondCardInfo);
        TransferPage.findErrorMessage("Выполнена попытка перевода суммыб превышающей остаток на карте списания");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));
        var actualBalanceSecondCard = dashboardPage.getCardBalance(getMaskedNumber(secondCardInfo.getCardNumber()));
        assertAll(() -> assertEquals(firstCardBalance, actualBalanceFirstCard),
                () -> assertEquals(secondCardBalance, actualBalanceSecondCard));
    }
}


