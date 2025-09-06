pipeline {
    agent any

    environment {
        TOMCAT_URL = credentials('tomcat-url')
    }

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    stages {
        stage('1. Checkout') {
            steps {
                checkout scm
            }
        }

        stage('2. Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('3. Unit Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('4. Code Quality Scan') {
            steps {
                withSonarQubeEnv('MySonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        // stage('5. Publish to Nexus') {
        //     when {
        //         branch 'develop'
        //     }
        //     steps { 
        //         withMaven(maven: 'Maven3', globalMavenSettingsConfig: '', mavenSettingsConfig: 'nexus-maven-config') {
                   
        //             withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD')]) {
        //                 sh 'mvn deploy'
        //             }
        //         }
        //     }
        // }

        stage('6. Deploy to Tomcat') {
            when {
                branch 'develop'
            }
            steps {
                sh 'mvn package'
                
                deploy adapters: [tomcat9(credentialsId: 'tomcat-credentials', path: '', url: env.TOMCAT_URL)],
                       contextPath: 'NumberGuessGame', war: 'target/*.war'
            }
        }
    }

    post {
        success {
            slackSend botUser: true, channel: '#number-guess-game-ci-cd-build-alert', color: 'good', message: "SUCCESSFUL: `${env.JOB_NAME}` build `${env.BUILD_NUMBER}`. Details: ${env.BUILD_URL}"
        }
        failure {
            slackSend botUser: true, channel: '#number-guess-game-ci-cd-build-alert', color: 'danger', message: "FAILED: `${env.JOB_NAME}` build `${env.BUILD_NUMBER}`. Check console: ${env.BUILD_URL}"
        }
    }
}