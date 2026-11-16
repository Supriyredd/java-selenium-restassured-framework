package com.sdubbaka.qa.tests.ui;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.sdubbaka.qa.config.Config;
import com.sdubbaka.qa.pages.InventoryPage;
import com.sdubbaka.qa.pages.LoginPage;
import com.sdubbaka.qa.utils.TestData;
import java.util.Map;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseUiTest {

    @Test(groups = "smoke")
    public void validUserSeesProductList() {
        InventoryPage inventory = new LoginPage(driver()).open().loginAs(Config.username(), Config.password());
        assertEquals(inventory.productCount(), 4);
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return TestData.rows("invalid-logins.json");
    }

    @Test(dataProvider = "invalidLogins", groups = "regression")
    public void invalidLoginShowsError(Map<String, String> row) {
        LoginPage page = new LoginPage(driver()).open().submit(row.get("username"), row.get("password"));
        assertEquals(page.errorText(), row.get("error"), "case: " + row.get("case"));
        assertTrue(driver().getCurrentUrl().endsWith("/"), "should stay on login page");
    }
}
