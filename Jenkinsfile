pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
        // SonarQube scanner is configured under "Global Tool Configuration"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'sonar-scanner'
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        ${scannerHome}/bin/sonar-scanner \
                        -Dsonar.projectKey=myproject \
                        -Dsonar.sources=src \
                        -Dsonar.java.binaries=target
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Build, test, packaging, and SonarQube analysis completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check logs.'
        }
    }
}
