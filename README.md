# Kafka Stream Data Enrichment System

A Spring Boot application that implements a Kafka Streams processing system for data enrichment and error handling.

## Features

- KStream and KTable data enrichment
- Error handling with automatic retry mechanism
- Error state management
- Monitoring and health checks
- Avro schema support
- Exactly-once processing guarantee

## Technology Stack

- Java 11+
- Spring Boot 2.7.x
- Kafka Streams 3.3+
- Gradle 7.6+
- Avro 1.11+
- Confluent Schema Registry 7.3+

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/
│   │       ├── config/       # Configuration classes
│   │       ├── model/        # Data models
│   │       ├── processor/    # Stream processors
│   │       ├── service/      # Business services
│   │       └── Application.java
│   └── resources/
│       ├── avro/            # Avro schema files
│       └── application.yml
└── test/                    # Test code
```

## Getting Started

### Prerequisites

- JDK 11 or later
- Gradle 7.6+
- Apache Kafka 3.3+
- Confluent Schema Registry

### Building the Project

```bash
./gradlew build
```

### Running the Application

```bash
./gradlew bootRun
```

## Configuration

The application can be configured through `application.yml`. Key configuration properties:

```yaml
kafka:
  bootstrap-servers: localhost:9092
  streams:
    application-id: data-enrichment-app
    properties:
      schema.registry.url: http://localhost:8081
      processing.guarantee: exactly_once_v2
```

## Monitoring

Health and metrics endpoints are available at:
- Health: `/health`
- Metrics: `/metrics`
- Prometheus: `/prometheus`

## Error Handling

The system includes:
- Automatic retry mechanism for failed records
- Error topic for tracking failures
- Configurable retry attempts and intervals
- Dead letter queue support

## License

This project is licensed under the MIT License. 