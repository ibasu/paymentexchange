<span><img src="https://media.licdn.com/dms/image/C4E0BAQEFuIz-Ln63-w/company-logo_200_200/0/1644510338696?e=1702512000&v=beta&t=EutZaLMDC9Ubwbpz0vWcdJfdeTnHdve7z6EqegmOOes" height="60"></span>

## Prerequisites
- JDK 11+ installed with JAVA_HOME configures
- Gradle configured

## Supported technologies

Languages:

* Java

Framework/Module:
* Spring Boot
* Spring Framework
* Spring WebFlux - Reactive

## Getting started

### Problem Description
### Requirement #1: Store a Purchase Transaction
The application must be able to accept and store (i.e., persist) a purchase transaction with a description, transaction
date, and a purchase amount in United States dollars. When the transaction is stored, it will be assigned a unique
identifier.
#### Field requirements
- Description: must not exceed 50 characters
- Transaction date: must be a valid date
- Purchase amount: must be a valid amount rounded to the nearest cent
- Unique identifier: must uniquely identify the purchase

### Requirement #2: Retrieve a Purchase Transaction in a Specified Country’s Currency

Based upon purchase transactions previously submitted and stored, your application must provide a way to retrieve the
stored purchase transactions converted to currencies supported by the Treasury Reporting Rates of Exchange API based
upon the exchange rate active for the date of the purchase.
https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange
The retrieved purchase should include the identifier, the description, the transaction date, the original US dollar purchase
amount, the exchange rate used, and the converted amount based upon the specified currency’s exchange rate for the
date of the purchase.

Currency conversion requirements
- When converting between currencies, you do not need an exact date match, but must use a currency conversion
rate less than or equal to the purchase date from within the last 6 months.
- If no currency conversion rate is available within 6 months equal to or before the purchase date, an error should
be returned stating the purchase cannot be converted to the target currency.
- The converted purchase amount to the target currency should be rounded to two decimal places (i.e., cent).

### Solution

### Project Name : Payment Exchange

##### This is a simple gradle java spring boot application which exposes 2 RESTFul API endpoints
The **Payment Exchange** microservice is part of **Internation Payments** umbrella. 
The Payment Exchange microservice, is responsible for 
maintaining:

1. Create New Payments with Default USD currency
2. Retrieve an existing Payment in different country currency

Following are the 2 endpoints exposed by this microservice:
- POST /paymentexchange/api/v1/payments : Creates a new payment transaction in USD
- GET /paymentexchange/api/v1/payments/{transactionId}/{countryCurrencyDesc} : Retrieves an existing transaction in the sepecified Country Currency

##### Design Considerations
- A java based spring boot RESTful application which accepts HTTP requests in JSON format ONLY
- The program returns the custom result object which contains the output and errors(if any)
- The application uses in memory H2 database as the data store
- DB migration is being done using Flyway
- Payment Transaction Date has been deliberately kept an input to the request as its possible the payment might have been warehoused overnight
- The microservice loads all the country currency desc using the https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country_currency_desc&page%5Bsize%5D=500
- However, the above loading is disabled by default during the execution of tests
- The country currency is loaded in the memory to validate the user input to retrieve any existing transaction in specified country's currency

##### Validation
- The country currency description input for retrieving existing transaction should be part of the list exposed by Fiscal Treasury : https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country_currency_desc
- Please refer to the swagger for other mandatory fields and the format of the data

### Build the spring boot application

```
./gradlew 
```
### Generate Test Reports

```
./gradlew jacocoTestReport
```

### Start the gradle Java application

```groovy
./gradlew bootRun
```
#### Or
```bash
java -jar build/libs/paymentsexchange-0.0.1-SNAPSHOT.jar
```

### Swagger
Swagger can be found in the below url:

```
http://localhost:8080/paymentexchange/swagger-ui.html
```

### Postman Collection
Postman collection is also included in the archive, named as Ledger Growth.postman_collection.json

##### Create New Payment Transaction
```bash
curl --location --request POST 'http://localhost:8080/paymentexchange/api/v1/payments' \
--header 'Content-Type: application/json' \
--data-raw '{
    "purchaseAmount": 12345.22,
    "transactionDescription": "sample payment",
    "transactionDate": "2023-09-05T20:01:18"
}'
```
- Response
```json
{
  "originalCurrency": "USD",
  "transactionId": "dd939d41-7860-42c3-8b61-c6156c88762a",
  "purchaseAmount": 12345.22,
  "transactionDescription": "sample payment",
  "transactionDate": "2023-09-05T20:01:18",
  "createdUser": "System"
}
```

##### Retrieve Existing Payment in India Rupee
```bash
curl --location --request GET 'http://localhost:8080/paymentexchange/api/v1/payments/dd939d41-7860-42c3-8b61-c6156c88762a/India-Rupee' \
--header 'Content-Type: application/json' 
```
- Response
```json
{
    "paymentTransactionDetails": {
        "originalCurrency": "USD",
        "transactionId": "dd939d41-7860-42c3-8b61-c6156c88762a",
        "purchaseAmount": 12345.22,
        "transactionDescription": "some",
        "transactionDate": "2023-09-05T20:01:18",
        "createdUser": "System"
    },
    "exchangeRateDetails": {
        "exchangeRate": 82.09,
        "originalPurchaseAmount": 12345.22,
        "convertedPurchaseAmount": 1013419.11,
        "convertedCountryCurrency": "India-Rupee"
    }
}
```
