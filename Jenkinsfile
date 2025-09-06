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

stage('5. Deploy to Tomcat') {
    when {
        branch 'develop'
    }
    steps {
        sh 'mvn package'
        echo "Starting a final, clean deployment to Tomcat..."
        
        sh '''
            echo "Stopping Tomcat..."
            sudo systemctl stop tomcat9

            echo "Cleaning up old application..."
            sudo rm -rf /var/lib/tomcat9/webapps/NumberGuessGame
            sudo rm -f /var/lib/tomcat9/webapps/NumberGuessGame.war

            echo "Copying new WAR file..."
            sudo cp target/*.war /var/lib/tomcat9/webapps/NumberGuessGame.war

            echo "Changing ownership to the tomcat user..."
            sudo chown tomcat:tomcat /var/lib/tomcat9/webapps/NumberGuessGame.war

            echo "Starting Tomcat..."
            sudo systemctl start tomcat9
        '''
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