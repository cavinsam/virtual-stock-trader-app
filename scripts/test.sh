#!/bin/bash

# Virtual Stock Trading App Testing Script
# Usage: ./scripts/test.sh [test-type]

set -e

# Configuration
TEST_TYPE=${1:-all}
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

# Run unit tests
run_unit_tests() {
    log_info "Running unit tests..."
    
    # Backend unit tests
    log_info "Running backend unit tests..."
    cd backend/virtual-stock-trader
    mvn clean test -Dspring.profiles.active=test
    cd ../..
    
    # Frontend unit tests
    log_info "Running frontend unit tests..."
    cd frontend/virtual-stock-trader/frontend
    npm test -- --coverage --watchAll=false
    cd ../../..
    
    log_success "Unit tests completed"
}

# Run integration tests
run_integration_tests() {
    log_info "Running integration tests..."
    
    # Start test environment
    log_info "Starting test environment..."
    docker-compose -f docker-compose.ci.yml up -d
    
    # Wait for services to be ready
    log_info "Waiting for services to be ready..."
    sleep 30
    
    # Run integration tests
    log_info "Running integration test suite..."
    
    # Test backend health
    log_info "Testing backend health endpoint..."
    if curl -f http://localhost:8081/api/auth/test; then
        log_success "Backend health check passed"
    else
        log_error "Backend health check failed"
        exit 1
    fi
    
    # Test frontend
    log_info "Testing frontend..."
    if curl -f http://localhost:3000; then
        log_success "Frontend health check passed"
    else
        log_error "Frontend health check failed"
        exit 1
    fi
    
    # Test API proxy
    log_info "Testing API proxy..."
    if curl -f http://localhost:3000/api/auth/test; then
        log_success "API proxy test passed"
    else
        log_error "API proxy test failed"
        exit 1
    fi
    
    # Test signup endpoint
    log_info "Testing signup endpoint..."
    if curl -X POST http://localhost:3000/api/auth/signup \
        -H "Content-Type: application/json" \
        -d '{"username":"testuser","email":"test@example.com","password":"password123"}'; then
        log_success "Signup endpoint test passed"
    else
        log_error "Signup endpoint test failed"
        exit 1
    fi
    
    # Test login endpoint
    log_info "Testing login endpoint..."
    if curl -X POST http://localhost:3000/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"testuser","password":"password123"}'; then
        log_success "Login endpoint test passed"
    else
        log_error "Login endpoint test failed"
        exit 1
    fi
    
    # Cleanup
    log_info "Cleaning up test environment..."
    docker-compose -f docker-compose.ci.yml down -v
    
    log_success "Integration tests completed"
}

# Run performance tests
run_performance_tests() {
    log_info "Running performance tests..."
    
    # Start test environment
    log_info "Starting test environment..."
    docker-compose -f docker-compose.ci.yml up -d
    
    # Wait for services to be ready
    log_info "Waiting for services to be ready..."
    sleep 30
    
    # Run load tests (if you have a load testing tool)
    log_info "Running load tests..."
    
    # Example using Apache Bench (if available)
    if command -v ab &> /dev/null; then
        log_info "Running Apache Bench tests..."
        ab -n 100 -c 10 http://localhost:3000/
        ab -n 100 -c 10 http://localhost:8081/api/auth/test
    else
        log_warning "Apache Bench not found, skipping load tests"
    fi
    
    # Cleanup
    log_info "Cleaning up test environment..."
    docker-compose -f docker-compose.ci.yml down -v
    
    log_success "Performance tests completed"
}

# Run security tests
run_security_tests() {
    log_info "Running security tests..."
    
    # Check for common security issues
    log_info "Checking for security vulnerabilities..."
    
    # Backend security scan
    cd backend/virtual-stock-trader
    if command -v mvn &> /dev/null; then
        mvn org.owasp:dependency-check-maven:check
    else
        log_warning "Maven not found, skipping dependency check"
    fi
    cd ../..
    
    # Frontend security scan
    cd frontend/virtual-stock-trader/frontend
    if command -v npm &> /dev/null; then
        npm audit
    else
        log_warning "npm not found, skipping audit"
    fi
    cd ../../..
    
    log_success "Security tests completed"
}

# Run all tests
run_all_tests() {
    log_info "Running all tests..."
    
    run_unit_tests
    run_integration_tests
    run_performance_tests
    run_security_tests
    
    log_success "All tests completed successfully!"
}

# Main function
main() {
    log_info "Starting test suite: ${TEST_TYPE}"
    
    case $TEST_TYPE in
        "unit")
            run_unit_tests
            ;;
        "integration")
            run_integration_tests
            ;;
        "performance")
            run_performance_tests
            ;;
        "security")
            run_security_tests
            ;;
        "all")
            run_all_tests
            ;;
        *)
            log_error "Unknown test type: ${TEST_TYPE}"
            log_info "Available test types: unit, integration, performance, security, all"
            exit 1
            ;;
    esac
    
    log_success "Test suite completed successfully!"
}

# Run main function
main "$@"
