pipeline {
    agent any

    triggers {
        githubPush()
    }

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    environment {
        SONARQUBE_ENV = 'SonarQubeServer'
        NEXUS_URL = 'http://34.228.143.158:8081'
        NEXUS_REPO = 'maven-releases'
        NEXUS_CREDENTIALS = 'nexus-creds'
        TOMCAT_SERVER = 'tomcat-ssh'

        ARTIFACT_ID = 'NumberGuessGame'
        ARTIFACT_VERSION = '1.0-SNAPSHOT'
        WAR_FILE = "target/${ARTIFACT_ID}-${ARTIFACT_VERSION}.war"
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/nathanmordecai-bot/Number-Guess-Game.git'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh '''
                        echo "=== SONARQUBE ANALYSIS STARTED ==="
                        echo "Using SonarQube environment: ${SONARQUBE_ENV}"
                        mvn clean compile sonar:sonar \
                          -Dsonar.projectKey=com.studentapp:NumberGuessGame \
                          -Dsonar.projectName=NumberGuessGame
                        echo "=== SONARQUBE ANALYSIS COMPLETED ==="
                    '''
                }
            }
        }

        stage('Debug SonarQube Task') {
            steps {
                script {
                    echo "=== DEBUGGING SONARQUBE TASK ==="
                    echo "1. Checking workspace for SonarQube logs..."
                    sh '''
                        echo "Current directory: $(pwd)"
                        echo "Looking for task ID in logs..."
                        find . -name "*sonar*" -type f | head -10
                        find . -name "*.log" -type f -exec grep -l "sonar\|task" {} \; | head -5
                        echo "--- Build logs ---"
                        ls -la target/*.log 2>/dev/null || echo "No target logs found"
                    '''
                    
                    echo "2. Checking SonarQube server configuration..."
                    echo "SONARQUBE_ENV variable: ${SONARQUBE_ENV}"
                    
                    echo "3. If Quality Gate is stuck, check these manually:"
                    echo "   - SonarQube Server URL in Jenkins: http://13.218.50.185:9000"
                    echo "   - Task status API: curl http://13.218.50.185:9000/api/ce/activity"
                    echo "   - Server status: curl http://13.218.50.185:9000/api/system/status"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    echo "=== QUALITY GATE CHECK ==="
                    echo "Starting Quality Gate check with 15-minute timeout..."
                    
                    try {
                        timeout(time: 15, unit: 'MINUTES') {
                            def qualityGate = waitForQualityGate abortPipeline: true
                            echo "Quality Gate Status: ${qualityGate.status}"
                            if (qualityGate.status == 'OK') {
                                echo "✅ Quality Gate PASSED!"
                            } else {
                                echo "❌ Quality Gate FAILED: ${qualityGate.status}"
                            }
                        }
                    } catch (Exception e) {
                        echo "⚠️ Quality Gate check failed with error: ${e.message}"
                        echo "Possible causes:"
                        echo "1. SonarQube server unreachable"
                        echo "2. Analysis task ID not found"
                        echo "3. Timeout exceeded"
                        echo "4. Authentication issues"
                        throw e
                    }
                }
            }
        }

        stage('Build WAR') {
            steps {
                sh '''
                    echo "=== BUILDING WAR FILE ==="
                    mvn clean package -DskipTests
                    echo "WAR file location: ${WAR_FILE}"
                    ls -la target/*.war 2>/dev/null || echo "No WAR file found!"
                '''
            }
        }

        stage('Upload to Nexus') {
            steps {
                script {
                    echo "=== UPLOADING TO NEXUS ==="
                    echo "Nexus URL: ${NEXUS_URL}"
                    echo "Repository: ${NEXUS_REPO}"
                    echo "WAR File: ${WAR_FILE}"
                    
                    // Check if WAR file exists
                    if (fileExists("${WAR_FILE}")) {
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
                        echo "✅ WAR file uploaded successfully to Nexus"
                    } else {
                        error "❌ WAR file not found: ${WAR_FILE}. Build may have failed."
                    }
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                script {
                    echo "=== DEPLOYING TO TOMCAT ==="
                    echo "Tomcat Server: ${TOMCAT_SERVER}"
                    echo "Remote Directory: /opt/tomcat/webapps"
                    
                    if (fileExists("${WAR_FILE}")) {
                        sshPublisher(
                            publishers: [
                                sshPublisherDesc(
                                    configName: "${TOMCAT_SERVER}",
                                    transfers: [
                                        sshTransfer(
                                            sourceFiles: "${WAR_FILE}",
                                            remoteDirectory: "/opt/tomcat/webapps",
                                            removePrefix: "target",
                                            execCommand: """
                                                echo "Deploying WAR file to Tomcat..."
                                                ls -la /opt/tomcat/webapps/*.war 2>/dev/null | head -5
                                                echo "Restarting Tomcat may be required..."
                                            """
                                        )
                                    ],
                                    usePromotionTimestamp: false
                                )
                            ]
                        )
                        echo "✅ Deployment to Tomcat completed"
                    } else {
                        error "❌ WAR file not found for deployment: ${WAR_FILE}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "=== PIPELINE EXECUTION COMPLETE ==="
            echo "Pipeline status: ${currentBuild.currentResult}"
            
            // Clean up workspace except for important logs
            sh '''
                echo "=== FINAL WORKSPACE STATE ==="
                echo "Top-level files:"
                ls -la | head -20
                echo "---"
                echo "Target directory:"
                ls -la target/ 2>/dev/null | head -20 || echo "No target directory"
            '''
        }
        success {
            echo '✅ Pipeline completed successfully!'
            emailext (
                subject: "Pipeline SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """The pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER} completed successfully.
                
                Build URL: ${env.BUILD_URL}
                SonarQube Analysis: http://13.218.50.185:9000/dashboard?id=com.studentapp:NumberGuessGame
                """,
                to: 'YOUR_EMAIL@example.com'  // Replace with your email
            )
        }
        failure {
            echo '❌ Pipeline failed!'
            script {
                // Capture error details
                def errorLog = sh(script: 'tail -100 /var/log/jenkins/jenkins.log 2>/dev/null || echo "Log not available"', returnStdout: true).trim()
                
                emailext (
                    subject: "Pipeline FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """The pipeline ${env.JOB_NAME} #${env.BUILD_NUMBER} failed.
                    
                    Error Details:
                    ${currentBuild.rawBuild.getLog(100).join('\n')}
                    
                    Last 100 lines of Jenkins log:
                    ${errorLog}
                    
                    Build URL: ${env.BUILD_URL}
                    """,
                    to: 'YOUR_EMAIL@example.com'  // Replace with your email
                )
            }
        }
        unstable {
            echo '⚠️ Pipeline is unstable (tests failing)'
        }
    }
}