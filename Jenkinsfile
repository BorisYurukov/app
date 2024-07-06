pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'weare-app'
        DOCKER_IMAGE = 'dadockerman/weare-app'
        DOCKER_REGISTRY = 'https://index.docker.io/v1/'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/BorisYurukov/app.git'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_ID}")
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry(DOCKER_REGISTRY, DOCKER_CREDENTIALS_ID) {
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_ID}").push()
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_ID}").push('latest')
                    }
                }
            }
        }
        
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    sh 'docker-compose -f ../app/docker-compose.yml down || true'
                    sh 'docker-compose -f ../app/docker-compose.yml up -d'
                }
            }
        }
    }
    
    post {
        always {
            sh 'docker rmi $(docker images -f "dangling=true" -q) || true'
        }
    }
}
