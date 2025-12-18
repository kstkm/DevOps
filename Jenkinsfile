pipeline {
    agent any

    environment {
        // ЗАМЕНИТЕ на ваш логин на Docker Hub
        DOCKER_HUB_USER = "pacapu"
        IMAGE_NAME = "todo-app"
        VERSION = "1.1.${BUILD_NUMBER}"
        REGISTRY_CREDS = 'docker-hub-creds' // ID ваших credentials в Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                sh "chmod +x mvnw"
                // Собираем проект через Maven Wrapper
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
                // Выполняем анализ (Checkstyle)
                sh "./mvnw checkstyle:checkstyle"
            }
        }

        stage('Prepare and Build Docker Image') {
            steps {
                script {
                    // Выполняем требование ТЗ: JAR должен быть в build/libs/
                    sh "mkdir -p build/libs"
                    sh "cp target/*.jar build/libs/app.jar"

                    // Собираем образ
                    sh "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:${VERSION} -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest -f Docker/Dockerfile ."
                }
            }
        }

        stage('Push to Registry') {
            steps {
                // Безопасное использование логина/пароля от Docker Hub
                withCredentials([usernamePassword(credentialsId: "${REGISTRY_CREDS}", passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                    sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:${VERSION}"
                    sh "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                    sh "docker logout"
                }
            }
        }

        stage('Conditional Deploy') {
            when {
                branch 'main'
            }
            steps {
                echo "=== DEPLOYING VERSION ${VERSION} TO PRODUCTION ==="
                // Здесь можно добавить команду запуска:
                // sh "docker compose -f Docker/docker-composeKostya.yml up -d"
            }
        }
    }

    post {
        always {
            // Архивация артефактов (JAR файла) для истории
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
        success {
            echo "Successfully built and pushed version ${VERSION}"
        }
        failure {
            echo "Pipeline failed! Проверьте логи сборки выше."
        }
    }
}