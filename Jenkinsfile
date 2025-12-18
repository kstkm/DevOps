pipeline {
    agent any

    environment {
        // Динамический тег версии на основе номера сборки Jenkins
        DOCKER_IMAGE = "your-docker-hub-username/todo-app"
        VERSION = "1.1.${BUILD_NUMBER}"
        REGISTRY_CREDS = 'docker-hub-creds'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Используем Maven Wrapper как в задании (аналог gradlew)
                sh "chmod +x mvnw"
                sh "./mvnw clean package -DskipTests"
            }
        }

        stage('Test') {
            steps {
                sh "./mvnw test"
            }
        }

        stage('Static Code Analysis') {
            steps {
                // Выполняется, но не валит билд, если не настроены жесткие пороги
                sh "./mvnw checkstyle:checkstyle"
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Сборка образа с динамическим тегом
                    appImage = docker.build("${DOCKER_IMAGE}:${VERSION}", "-f Docker/Dockerfile .")
                }
            }
        }

        stage('Push to Registry') {
            steps {
                script {
                    docker.withRegistry('', REGISTRY_CREDS) {
                        appImage.push()
                        appImage.push("latest")
                    }
                }
            }
        }

        stage('Conditional Deploy') {
            when {
                branch 'main' // Выполняется только на ветке main
            }
            steps {
                echo "Deploying version ${VERSION} to Production..."
                // Здесь может быть команда docker-compose up или деплой в K8s
            }
        }
    }

    post {
        always {
            // Архивация логов и JAR файла
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
        success {
            echo "Pipeline finished successfully!"
        }
        failure {
            echo "Pipeline failed! Check logs."
            // Можно добавить mail to: 'admin@example.com', subject: "Build Failed: ${env.JOB_NAME}"
        }
    }
}