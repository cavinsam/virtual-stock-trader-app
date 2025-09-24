pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'docker.io'
        BACKEND_IMAGE = 'cavinsam/virtual-stock-trading-app-backend'
        FRONTEND_IMAGE = 'cavinsam/virtual-stock-trading-app-frontend'
        DOCKER_CREDENTIALS = credentials('docker-hub-credentials')
        MYSQL_ROOT_PASSWORD = 'jenkins_test_password'
        JWT_SECRET = 'jenkins_jwt_secret'
        ALPHAVANTAGE_API_KEY = 'jenkins_test_api_key'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    env.BUILD_TAG = "${env.BUILD_NUMBER}-${env.GIT_COMMIT_SHORT}"
                }
            }
        }
        
        stage('Backend Tests') {
            steps {
                script {
                    docker.image('mysql:8.0').withRun(
                        "-e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} " +
                        "-e MYSQL_DATABASE=virtual_stock_trader_test " +
                        "-p 3306:3306"
                    ) { c ->
                        sh '''
                            # Wait for MySQL to be ready
                            while ! docker exec ${c.id} mysqladmin ping -h"localhost" --silent; do
                                echo "Waiting for MySQL..."
                                sleep 2
                            done
                            
                            # Run backend tests
                            cd backend/virtual-stock-trader
                            mvn clean test \
                                -Dspring.datasource.url=jdbc:mysql://localhost:3306/virtual_stock_trader_test \
                                -Dspring.datasource.username=root \
                                -Dspring.datasource.password=${MYSQL_ROOT_PASSWORD} \
                                -DJWT_SECRET=${JWT_SECRET} \
                                -DALPHAVANTAGE_API_KEY=${ALPHAVANTAGE_API_KEY}
                        '''
                    }
                }
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'backend/virtual-stock-trader/target/surefire-reports/*.xml'
                    publishCoverage adapters: [
                        jacocoAdapter('backend/virtual-stock-trader/target/site/jacoco/jacoco.xml')
                    ], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                }
            }
        }
        
        stage('Frontend Tests') {
            steps {
                sh '''
                    cd frontend/virtual-stock-trader/frontend
                    npm ci
                    npm test -- --coverage --watchAll=false
                    npm run build
                '''
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'frontend/virtual-stock-trader/frontend/coverage/lcov-report',
                        reportFiles: 'index.html',
                        reportName: 'Frontend Coverage Report'
                    ])
                }
            }
        }
        
        stage('Build Docker Images') {
            parallel {
                stage('Build Backend Image') {
                    steps {
                        script {
                            def backendImage = docker.build(
                                "${BACKEND_IMAGE}:${BUILD_TAG}",
                                "--build-arg BUILD_NUMBER=${BUILD_NUMBER} " +
                                "--build-arg GIT_COMMIT=${GIT_COMMIT_SHORT} " +
                                "./backend/virtual-stock-trader"
                            )
                            docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-hub-credentials') {
                                backendImage.push("${BUILD_TAG}")
                                if (env.BRANCH_NAME == 'main') {
                                    backendImage.push('latest')
                                }
                            }
                        }
                    }
                }
                
                stage('Build Frontend Image') {
                    steps {
                        script {
                            def frontendImage = docker.build(
                                "${FRONTEND_IMAGE}:${BUILD_TAG}",
                                "--build-arg BUILD_NUMBER=${BUILD_NUMBER} " +
                                "--build-arg GIT_COMMIT=${GIT_COMMIT_SHORT} " +
                                "./frontend/virtual-stock-trader/frontend"
                            )
                            docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-hub-credentials') {
                                frontendImage.push("${BUILD_TAG}")
                                if (env.BRANCH_NAME == 'main') {
                                    frontendImage.push('latest')
                                }
                            }
                        }
                    }
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                script {
                    // Create docker-compose file for testing
                    writeFile file: 'docker-compose.test.yml', text: '''
version: '3.8'
services:
  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: virtual_stock_trader
    ports:
      - "3307:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  backend:
    image: ${BACKEND_IMAGE}:${BUILD_TAG}
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "8083:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/virtual_stock_trader
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
      - ALPHAVANTAGE_API_KEY=${ALPHAVANTAGE_API_KEY}

  frontend:
    image: ${FRONTEND_IMAGE}:${BUILD_TAG}
    depends_on:
      - backend
    ports:
      - "3001:80"
'''
                    
                    sh '''
                        # Start the application stack
                        docker-compose -f docker-compose.test.yml up -d
                        
                        # Wait for services to be ready
                        sleep 30
                        
                        # Run integration tests
                        echo "🧪 Running integration tests..."
                        
                        # Test backend health
                        curl -f http://localhost:8083/api/auth/test || exit 1
                        
                        # Test frontend
                        curl -f http://localhost:3001 || exit 1
                        
                        # Test API proxy
                        curl -f http://localhost:3001/api/auth/test || exit 1
                        
                        echo "✅ All integration tests passed!"
                    '''
                }
            }
            post {
                always {
                    sh 'docker-compose -f docker-compose.test.yml down -v'
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "🚀 Deploying to staging environment..."
                    // Add your deployment logic here
                    // For example: kubectl apply -f k8s/ or docker-compose up -d
                    
                    // Example deployment command
                    sh '''
                        echo "Deploying version ${BUILD_TAG} to staging..."
                        # kubectl set image deployment/vst-backend backend=${BACKEND_IMAGE}:${BUILD_TAG}
                        # kubectl set image deployment/vst-frontend frontend=${FRONTEND_IMAGE}:${BUILD_TAG}
                        # kubectl rollout status deployment/vst-backend
                        # kubectl rollout status deployment/vst-frontend
                    '''
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
                input message: 'Deploy to production?', ok: 'Deploy'
            }
            steps {
                script {
                    echo "🚀 Deploying to production environment..."
                    // Add your production deployment logic here
                    sh '''
                        echo "Deploying version ${BUILD_TAG} to production..."
                        # Add production deployment commands
                    '''
                }
            }
        }
    }
    
    post {
        always {
            // Clean up Docker images
            sh '''
                docker image prune -f
                docker system prune -f
            '''
        }
        success {
            script {
                if (env.BRANCH_NAME == 'main') {
                    // Send success notification
                    echo "✅ Pipeline completed successfully!"
                    // Add notification logic here (Slack, email, etc.)
                }
            }
        }
        failure {
            // Send failure notification
            echo "❌ Pipeline failed!"
            // Add failure notification logic here
        }
        unstable {
            echo "⚠️ Pipeline completed with warnings!"
        }
    }
}
