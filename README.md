# Number Guessing Game: A CI/CD DevOps Project

This repository contains a Java-based web application for a Number Guessing Game. The primary goal of this project is to demonstrate a fully automated CI/CD pipeline using modern DevOps tools and practices. The pipeline automates the process of building, testing, analyzing, and deploying the application.

## Project Objective

The core objective is to develop and deploy a fully automated pipeline for a Java-based web application. This project focuses on enabling continuous integration and continuous deployment (CI/CD), adhering to DevOps best practices for collaboration, code quality, and automation.

## Application Functionality

* The application is a simple number guessing game where a random number between 1 and 100 is generated.
* Users can submit their guesses via a web form.
* The application provides feedback if the guess is too high, too low, or correct.
* Upon a correct guess, the game congratulates the user and resets the number for a new game.

## Technologies Used

* **Language:** Java 17
* **Build Tool:** Apache Maven
* **Web Framework:** Java Servlets
* **Testing:** JUnit 4
* **CI/CD Server:** Jenkins
* **Code Quality:** SonarQube
* **Deployment Target:** Apache Tomcat Server
* **Version Control:** Git & GitHub

## Project Directory Structure

The project follows the standard Maven web application directory structure.

```md
NumberGuessGame/
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── studentapp
│   │   │           └── NumberGuessServlet.java
│   │   └── webapp
│   │       ├── WEB-INF
│   │       │   └── web.xml
│   │       └── index.jsp
│   └── test
│       └── java
│           └── com
│               └── studentapp
│                   └── NumberGuessServletTest.java
└──README.md
├── .gitignore
└── jenkinsfile
```

## Local Build and Test Instructions

### Prerequisites

* Java Development Kit (JDK) 17
* Apache Maven

### Commands

1. **Clone the repository:**

```bash
    git clone https://github.com/omotomiwa26/NumberGuessGame.git
    cd NumberGuessGame
```

2. **Compile the source code:**

 ```bash
    mvn compile
```

3. **Run unit tests:**

 ```bash
    mvn test
```

4. **Package the application into a WAR file:**

```bash
    mvn package
```

This will generate a `NumberGuessGame.war` file in the `target/` directory, which can be manually deployed to a Tomcat server.

## CI/CD Pipeline Overview

The entire build, test, and deployment process is automated using a Jenkins pipeline, defined in the `Jenkinsfile` at the root of this repository.

### Pipeline Triggers

* **CI Trigger (Validation):** The pipeline is automatically triggered when a developer opens or updates a Pull Request targeting the `develop` branch. This runs all validation steps to provide feedback before merging.
* **CD Trigger (Deployment):** After a Pull Request is approved and merged, a push event to the `develop` branch automatically triggers the pipeline again, this time including the deployment stage.

### Pipeline Stages

1. **Checkout:** Clones the source code from the GitHub repository.
2. **Compile:** Compiles the Java source code using Maven.
3. **Unit Test:** Executes all JUnit tests to ensure code correctness and prevent regressions.
4. **Code Quality Scan:** Performs a static code analysis using SonarQube to check for bugs, vulnerabilities, and code smells, ensuring code quality and maintainability.
5. **Build Package:** Packages the compiled application into a `.war` artifact if all previous stages succeed.
6. **Deploy to Tomcat:** Deploys the `.war` artifact to a running Apache Tomcat server, making the application accessible via a web browser. This stage only runs on successful merges to the `develop` branch.

## Collaboration Workflow

To ensure effective teamwork and maintain a clean codebase, this project follows a Git Flow-based branching strategy.

* **`main` branch:** This branch is protected and contains only stable, production-ready code.
* **`develop` branch:** This is the primary integration branch. All feature branches are merged into `develop` after review.
* **`feature/*` branches:** All new development and bug fixes are done in feature branches (e.g., `feature/add-styling`, `feature/improve-tests`).

**Workflow:**

1. Create a `feature` branch from `develop`.
2. Complete the work and commit changes.
3. Open a Pull Request to merge the `feature` branch into `develop`.
4. The PR triggers the Jenkins validation pipeline. All checks must pass.
5. After a successful code review and pipeline run, the PR is merged.
6. The merge to `develop` triggers the deployment pipeline, automatically updating the application on the server.

## Team Members

* [omotomiwa afonja](https://github.com/omotomiwa26)
* [onoh chisom](https://github.com/Munachis0)
* [olubunmi adekanmbi](https://github.com/olubunmi-ade)
