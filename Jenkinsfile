pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        SONARQUBE_ENV = 'SonarQubeServer'   // Name configured in Jenkins
        NEXUS_URL = 'http://34.228.143.158:8081/repository/maven-releases/'
        NEXUS_CREDENTIALS = 'nexus-creds'
        TOMCAT_SERVER = 'tomcat-ssh'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/nathanmordecai-bot/Number-Guess-Game.git'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    sh 'mvn clean verify sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Upload to Nexus') {
            steps {
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: '34.228.143.158:8081',
                    groupId: 'com.studentapp',
                    version: '1.0.0',
                    repository: 'maven-releases',
                    credentialsId: 'nexus-creds',
                    artifacts: [
                        [artifactId: 'NumberGuessGame',
                         classifier: '',
                         file: 'target/NumberGuessGame.war',
                         type: 'war']
                    ]
                )
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: 'tomcat-ssh',
                            transfers: [
                                sshTransfer(
                                    sourceFiles: 'target/NumberGuessGame.war',
                                    remoteDirectory: '/opt/tomcat/webapps',
                                    removePrefix: 'target'
                                )
                            ],
                            usePromotionTimestamp: false
                        )
                    ]
                )
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
}
