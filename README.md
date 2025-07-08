[![Maven Central](https://img.shields.io/maven-central/v/tools.dynamia.reports/tools.dynamia.reports.core)](https://search.maven.org/search?q=tools.dynamia.reports)
![Java Version Required](https://img.shields.io/badge/java-21-blue)
[![Maven Build](https://github.com/dynamiatools/dynamia-reports/actions/workflows/maven.yml/badge.svg)](https://github.com/dynamiatools/dynamia-reports/actions/workflows/maven.yml)
[![Release and Deploy](https://github.com/dynamiatools/dynamia-reports/actions/workflows/release.yml/badge.svg)](https://github.com/dynamiatools/dynamia-reports/actions/workflows/release.yml)

# DynamiaReports

Is a small reporting framework built using DynamiaTools for generate reports in app storing queries metadata in database and allowing users to run, edit, copy the reports. 

## Initial Features
- Create Queries using JPSQL or native SQL
- Create or use current database datasource
- Use filters
- Show reports in screen
- Exports reports to CVS, Excel and PDF
- Send reports to email
- View Reports 
- Embed reports in other apps
- Integrate as a module with any DynamiaTools modules
- View reports data as charts


## Architecture
Modules organization

### Core Module
Main module with all reporting logic, allowing to run, store, filter and generate report data. 

### UI Module
- View report data in table view
- Edit reports
- Show report filters
- Export to CSV, Excel and PDF actions
- Custom report actions for extensions

### Datasources Modules
Additional module for  create and connect to external datasources. Mainly SQL databases, later NoSQL and plain files

### Boot module
A spring boot application to run DynamiaReports as a standalone app.

## Docker Deployment

DynamiaReports can be deployed using Docker. The application uses a lightweight Java 21 base image and is configured to run in Spanish (es_CO) locale with Bogotá timezone.

### Required Environment Variables

The following environment variables can be configured when running the container:

1. **Database Configuration**
   - `REPORTS_DB_DRIVER`: Database driver class (default: `org.hsqldb.jdbc.JDBCDriver`)
   - `REPORTS_DB_URL`: Database connection URL (default: `jdbc:hsqldb:file:${REPORTS_HOME}/reports.db`)
   - `REPORTS_DB_USER`: Database username
   - `REPORTS_DB_PASSWORD`: Database password
   - `REPORTS_DB_HIBERNATE_DDL_AUTO`: Hibernate DDL strategy (default: `update`)
   - `REPORTS_DB_SHOW_SQL`: Show SQL logs (default: `true`)

2. **Application Configuration**
   - `REPORTS_HOME`: Base directory for reports and logs (default: current directory)

### Running with Docker

1. Pull the latest image:
   ```bash
   docker pull dynamia/tools-reports
   ```

2. Run the container:
   ```bash
   docker run -d \
     -p 8080:8080 \
     -e REPORTS_DB_DRIVER=org.hsqldb.jdbc.JDBCDriver \
     -e REPORTS_DB_URL=jdbc:hsqldb:file:/data/reports.db \
     -e REPORTS_HOME=/data \
     -v /path/to/local/data:/data \
     dynamia/tools-reports:latest
   ```

The application will be available at `http://localhost:8080` once the container is running.

### Volume Mapping

It's recommended to map a local volume for:
- Reports database (if using HSQLDB) or use custom JDBC connection to store reports
- Log files
- Report files and configurations

This ensures data persistence between container restarts.

### Port Configuration

The application runs on port 8080 by default, but this can be changed by mapping a different host port in the docker run command.


# License
Opensource project using Apache 2.0 license

