pipeline {
    agent any

    environment {
        // Docker Hub credentials ID created in Jenkins
        DOCKER_CREDENTIALS_ID = 'a38e45ae-ac3e-4f85-b281-e70fc8ab758f'
        DOCKER_IMAGE = 'boris.yurukov.a50@learn.telerikacademy.com/weare-app'
        DOCKER_REGISTRY = 'https://index.docker.io/v1/'
    }

    stages {
        stage('Clone Repository') {
            steps {
                // Clone your repository
                git 'https://github.com/BorisYurukov/App.git'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_ID}")
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                script {
                    // Login to Docker Hub
                    docker.withRegistry(DOCKER_REGISTRY, DOCKER_CREDENTIALS_ID) {
                        // Push the Docker image to Docker Hub
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_ID}").push()
                        // Optionally, tag the latest version
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_ID}").push('latest')
                    }
                }
            }
        }
        
        stage('Deploy Docker Container') {
            steps {
                script {
                    // Remove any existing container
                    sh 'docker rm -f weare-app || true'
                    // Run the Docker container
                    sh """
                    docker run -d -p 8081:8081 \
                    --name weare-app \
                    ${DOCKER_IMAGE}:${env.BUILD_ID}
                    """
                }
            }
        }
    }
    
    post {
        always {
            // Clean up Docker images
            sh 'docker rmi $(docker images -f "dangling=true" -q) || true'
        }
    }
}
