import org.junit.jupiter.api.*;
import helpers.AllureRestAssuredFilter;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class DemowebshopAPISimpleTest {

    String url = "http://demowebshop.tricentis.com",
            email = "test@test.gg",
            password = "1234pass",
            quantityString;
    int quantityInt;

    @Test
    void addToWishlist() {
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

            step("Check UI quantity", () ->
                    open(url));
            quantityString = $("#topcartlink").sibling(0).$(".wishlist-qty").getText();
            quantityString = quantityString.replaceAll("[()]", "");
            quantityInt = Integer.parseInt(quantityString);
            quantityInt++;
            quantityString = "(" + quantityInt + ")";

            step("Add to wishlist and check", () ->
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .body("product_attribute_5_7_1=1&addtocart_5.EnteredQuantity=1")
                            .cookie("NOPCOMMERCE.AUTH", authorizationCookie)
                            .when()
                            .post(url + "/addproducttocart/details/5/2")
                            .then()
                            .statusCode(200)
                            .body("success", is(true),
                                    "message", is("The product has been added to your <a href=\"/wishlist\">wishlist</a>"),
                                    "updatetopwishlistsectionhtml", is(quantityString)));
        });
    }
}