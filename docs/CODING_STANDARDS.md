# XRPShield — Engineering & Coding Standards

## 1. Core Principles
* **Clean Architecture:** Strict boundaries between HTTP Controllers, Business Services, JPA Repositories, Smart Contracts, and UI Components.
* **SOLID Compliance:** Single responsibility per class, dependency injection via constructor, open/closed interface contracts.
* **Zero Dummy / Simulation Policy:** Never invent placeholder functions, dummy APIs, hardcoded credentials, or stub responses.
* **DTO Separation:** Domain Entities must never be returned directly in REST API responses. Always map through typed DTO objects (`ApiResponse<T>`).

---

## 2. Java / Spring Boot Guidelines
* **Target Java Version:** Java 21 LTS syntax & features.
* **Dependency Injection:** Use Spring `@RequiredArgsConstructor` or explicit constructor injection. Never use `@Autowired` on fields.
* **Exception Handling:** Exception handling must route through `GlobalExceptionHandler` returning `ErrorDetails`.
* **Logging:** Use SLF4J (`org.slf4j.Logger`) for all log statements. Never use `System.out.println`.

---

## 3. Smart Contract Guidelines (Solidity)
* **Compiler Target:** Solidity `0.8.24` with optimizer enabled (200 runs).
* **Security Checks:** Explicit reentrancy protection, access control modifiers, and standard event emission.
* **Documentation:** Use Natspec tags (`@title`, `@dev`, `@param`, `@return`) for all public functions.

---

## 4. Frontend Guidelines
* **Native Standards:** HTML5, CSS3, Vanilla ES6 JavaScript modules.
* **No Unused CSS/JS:** Every utility class and component function must serve an active UI purpose.
* **SPA Routing:** Clean hash routing with async component views.
