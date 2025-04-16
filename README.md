# Percy Selenium Java Framework

A comprehensive test automation framework using Percy for visual testing with Selenium WebDriver.

## Overview

This framework allows you to perform visual regression testing using Percy and Selenium WebDriver. It reads test data from an Excel file and captures snapshots for visual comparison.

## Features

- Integration with Percy for visual testing
- Excel-driven test data management
- Parallel test execution
- CI/CD integration with GitHub Actions
- Cross-browser testing support
- Reporting

## Prerequisites

- Java JDK 11 or higher
- Maven 3.6.3 or higher
- Chrome/Firefox browser
- Percy account and token

## Project Structure

```
percy-selenium-java-master/
├── .github/
│   └── workflows/
│       └── main.yml         # GitHub Actions CI pipeline
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/
│   │   │       └── percy/
│   │   │           └── selenium/
│   │   │               ├── core/       # Core framework classes
│   │   │               ├── utils/      # Utility classes
│   │   │               └── Percy.xlsx  # Test data
│   │   └── resources/
│   │       └── config.properties   # Configuration properties
│   └── test/
│       ├── java/
│       │   └── io/
│       │       └── percy/
│       │           └── selenium/
│       │               └── BajajDemoTest.java  # Your test class
│       └── resources/
│
