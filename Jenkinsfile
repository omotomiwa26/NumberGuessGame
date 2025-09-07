pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    stages {
        stage('1. Checkout Source') {
            steps {
                script {
                    echo "🔄 Checking out source code from ${env.BRANCH_NAME}"
                }
                checkout scm
            }
        }

        stage('2. Compile Code') {
            steps {
                script {
                    echo "⚙️ Compiling source on branch: ${env.BRANCH_NAME}"
                }
                sh 'mvn compile'
            }
        }

        stage('3. Run Unit Tests') {
            steps {
                script {
                    echo "🧪 Running unit tests for ${env.BRANCH_NAME}"
                }
                sh 'mvn test'
            }
        }

        stage('4. Code Quality Scan (SonarQube)') {
            steps {
                script {
                    echo "🔍 Running SonarQube scan on ${env.BRANCH_NAME}"
                }
                withSonarQubeEnv('MySonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('5. Deploy to Dev Tomcat') {
            when {
                allOf {
                    branch 'develop'
                    not { changeRequest() }
                }
            }
            steps {
                script {
                    echo "🚀 Deploying build ${env.BUILD_NUMBER} to DEV Tomcat from branch: ${env.BRANCH_NAME}"
                }

                sh 'mvn package'

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

        stage('6. Deploy to Prod Tomcat') {
            when {
                allOf {
                    branch 'main'
                    not { changeRequest() }
                }
            }
            steps {
                script {
                    echo "🚀 Deploying build ${env.BUILD_NUMBER} to PROD Tomcat from branch: ${env.BRANCH_NAME}"
                }

                sh 'mvn package'

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
        script {
            def buildType = (env.CHANGE_ID) ? "PR Build" : env.BRANCH_NAME
            slackSend botUser: true, channel: '#number-guess-game-ci-cd-build-alert',
                color: 'good',
                message: "✅ SUCCESSFUL: `${buildType}` | Job `${env.JOB_NAME}` build `${env.BUILD_NUMBER}`\n🔗 Details: ${env.BUILD_URL}"

            if (env.BRANCH_NAME == "develop" || env.BRANCH_NAME == "main") {
                sh '''
                    BACKUP_DIR=/var/lib/tomcat9/backups
                    TIMESTAMP=$(date +%Y%m%d%H%M%S)

                    if [ -f /var/lib/tomcat9/webapps/NumberGuessGame.war ]; then
                        echo "📦 Backing up deployed WAR as NumberGuessGame-$TIMESTAMP.war..."
                        sudo cp /var/lib/tomcat9/webapps/NumberGuessGame.war $BACKUP_DIR/NumberGuessGame-$TIMESTAMP.war || true

                        echo "Updating last-stable.war symlink..."
                        sudo ln -sf $BACKUP_DIR/NumberGuessGame-$TIMESTAMP.war $BACKUP_DIR/last-stable.war || true
                        sudo chown -h tomcat:tomcat $BACKUP_DIR/last-stable.war || true
                    else
                        echo "⚠️ No deployed WAR found to back up."
                    fi
                '''
            }
        }
    }

    failure {
        script {
            def buildType = (env.CHANGE_ID) ? "PR Build" : env.BRANCH_NAME
            slackSend botUser: true, channel: '#number-guess-game-ci-cd-build-alert',
                color: 'danger',
                message: "❌ FAILED: `${buildType}` | Job `${env.JOB_NAME}` build `${env.BUILD_NUMBER}`\n🔗 Console: ${env.BUILD_URL}"

            if (env.BRANCH_NAME == "develop" || env.BRANCH_NAME == "main") {
                echo "⚠️ Deployment failed on ${env.BRANCH_NAME}, initiating rollback..."
                try {
                    sh '''
                        BACKUP_DIR=/var/lib/tomcat9/backups

                        if [ -f $BACKUP_DIR/last-stable.war ]; then
                            echo "Stopping Tomcat..."
                            sudo systemctl stop tomcat9

                            echo "Restoring last stable WAR..."
                            sudo cp $BACKUP_DIR/last-stable.war /var/lib/tomcat9/webapps/NumberGuessGame.war || true
                            sudo chown tomcat:tomcat /var/lib/tomcat9/webapps/NumberGuessGame.war || true

                            echo "Starting Tomcat..."
                            sudo systemctl start tomcat9
                        else
                            echo "⚠️ No last-stable.war found. Cannot rollback."
                        fi
                    '''
                    slackSend botUser: true, channel: '#number-guess-game-ci-cd-build-alert',
                        color: 'warning',
                        message: "🔄 Rollback attempted for `${env.BRANCH_NAME}` after failed deploy (check logs)."
                } catch (err) {
                    slackSend botUser: true, channel: '#number-guess-game-ci-cd-build-alert',
                        color: 'danger',
                        message: "⚠️ Rollback FAILED for `${env.BRANCH_NAME}`. Manual intervention required!"
                    error("Rollback failed: ${err}")
                }
            }
        }
    }
}
}
