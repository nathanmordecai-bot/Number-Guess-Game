pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        SONARQUBE_ENV = 'SonarQubeServer'
        NEXUS_URL = '34.228.143.158:8081'
        NEXUS_REPO = 'maven-releases'
        NEXUS_CREDENTIALS = 'nexus-creds'
        TOMCAT_SERVER = 'tomcat-ssh'
        ARTIFACT_VERSION = '1.0.0'
        ARTIFACT_ID = 'NumberGuessGame'
        WAR_FILE = "target/${ARTIFACT_ID}-1.0-SNAPSHOT.war"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/nathanmordecai-bot/Number-Guess-Game.git'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
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

        stage('Build WAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Upload to Nexus') {
            steps {
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: "${NEXUS_URL}",
                    groupId: 'com.studentapp',
                    version: "${ARTIFACT_VERSION}",
                    repository: "${NEXUS_REPO}",
                    credentialsId: "${NEXUS_CREDENTIALS}",
                    artifacts: [
                        [
                            artifactId: "${ARTIFACT_ID}",
                            classifier: '',
                            file: "${WAR_FILE}",
                            type: 'war'
                        ]
                    ]
                )
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: "${TOMCAT_SERVER}",
                            transfers: [
                                sshTransfer(
                                    sourceFiles: "${WAR_FILE}",
                                    remoteDirectory: "/opt/tomcat/webapps",
                                    removePrefix: "target"
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
