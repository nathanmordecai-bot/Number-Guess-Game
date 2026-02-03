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
                sh """
                    echo "Deploying application to Tomcat..."

                    # Copy WAR to Tomcat server
                    scp target/*.war ec2-user@44.192.25.13:/opt/tomcat/webapps/

                    # Restart Tomcat
                    ssh ec2-user@44.192.25.13 << EOF
                        sudo systemctl restart tomcat || (
                            /opt/tomcat/bin/shutdown.sh
                            /opt/tomcat/bin/startup.sh
                        )
                    EOF
                """
            }
        }
    }

    post {
        success {
            echo 'CI/CD Pipeline completed successfully. Application deployed to Tomcat.'
        }
        failure {
            echo 'Pipeline failed. Check Jenkins logs.'
        }
    }
}
