# Jenkins Setup Guide for Virtual Stock Trading App

This guide provides detailed instructions for setting up Jenkins CI/CD pipeline for your virtual stock trading application.

## Prerequisites

- Jenkins server running on localhost:8080
- Docker installed and running
- Git installed
- Java 17+ installed
- Node.js 20+ installed

## Step 1: Install Required Jenkins Plugins

### Essential Plugins

1. **Docker Pipeline** - For Docker integration
2. **Docker** - Docker plugin
3. **GitHub Integration** - GitHub webhook support
4. **Pipeline** - Pipeline support
5. **Blue Ocean** - Modern UI (optional)
6. **HTML Publisher** - For test reports
7. **Coverage** - Code coverage reports
8. **Credentials Binding** - Secure credential management

### Installation Steps

1. Go to Jenkins → Manage Jenkins → Manage Plugins
2. Click on "Available" tab
3. Search and install the plugins listed above
4. Restart Jenkins after installation

## Step 2: Configure Global Tools

### Java Configuration

1. Go to Jenkins → Manage Jenkins → Global Tool Configuration
2. Under "JDK", click "Add JDK"
3. Name: `JDK-17`
4. JAVA_HOME: `/usr/lib/jvm/java-17-openjdk` (adjust path as needed)

### Maven Configuration

1. Under "Maven", click "Add Maven"
2. Name: `Maven-3.8`
3. Install automatically: Check
4. Version: `3.8.5`

### Node.js Configuration

1. Under "NodeJS", click "Add NodeJS"
2. Name: `NodeJS-20`
3. Install automatically: Check
4. Version: `20.x`

### Docker Configuration

1. Under "Docker", click "Add Docker"
2. Name: `Docker`
3. Install automatically: Check
4. Version: `latest`

## Step 3: Configure Docker Hub Credentials

### Create Credentials

1. Go to Jenkins → Manage Jenkins → Manage Credentials
2. Click on "System" → "Global credentials"
3. Click "Add Credentials"
4. Configure:
   - Kind: `Username with password`
   - Scope: `Global`
   - Username: `your_docker_hub_username`
   - Password: `your_docker_hub_password_or_token`
   - ID: `docker-hub-credentials`
   - Description: `Docker Hub credentials for pushing images`

## Step 4: Configure GitHub Integration

### GitHub Webhook Setup

1. Go to your GitHub repository → Settings → Webhooks
2. Click "Add webhook"
3. Configure:
   - Payload URL: `http://localhost:8080/github-webhook/`
   - Content type: `application/json`
   - Events: `Just the push event`
   - Active: Check

### GitHub Credentials (if needed)

1. Go to Jenkins → Manage Jenkins → Manage Credentials
2. Add new credentials:
   - Kind: `Username with password`
   - Username: `your_github_username`
   - Password: `your_github_token`
   - ID: `github-credentials`

## Step 5: Create Jenkins Pipeline

### Create New Pipeline Job

1. Go to Jenkins → New Item
2. Enter item name: `virtual-stock-trading-app`
3. Select "Pipeline"
4. Click "OK"

### Configure Pipeline

1. **General Settings**:
   - Description: `Virtual Stock Trading App CI/CD Pipeline`
   - GitHub project: Check and enter your repository URL

2. **Build Triggers**:
   - GitHub hook trigger for GITScm polling: Check
   - Poll SCM: `H/5 * * * *` (optional, for backup polling)

3. **Pipeline Configuration**:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: `https://github.com/your-username/your-repo.git`
   - Credentials: Select your GitHub credentials (if private repo)
   - Branch Specifier: `*/main`
   - Script Path: `Jenkinsfile`

4. **Advanced Project Options**:
   - Lightweight checkout: Check (for better performance)

## Step 6: Configure Environment Variables

### Global Environment Variables

1. Go to Jenkins → Manage Jenkins → Configure System
2. Under "Global properties", check "Environment variables"
3. Add the following variables:

```
MYSQL_ROOT_PASSWORD=jenkins_test_password
JWT_SECRET=jenkins_jwt_secret_key
ALPHAVANTAGE_API_KEY=your_alphavantage_api_key
```

### Job-Specific Environment Variables

