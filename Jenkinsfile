pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \
                              -Dsonar.projectKey=NumberGuessGame \
                              -Dsonar.login=$SONAR_TOKEN
                        """
                    }
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sshagent(['tomcat-deploy-key']) {
                    sh """
                        echo "Deploying application to Tomcat..."
                        
                        # Accept host key (safely)
                        mkdir -p ~/.ssh
                        ssh-keyscan -H 44.192.25.13 >> ~/.ssh/known_hosts 2>/dev/null
                        
                        # Deploy WAR file
                        scp target/NumberGuessGame-1.0-SNAPSHOT.war ec2-user@44.192.25.13:/tmp/
                        
                        # Restart Tomcat
                        ssh ec2-user@44.192.25.13 '
                            echo "Copying WAR file to Tomcat webapps..."
                            sudo cp /tmp/NumberGuessGame-1.0-SNAPSHOT.war /opt/tomcat/webapps/
                            
                            echo "Restarting Tomcat service..."
                            sudo systemctl restart tomcat
                            
                            # Wait for deployment
                            sleep 5
                            
                            # Verify deployment
                            if sudo ls /opt/tomcat/webapps/NumberGuessGame-1.0-SNAPSHOT.war > /dev/null 2>&1; then
                                echo "✅ Deployment successful!"
                            else
                                echo "❌ Deployment failed - WAR file not found!"
                                exit 1
                            fi
                        '
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'CI/CD Pipeline completed successfully. Application deployed to Tomcat.'
            echo "Access application at: http://44.192.25.13:8080/NumberGuessGame-1.0-SNAPSHOT/"
        }
        failure {
            echo 'Pipeline failed. Check Jenkins logs.'
        }
        always {
            echo 'Pipeline execution completed.'
        }
    }
}