# Java Selenium + REST Assured Framework

![tests](https://github.com/Supriyredd/java-selenium-restassured-framework/actions/workflows/tests.yml/badge.svg)

A Java 17 test automation framework for UI and API testing: Selenium WebDriver 4 with the Page Object Model, REST Assured 5 with JSON schema validation, TestNG data providers and parallel execution, and a GitHub Actions pipeline across Chrome and Firefox.

> **Portfolio project.** Written independently against a small demo shop bundled in `app/` (Node, synthetic data). It contains no employer or client code, data or configuration.

## What this repository demonstrates

| Skill | Where to look |
| --- | --- |
| Page Object Model with explicit waits only | `pages/BasePage.java`, `pages/*Page.java` |
| Thread-safe WebDriver (ThreadLocal) for parallel TestNG runs | `driver/DriverFactory.java`, `testng.xml` |
| Config from properties, overridable by `-D` flags or env vars | `config/Config.java` |
| Data-driven tests from JSON via `@DataProvider` | `utils/TestData.java`, `tests/ui/LoginTest.java` |
| REST Assured request spec, auth token handling, schema validation | `api/ShopApi.java`, `tests/api/` |
| Negative API cases (400/401/403/404/409/422) | `tests/api/OrdersApiTest.java` |
| Cross-layer check: UI order verified through the API | `tests/ui/CheckoutTest.java` |
| Screenshot on failure (TestNG listener) | `utils/ScreenshotOnFailureListener.java` |
| CI quality gate, browser matrix, report artifacts | `.github/workflows/tests.yml` |

## Run it

Requires Java 17+, Maven 3.9+, Node 20+ and Chrome or Firefox (Selenium Manager downloads the driver).

```bash
node app/server.js &                       # start the demo app on :3000
mvn test                                   # full suite, headless Chrome
mvn test -Dbrowser=firefox -Dheadless=false
mvn test -Dgroups=smoke                    # smoke group only
```

Override any setting with a system property or environment variable, for example `BASE_URL=https://staging.example.com mvn test`. Credentials for real environments belong in CI secrets, never in `config.properties`.

## Design notes

- Locators use `data-test` attributes, so page styling changes do not break tests.
- No `Thread.sleep` and no implicit waits; every interaction waits for a specific condition.
- Each UI test gets a fresh browser (`@BeforeMethod`/`@AfterMethod`), so tests do not share state and can run in any order.
- API tests create their own data and do not depend on each other.

## Limitations and next steps

- Next: Allure reporting, Selenium Grid / BrowserStack execution via `RemoteWebDriver`, Cucumber feature files on top of the same page objects.

## About the author

Supriya Dubbaka, Senior SDET. [LinkedIn](https://www.linkedin.com/in/supriya-dubbaka-64ba7532a/)

Built with AI-assisted development tools (GitHub Copilot / Claude); all code reviewed and run before publishing.

## License

MIT
