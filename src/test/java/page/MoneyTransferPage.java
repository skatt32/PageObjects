package page;

import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static java.lang.String.valueOf;

public class MoneyTransferPage {
    private SelenideElement transferAmount = $("[data-test-id='amount'] .input__control");
    private SelenideElement fromField = $("[data-test-id='from'] .input__control");
    private SelenideElement transferButton = $("[data-test-id='action-transfer']");


    public DashboardPage transferMoney(int amount, DataHelper.CardInfo from) {
        transferAmount.setValue(valueOf(amount));
        fromField.setValue(valueOf(from));
        transferButton.click();
        return new DashboardPage();
    }

    public void emptyAmountField() {
        $(byText("Сумма перевода должна быть больше нуля.")).shouldBe(visible);
    }

    public void amountMoreThanBalance() {
        $(byText("Невозможно осуществить перевод, так как сумма превышает баланс.")).shouldBe(visible);
    }

    public void enterAnotherCard() {
        $(byText("Нельзя переводить с карты на эту же карту. Введите номер другой карты.")).shouldBe(visible);
    }

}
