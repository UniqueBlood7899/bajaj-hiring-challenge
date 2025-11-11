# Bajaj Finserv Health - Hiring Challenge (Java)

A Spring Boot application built with **Java 24** that automates webhook generation and SQL query submission for the Bajaj Finserv Health hiring assessment.

## Features

- **Webhook Generation**: Automatically generates a webhook URL using candidate credentials
- **SQL Solution**: Implements a PostgreSQL query to find the highest-paid employee with specific payment criteria
- **Automated Submission**: Posts the final SQL query to the generated webhook endpoint
- **Modern Java**: Built with Java 24 features including text blocks for SQL queries
- **Spring Boot 3.5.7**: Latest Spring Boot framework with dependency injection and auto-configuration

## Prerequisites

- Java 24 (OpenJDK 24.0.2 or later)
- Gradle 8.14.3 (included via wrapper)

## Tech Stack

- **Java 24** - Latest Java runtime
- **Spring Boot 3.5.7** - Application framework
- **Spring Web** - REST client with RestTemplate
- **Jackson** - JSON serialization/deserialization
- **Gradle** - Build tool

## Project Structure

```
src/
├── main/
│   ├── java/io/pesu/hiring/
│   │   ├── App.java                          # Main application entry point
│   │   ├── config/
│   │   │   └── HttpConfig.java               # RestTemplate configuration
│   │   ├── model/
│   │   │   ├── GenerateWebhookRequest.java   # Webhook generation request
│   │   │   ├── GenerateWebhookResponse.java  # Webhook generation response
│   │   │   └── FinalQueryRequest.java        # Final query submission request
│   │   ├── service/
│   │   │   ├── SqlSolutionService.java       # SQL query builder
│   │   │   └── WebhookClient.java            # HTTP client for API calls
│   │   └── runner/
│   │       └── StartupRunner.java            # Application startup orchestrator
│   └── resources/
│       └── application.yml                    # Application configuration
└── test/
    └── java/io/pesu/hiring/
        └── AppTests.java                      # Unit tests
```

## Configuration

Update [`src/main/resources/application.yml`](src/main/resources/application.yml) with your credentials:

```yaml
app:
  name: "Your Name"
  regNo: "Your Registration Number"
  email: "your.email@domain"
```

## Running the Application

### Build the project:
```bash
./gradlew clean build
```

### Run the application:
```bash
./gradlew bootRun
```

### Expected Output:
```
[*] Generating webhook...
[*] Got webhook: GenerateWebhookResponse{webhook='https://...', accessToken='***'}
[*] Final SQL prepared:
SELECT
    p.amount AS salary,
    (e.first_name || ' ' || e.last_name) AS name,
    DATE_PART('year', AGE(CURRENT_DATE, e.dob))::int AS age,
    d.department_name
FROM payments p
JOIN employee e ON e.emp_id = p.emp_id
JOIN department d ON d.department_id = e.department
WHERE EXTRACT(DAY FROM p.payment_time) <> 1
ORDER BY p.amount DESC
LIMIT 1;
[*] Posting final query to webhook...
[✓] Done. Final query submitted.
```

## Testing

Run unit tests:
```bash
./gradlew test
```

## SQL Query Explanation

The solution finds the **highest-paid employee** whose payment was **not made on the 1st of the month**:

- Joins `payments`, `employee`, and `department` tables
- Filters out payments made on day 1 using `EXTRACT(DAY FROM p.payment_time) <> 1`
- Calculates age using PostgreSQL's `AGE()` and `DATE_PART()` functions
- Orders by salary descending and limits to 1 result

## API Endpoints Used

1. **Generate Webhook**: `POST https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA`
2. **Submit Query**: `POST <generated-webhook-url>` with Authorization header

## Author

**Surya P S**
- Registration: PES2UG22CS603
- Email: pes2ug22cs603@pesu.pes.edu

## License

This project is created for the Bajaj Finserv Health hiring challenge.

## Acknowledgments

- Bajaj Finserv Health for the hiring challenge
- Spring Boot team for the excellent framework