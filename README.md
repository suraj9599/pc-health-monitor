# PC Health Monitoring Tool

A real-time PC Health Monitoring application built with **Java 21, Spring Boot, MySQL, and OSHI**. It monitors system resources, tracks network activity, stores historical performance metrics, and generates alerts when resource usage crosses defined thresholds.

## Features

### System Information
- Detects computer manufacturer and model.
- Displays operating system and version.
- Retrieves processor details and core counts.
- Displays total RAM, disk capacity, and system uptime.

### Resource Monitoring
- Real-time CPU usage monitoring.
- RAM usage and availability tracking.
- Disk usage and free-space monitoring.
- Automatic system health status: `NORMAL`, `WARNING`, or `CRITICAL`.

### Network Monitoring
- Detects available network interfaces.
- Displays interface names, IP addresses, and connection status.
- Tracks network bytes and packets received and sent.
- Calculates historical download and upload speeds.

### Historical Metrics
- Automatically collects system metrics every 30 seconds.
- Stores monitoring data in MySQL.
- Displays historical CPU, RAM, and disk usage charts.
- Supports multiple time ranges: 1 hour, 6 hours, 24 hours, and 7 days.
- Automatically deletes metrics older than 30 days.

### Alert Management
- Generates alerts when CPU, RAM, or disk usage reaches warning or critical levels.
- Tracks recovery when resource usage returns to normal.
- Prevents repeated alerts when the status remains unchanged.
- Supports filtering alerts by resource type and status.

### Dashboard and Reports
- Dark-themed monitoring dashboard.
- Live resource monitoring with automatic refresh.
- Historical performance charts.
- Network traffic visualization.
- CSV export for metrics and alerts.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming |
| Spring Boot | REST API and application framework |
| Spring Data JPA | Database operations |
| MySQL 8 | Persistent data storage |
| OSHI | Hardware and operating system information |
| HTML, CSS, JavaScript | Frontend dashboard |
| Chart.js | Performance charts |
| Maven | Dependency management and build |
| Postman | API testing |
| IntelliJ IDEA | Development environment |

## Project Structure

```text
PCHealthMonitor/
├── src/
│   └── main/
│       ├── java/com/pcmonitor/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── monitoring/
│       │   ├── repository/
│       │   ├── service/
│       │   └── PcHealthMonitorApplication.java
│       │
│       └── resources/
│           ├── static/
│           │   ├── css/style.css
│           │   ├── js/dashboard.js
│           │   └── index.html
│           └── application.properties
│
├── pom.xml
└── README.md
```

## Prerequisites

Before running the application, install:

- Java Development Kit (JDK) 21
- MySQL 8
- Maven (or use the Maven Wrapper if included)
- IntelliJ IDEA or another Java IDE

## Installation and Setup

### 1. Clone the repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

Navigate to the project directory:

```bash
cd PCHealthMonitor
```

### 2. Create the MySQL database

Open MySQL and execute:

```sql
CREATE DATABASE pc_health_monitor;
```

### 3. Configure environment variables

Set the following environment variables in your IDE's Run Configuration or operating system:

```text
DB_URL=jdbc:mysql://localhost:3306/pc_health_monitor
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

Replace `your_mysql_password` with your local MySQL password.

**Security note:** Never commit your actual database password or other credentials to GitHub.

### 4. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Alternatively, run `PcHealthMonitorApplication.java` from your IDE.

### 5. Open the dashboard

Once the application starts, open:

```text
http://localhost:8080/
```

The application automatically creates the required database tables through JPA configuration.

## REST API Endpoints

All endpoints use the base URL:

```text
http://localhost:8080
```

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/system/info` | Retrieves system information |
| GET | `/api/monitoring/health` | Retrieves CPU, RAM, and disk health |
| GET | `/api/monitoring/network` | Retrieves network interface information |
| GET | `/api/metrics/latest` | Retrieves the latest stored metrics |
| GET | `/api/metrics/history?from=...&to=...` | Retrieves historical metrics |
| GET | `/api/alerts` | Retrieves the latest alerts |
| GET | `/api/alerts/all` | Retrieves all alerts |
| GET | `/api/alerts/filter?metricType=RAM` | Filters alerts by resource type |
| GET | `/api/alerts/filter?status=WARNING` | Filters alerts by status |

## Monitoring Configuration

The monitoring settings can be configured in `application.properties`:

```properties
monitoring.collection-interval=30000
monitoring.retention-days=30
monitoring.cleanup-cron=0 0 2 * * *
```

| Setting | Value | Description |
|---|---|---|
| Collection interval | 30 seconds | Collects system metrics |
| Data retention | 30 days | Keeps historical metrics |
| Cleanup schedule | 2:00 AM daily | Removes expired metrics |

## Screenshots

Add screenshots of your running application here to help visitors understand the dashboard.

Suggested screenshots:

- Main dashboard
- CPU, RAM, and disk monitoring
- Historical performance charts
- Network traffic monitoring
- Alert management and filters
- CSV export results

For example, save your screenshots in a folder named `screenshots` and embed them like this:

```markdown
[//]: # (![PC Health Monitor Dashboard]&#40;screenshots/dashboard.png&#41;)
```

## Future Enhancements

- Multi-computer monitoring.
- Email notifications for critical alerts.
- User authentication and role-based access.
- Advanced system performance reports.
- Configurable alert thresholds.

## Author

**Suraj Singh**

Developed as a hands-on project to strengthen practical skills in Java, Spring Boot, REST APIs, MySQL, system monitoring, and IT support.

---

If you find this project useful, feel free to explore the source code and share feedback.
