# How to checkout

```
git clone https://github.com/zolv/simple-money-transfer-service.git
```

# How to build

Execute from project's root directory:

```
mvn clean test jacoco:report package
```

Expected output:

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  10.921 s
[INFO] Finished at: 2020-03-13T00:05:59+01:00
[INFO] ------------------------------------------------------------------------
```

# How to run


Execute from project's root directory:

```
java -jar target/simple-money-transfer-service-1.0.0.jar

```

Expected output:

```
[main] INFO io.javalin.Javalin - 
           __                      __ _
          / /____ _ _   __ ____ _ / /(_)____
     __  / // __ `/| | / // __ `// // // __ \
    / /_/ // /_/ / | |/ // /_/ // // // / / /
    \____/ \__,_/  |___/ \__,_//_//_//_/ /_/

        https://javalin.io/documentation

[main] INFO org.eclipse.jetty.util.log - Logging initialized @253ms to org.eclipse.jetty.util.log.Slf4jLog
[main] INFO io.javalin.Javalin - Starting Javalin ...
[main] INFO io.javalin.Javalin - Listening on http://localhost:8080/
[main] INFO io.javalin.Javalin - Javalin started in 133ms \o/
```

Go to Your browser and visit:

```
http://localhost:8080/
```

Expected test welcome message:

```
Hello Fellow! Your Simple Money Transfer Service is up and running
```

By default, every account has obviously 0 balance. If You run the app with `withBalance` parameter, You can set a balance on account creation so You can test money transfers.

```
java -jar target/simple-money-transfer-service-1.0.0.jar withBalance

```

# Available endpoints

### Create an account

```
POST /accounts
```
Request:

```
{
	"currency": "EUR",
	"balance": 1000
}
```
Note: balance field is used only for testing purpose together with `withBalance` flag (see above)

Response:

```
{
	"id": "5d2e70ea-19c1-45cb-b3f3-1e530fd7aaef",
	"currency": "EUR",
	"balance": 1000
}
```

### Get an account
```
GET /accounts/{id}
GET /accounts/5d2e70ea-19c1-45cb-b3f3-1e530fd7aaef
```
Response:

```
{
	"id": "5d2e70ea-19c1-45cb-b3f3-1e530fd7aaef",
	"currency": "EUR",
	"balance": 1000
}
```

### Create a money transfer

```
POST /transfers
```
Request:

```
{
	"fromAccountId": "615a6302-eb30-4a9f-bac2-dbb7e658d1e2",
	"toAccountId": "5d2e70ea-19c1-45cb-b3f3-1e530fd7aaef",
	"amount": 700,
	"currency": "EUR"
}
```
Response:

```
{
	"id": "f1a78350-9bbe-4f81-84c7-830ffe28bf69",
	"fromAccountId": "615a6302-eb30-4a9f-bac2-dbb7e658d1e2",
	"toAccountId": "5d2e70ea-19c1-45cb-b3f3-1e530fd7aaef",
	"amount": 700,
	"currency": "EUR",
	"status": "NEW"
}
```


### Get a money transfer

```
GET /transfers/{id}
GET /transfers/f1a78350-9bbe-4f81-84c7-830ffe28bf69
```

Response:

```
{
	"id": "f1a78350-9bbe-4f81-84c7-830ffe28bf69",
	"fromAccountId": "615a6302-eb30-4a9f-bac2-dbb7e658d1e2",
	"toAccountId": "5d2e70ea-19c1-45cb-b3f3-1e530fd7aaef",
	"amount": 700,
	"currency": "EUR",
	"status": "PROCESSED"
}
```

# Design notes

- KISS as a general rule in mind (involves some of the notes below) 
- Why `Javalin`? It's lightweight, fast (~150ms for startup) and powerful HTTP server.
- There is no DI framework. Simple DI solution (by Singleton patter and context name lookup was manually implemented).
- Models separation: API model - Domain Model - Persistence Model.
- Async transfer processing (with CQRS)
- `concurrent-junit` used for stress tests.
- `jacoco` used for test coverage. Unfortunately, it doesn't count some parts but debugging clearly proves the flow reaches the point.

# Room for improvement

- In FinTech industry, `BigInteger` with minor units is preferred instead to `BigDecimal` to guarantee precision.
- Rounding of `BigDecimal` exchange ratios can be improved so there are always 2 decimal places and not more.
- Use `Joda Money` instead of 2 above
- OpenApi may be introduced with code generation.
- In memory persistence layer should use entity cloning for every CRUD operation.
- More specific error handling matched to HTTP status codes.
- ...and many others.

# Why improvements were not applied?

If I apply all of improvements it will end up with fully scalable, performant, well design, documented, rock-solid API and no bugs microservices ecosystem which just needs resources to fulfill them.
This project fulfills requirements for *Simple* Money Transfer Service.

