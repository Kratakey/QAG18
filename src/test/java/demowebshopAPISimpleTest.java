import org.junit.jupiter.api.*;
import helpers.AllureRestAssuredFilter;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static java.lang.Integer.valueOf;
import static org.hamcrest.Matchers.is;

public class demowebshopAPISimpleTest {

    String url = "http://demowebshop.tricentis.com",
            email = "test@test.gg",
            password = "1234pass",
            cookie = "__utmc=78382081; __utmz=78382081.1639964410.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __RequestVerificationToken=BkrlxM-hjLuBFBKGwS_Igjg_4chl41Ui9xIIcCFB4GqUrsDe1SoLQFsUXfsmjWh7Jlp9XL3TaF_j-vi3mFSKR0QUPTcx_4D3RF0f8aqH6YU1; ASP.NET_SessionId=sv45cgxfvlbs4r4weclt24db; NOPCOMMERCE.AUTH=5E9E3828F85796FFAE03D96D06F2EC7345AF26A2BCB8E055CC46CF8EDB15F75EBA0FBE2CEC7B3A87908CB4E1104DA1F28270081CD18856520D35DB7FC589EF858CA11934162B679D278C7DBA88A48A76547F3CFEB1A69B398338754A1A196A50DD30DCA8B465FC90A40DB3760E4353E6526C26C880DE21215A4042FD2E97932D; Nop.customer=01a95561-f90e-4750-8cc9-5617e6fb6d31; NopCommerce.RecentlyViewedProducts=RecentlyViewedProductIds=5&RecentlyViewedProductIds=72; ARRAffinity=55622bac41413dfac968dd8f036553a9415557909fd0cd3244e7e0e656e4adc8; __utma=78382081.1831960674.1639964410.1639973003.1640577831.3; __utmt=1; __atuvc=5%7C51%2C7%7C52; __atuvs=61c93b3fa003bc29006; __utmb=78382081.14.10.1640577831",
            quantity;
    int qty;

    @Test
    void addToWishlist1() {
        step("Get cookie by api and set it to browser", () -> {
            String authorizationCookie =
                    given()
                            .filter(AllureRestAssuredFilter.withCustomTemplates())
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", email)
                            .formParam("Password", password)
                            .when()
                            .post(url + "/login")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");

            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open(url + "/Themes/DefaultClean/Content/images/logo.png"));

            step("Set cookie to to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));

            step("Check UI", () ->
                    open(url));
            quantity = $("#topcartlink").sibling(0).$(".wishlist-qty").getText();
            quantity = quantity.replaceAll("[()]", "");
            qty = Integer.parseInt(quantity);

        step("Add to wishlist and check", () ->
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .cookie(cookie)
                            .when()
                            .post(url + "/addproducttocart/details/5/2")
                            .then()
                            .statusCode(200)
                            .body("Success", is(true),
                                    "message", is("The product has been added to your <a href=\"/wishlist\">wishlist</a>"),
                                    "updatetopwishlistsectionhtml", is(qty + 1)));
        });
    }
}