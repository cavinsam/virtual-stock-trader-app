# CI/CD Setup Guide for Virtual Stock Trading App

This guide will help you set up both GitHub Actions and Jenkins CI/CD pipelines for your virtual stock trading application.

## Table of Contents

1. [GitHub Actions Setup](#github-actions-setup)
2. [Jenkins Setup](#jenkins-setup)
3. [Docker Hub Configuration](#docker-hub-configuration)
4. [Environment Variables](#environment-variables)
5. [Testing](#testing)
6. [Deployment](#deployment)
7. [Troubleshooting](#troubleshooting)

## GitHub Actions Setup

### Prerequisites

1. GitHub repository with your code
2. Docker Hub account
3. GitHub repository secrets configured

### Step 1: Configure GitHub Secrets

Go to your GitHub repository → Settings → Secrets and variables → Actions, and add the following secrets:

```
DOCKER_USERNAME=your_docker_hub_username
DOCKER_PASSWORD=your_docker_hub_password_or_token
```

### Step 2: Enable GitHub Actions

1. The workflow file is already created at `.github/workflows/ci-cd.yml`
2. Push your code to trigger the workflow
3. Go to Actions tab in your GitHub repository to monitor the pipeline

### Step 3: Workflow Features

The GitHub Actions workflow includes:

- **Test Stage**: Runs unit tests for both backend and frontend
- **Build Stage**: Builds and pushes Docker images to Docker Hub
- **Deploy Stage**: Deploys to staging environment (only on main branch)

## Jenkins Setup

### Prerequisites

1. Jenkins server running on localhost:8080
2. Docker installed on Jenkins server
3. Docker Hub credentials configured in Jenkins

### Step 1: Install Required Jenkins Plugins

Install the following plugins in Jenkins:

- Docker Pipeline
- Docker
- GitHub Integration
- Blue Ocean (optional, for better UI)
- HTML Publisher (for test reports)
- Coverage (for code coverage)

### Step 2: Configure Docker Hub Credentials

1. Go to Jenkins → Manage Jenkins → Manage Credentials
2. Add new credentials:
   - Kind: Username with password
   - ID: `docker-hub-credentials`
   - Username: Your Docker Hub username
   - Password: Your Docker Hub password/token

### Step 3: Create Jenkins Pipeline

1. Go to Jenkins → New Item
2. Choose "Pipeline"
3. Name it "virtual-stock-trading-app"
4. In Pipeline section:
   - Definition: Pipeline script from SCM
   - SCM: Git
   - Repository URL: Your GitHub repository URL
   - Script Path: Jenkinsfile

### Step 4: Configure Pipeline

The Jenkinsfile includes:

- **Checkout**: Gets the latest code
- **Backend Tests**: Runs Maven tests with MySQL
- **Frontend Tests**: Runs npm tests and builds
- **Build Images**: Builds and pushes Docker images
- **Integration Tests**: Tests the full application stack
- **Deploy**: Deploys to staging/production

## Docker Hub Configuration

### Step 1: Create Docker Hub Repositories

Create two repositories in Docker Hub:

1. `cavinsam/virtual-stock-trading-app-backend`
2. `cavinsam/virtual-stock-trading-app-frontend`

### Step 2: Configure Access Tokens

For better security, use Docker Hub access tokens instead of passwords:

1. Go to Docker Hub → Account Settings → Security
2. Create new access token
3. Use this token in your CI/CD configurations

## Environment Variables

### Required Environment Variables

```bash
# Database
MYSQL_ROOT_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_jwt_secret_key

# API Keys
ALPHAVANTAGE_API_KEY=your_alphavantage_api_key
```

### Setting Environment Variables

#### GitHub Actions
Add secrets in GitHub repository settings.

#### Jenkins
Add environment variables in Jenkins job configuration or use the Credentials plugin.

## Testing

### Running Tests Locally

#### Unit Tests
```bash
# Backend tests
cd backend/virtual-stock-trader
mvn test

# Frontend tests
cd frontend/virtual-stock-trader/frontend
npm test
```

#### Integration Tests
```bash
# Using the test script
./scripts/test.sh integration

# Or manually
docker-compose -f docker-compose.ci.yml up -d
# Wait for services to start
curl http://localhost:3000/api/auth/test
docker-compose -f docker-compose.ci.yml down -v
```

### Test Types

1. **Unit Tests**: Individual component testing
2. **Integration Tests**: Full application stack testing
3. **Performance Tests**: Load and stress testing
4. **Security Tests**: Vulnerability scanning

## Deployment

### Using Deployment Scripts

#### Linux/Mac
```bash
# Deploy to staging
./scripts/deploy.sh staging latest

# Deploy to production
./scripts/deploy.sh production v1.0.0
```

#### Windows PowerShell
```powershell
# Deploy to staging
.\scripts\deploy.ps1 staging latest

# Deploy to production
.\scripts\deploy.ps1 production v1.0.0
```

### Manual Deployment

#### Staging Environment
```bash
docker-compose -f docker-compose.staging.yml up -d
```

#### Production Environment
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Monitoring and Logs

### Viewing Logs

#### Docker Compose
```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs backend
docker-compose logs frontend
```

#### Individual Containers
```bash
# View container logs
docker logs vst-backend-app
docker logs vst-frontend-app
```

### Health Checks

The application includes health check endpoints:

- Backend: `http://localhost:8081/api/auth/test`
- Frontend: `http://localhost:3000`
- API Proxy: `http://localhost:3000/api/auth/test`

## Troubleshooting

### Common Issues

#### 1. Docker Build Failures

**Problem**: Docker build fails during CI/CD
**Solution**: 
- Check Dockerfile syntax
- Ensure all dependencies are available
- Verify build context includes necessary files

#### 2. Database Connection Issues

**Problem**: Backend can't connect to database
**Solution**:
- Check database container is running
- Verify connection string
- Ensure database is fully initialized

#### 3. Port Conflicts

**Problem**: Port already in use
**Solution**:
- Change ports in docker-compose files
- Stop conflicting services
- Use different port mappings

#### 4. Authentication Failures

**Problem**: Docker Hub push fails
**Solution**:
- Verify credentials are correct
- Check if using access token instead of password
- Ensure repository permissions are set

### Debug Commands

```bash
# Check running containers
docker ps

# Check container logs
docker logs <container_name>

# Check network connectivity
docker network ls
docker network inspect <network_name>

# Check volumes
docker volume ls
docker volume inspect <volume_name>
```

### Getting Help

1. Check the logs first
2. Verify environment variables
3. Test components individually
4. Check Docker and Docker Compose versions
5. Review CI/CD pipeline logs

## Best Practices

### Security

1. Use environment variables for secrets
2. Don't commit passwords to version control
3. Use Docker Hub access tokens
4. Regularly update base images
5. Scan for vulnerabilities

### Performance

1. Use multi-stage Docker builds
2. Cache dependencies in CI/CD
3. Use .dockerignore files
4. Optimize image sizes
5. Use health checks

### Monitoring

1. Set up log aggregation
2. Monitor resource usage
3. Set up alerts for failures
4. Track deployment metrics
5. Monitor application performance

## Next Steps

1. Set up monitoring and alerting
2. Configure automated security scanning
3. Implement blue-green deployments
4. Set up performance monitoring
5. Configure backup strategies