1. Go to your pipeline job → Configure
2. Under "Build Environment", check "Use secret text(s) or file(s)"
3. Add bindings for sensitive data

## Step 7: Test the Pipeline

### Manual Trigger

1. Go to your pipeline job
2. Click "Build Now"
3. Monitor the build progress in the console output

### Automatic Trigger

1. Push changes to your GitHub repository
2. Check if the webhook triggers the pipeline
3. Monitor the build in Jenkins

## Step 8: Configure Notifications (Optional)

### Email Notifications

1. Install "Email Extension" plugin
2. Configure SMTP settings in Jenkins → Manage Jenkins → Configure System
3. Add email notifications in your pipeline job

### Slack Notifications

1. Install "Slack Notification" plugin
2. Configure Slack integration
3. Add Slack notifications in your pipeline

## Step 9: Set Up Blue Ocean (Optional)

### Install Blue Ocean

1. Install "Blue Ocean" plugin
2. Go to Jenkins → Blue Ocean
3. Create new pipeline from your repository
4. Enjoy the modern UI

## Pipeline Stages Explained

### 1. Checkout Stage
- Gets the latest code from GitHub
- Extracts Git commit information

### 2. Backend Tests Stage
- Starts MySQL container for testing
- Runs Maven unit tests
- Publishes test results and coverage

### 3. Frontend Tests Stage
- Installs npm dependencies
- Runs frontend tests
- Builds the frontend application
- Publishes coverage reports

### 4. Build Docker Images Stage
- Builds backend Docker image
- Builds frontend Docker image
- Pushes images to Docker Hub
- Tags images with build number and commit hash

### 5. Integration Tests Stage
- Starts full application stack
- Runs integration tests
- Tests API endpoints
- Cleans up test environment

### 6. Deploy Stages
- Deploys to staging (automatic on main branch)
- Deploys to production (manual approval required)

## Monitoring and Maintenance

### Viewing Build History

1. Go to your pipeline job
2. Click on "Build History" to see all builds
3. Click on individual builds to see details

### Viewing Test Results

1. Go to a specific build
2. Click on "Test Result" to see test reports
3. Click on "Coverage Report" to see coverage details

### Viewing Console Output

1. Go to a specific build
2. Click on "Console Output" to see detailed logs

### Cleaning Up

The pipeline automatically cleans up:
- Docker images
- Test containers
- Temporary files

## Troubleshooting

### Common Issues

#### 1. Docker Permission Denied

**Problem**: Docker commands fail with permission denied
**Solution**:
```bash
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

#### 2. GitHub Webhook Not Working

**Problem**: Webhooks not triggering builds
**Solution**:
- Check webhook URL is correct
- Verify Jenkins is accessible from GitHub
- Check Jenkins logs for webhook errors

#### 3. Build Failures

**Problem**: Pipeline fails at various stages
**Solution**:
- Check console output for specific errors
- Verify all required tools are installed
- Check environment variables are set correctly

#### 4. Docker Hub Push Failures

**Problem**: Can't push images to Docker Hub
**Solution**:
- Verify Docker Hub credentials are correct
- Check if repository exists in Docker Hub
- Ensure Jenkins has internet access

### Debug Commands

```bash
# Check Jenkins logs
sudo tail -f /var/log/jenkins/jenkins.log

# Check Docker status
docker info

# Check Jenkins user permissions
id jenkins

# Check available plugins
jenkins-plugin-cli --list
```

### Getting Help

1. Check Jenkins system logs
2. Review pipeline console output
3. Verify all prerequisites are met
4. Check plugin compatibility
5. Review Jenkins documentation

## Best Practices

### Security

1. Use credentials plugin for sensitive data
2. Limit Jenkins user permissions
3. Regularly update Jenkins and plugins
4. Use HTTPS for webhooks
5. Implement proper access controls

### Performance

1. Use lightweight Docker images
2. Cache dependencies where possible
3. Parallelize build stages
4. Clean up resources after builds
5. Monitor resource usage

### Maintenance

1. Regularly update base images
2. Monitor build times
3. Clean up old builds
4. Update plugins regularly
5. Backup Jenkins configuration

## Next Steps

1. Set up monitoring and alerting
2. Configure automated security scanning
3. Implement deployment strategies
4. Set up performance monitoring
5. Configure backup and recovery
