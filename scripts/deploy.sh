#!/bin/bash

# Virtual Stock Trading App Deployment Script
# Usage: ./scripts/deploy.sh [environment] [version]

set -e

# Configuration
ENVIRONMENT=${1:-staging}
VERSION=${2:-latest}
PROJECT_NAME="virtual-stock-trading-app"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
check_docker() {
    log_info "Checking Docker status..."
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    log_success "Docker is running"
}

# Pull latest images
pull_images() {
    log_info "Pulling latest images..."
    docker pull cavinsam/virtual-stock-trading-app-backend:${VERSION}
    docker pull cavinsam/virtual-stock-trading-app-frontend:${VERSION}
    log_success "Images pulled successfully"
}

# Create environment-specific docker-compose file
create_compose_file() {
    log_info "Creating docker-compose file for ${ENVIRONMENT}..."
    
    case $ENVIRONMENT in
        "staging")
            COMPOSE_FILE="docker-compose.staging.yml"
            MYSQL_PORT="3307"
            BACKEND_PORT="8082"
            FRONTEND_PORT="3000"
            ;;
        "production")
            COMPOSE_FILE="docker-compose.prod.yml"
            MYSQL_PORT="3306"
            BACKEND_PORT="8081"
            FRONTEND_PORT="80"
            ;;
        *)
            log_error "Unknown environment: ${ENVIRONMENT}"
            exit 1
            ;;
    esac
    
    cat > ${COMPOSE_FILE} << EOF
version: '3.8'

services:
  db:
    image: mysql:8.0
    container_name: vst-mysql-${ENVIRONMENT}
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: \${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: virtual_stock_trader
    ports:
      - "${MYSQL_PORT}:3306"
    volumes:
      - mysql-${ENVIRONMENT}-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  backend:
    image: cavinsam/virtual-stock-trading-app-backend:${VERSION}
    container_name: vst-backend-${ENVIRONMENT}
    restart: unless-stopped
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "${BACKEND_PORT}:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/virtual_stock_trader
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=\${MYSQL_ROOT_PASSWORD}
      - JWT_SECRET=\${JWT_SECRET}
      - ALPHAVANTAGE_API_KEY=\${ALPHAVANTAGE_API_KEY}

  frontend:
    image: cavinsam/virtual-stock-trading-app-frontend:${VERSION}
    container_name: vst-frontend-${ENVIRONMENT}
    restart: unless-stopped
    depends_on:
      - backend
    ports:
      - "${FRONTEND_PORT}:80"

volumes:
  mysql-${ENVIRONMENT}-data:
EOF
    
    log_success "Created ${COMPOSE_FILE}"
}

# Create environment file
create_env_file() {
    log_info "Creating environment file..."
    
    ENV_FILE=".env.${ENVIRONMENT}"
    
    if [ ! -f "${ENV_FILE}" ]; then
        cat > ${ENV_FILE} << EOF
# ${ENVIRONMENT^} Environment Configuration
MYSQL_ROOT_PASSWORD=change_this_password_${ENVIRONMENT}
JWT_SECRET=change_this_jwt_secret_${ENVIRONMENT}
ALPHAVANTAGE_API_KEY=your_alphavantage_api_key_here
EOF
        log_warning "Created ${ENV_FILE}. Please update the values before deployment."
    else
        log_info "Using existing ${ENV_FILE}"
    fi
}

# Deploy the application
deploy() {
    log_info "Deploying to ${ENVIRONMENT} environment..."
    
    # Stop existing containers
    log_info "Stopping existing containers..."
    docker-compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} down || true
    
    # Start new containers
    log_info "Starting new containers..."
    docker-compose -f ${COMPOSE_FILE} --env-file ${ENV_FILE} up -d
    
    # Wait for services to be ready
    log_info "Waiting for services to be ready..."
    sleep 30
    
    # Health check
    log_info "Performing health checks..."
    
    case $ENVIRONMENT in
        "staging")
            BACKEND_URL="http://localhost:8082"
            FRONTEND_URL="http://localhost:3000"
            ;;
        "production")
            BACKEND_URL="http://localhost:8081"
            FRONTEND_URL="http://localhost:80"
            ;;
    esac
    
    # Check backend
    if curl -f "${BACKEND_URL}/api/auth/test" > /dev/null 2>&1; then
        log_success "Backend is healthy"
    else
        log_error "Backend health check failed"
        exit 1
    fi
    
    # Check frontend
    if curl -f "${FRONTEND_URL}" > /dev/null 2>&1; then
        log_success "Frontend is healthy"
    else
        log_error "Frontend health check failed"
        exit 1
    fi
    
    # Check API proxy
    if curl -f "${FRONTEND_URL}/api/auth/test" > /dev/null 2>&1; then
        log_success "API proxy is working"
    else
        log_error "API proxy health check failed"
        exit 1
    fi
}

# Main deployment function
main() {
    log_info "Starting deployment to ${ENVIRONMENT} environment with version ${VERSION}"
    
    check_docker
    pull_images
    create_compose_file
    create_env_file
    deploy
    
    log_success "Deployment completed successfully!"
    log_info "Application is available at:"
    
    case $ENVIRONMENT in
        "staging")
            echo "  Frontend: http://localhost:3000"
            echo "  Backend:  http://localhost:8082"
            echo "  Database: localhost:3307"
            ;;
        "production")
            echo "  Frontend: http://localhost:80"
            echo "  Backend:  http://localhost:8081"
            echo "  Database: localhost:3306"
            ;;
    esac
}

# Run main function
main "$@"
