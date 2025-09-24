# Virtual Stock Trading App Deployment Script (PowerShell)
# Usage: .\scripts\deploy.ps1 [environment] [version]

param(
    [string]$Environment = "staging",
    [string]$Version = "latest"
)

# Configuration
$ProjectName = "virtual-stock-trading-app"

# Logging functions
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# Check if Docker is running
function Test-Docker {
    Write-Info "Checking Docker status..."
    try {
        docker info | Out-Null
        Write-Success "Docker is running"
    }
    catch {
        Write-Error "Docker is not running. Please start Docker and try again."
        exit 1
    }
}

# Pull latest images
function Invoke-PullImages {
    Write-Info "Pulling latest images..."
    try {
        docker pull "cavinsam/virtual-stock-trading-app-backend:$Version"
        docker pull "cavinsam/virtual-stock-trading-app-frontend:$Version"
        Write-Success "Images pulled successfully"
    }
    catch {
        Write-Error "Failed to pull images"
        exit 1
    }
}

# Create environment-specific docker-compose file
function New-ComposeFile {
    Write-Info "Creating docker-compose file for $Environment..."
    
    $ComposeFile = "docker-compose.$Environment.yml"
    $MysqlPort = if ($Environment -eq "staging") { "3307" } else { "3306" }
    $BackendPort = if ($Environment -eq "staging") { "8082" } else { "8081" }
    $FrontendPort = if ($Environment -eq "staging") { "3000" } else { "80" }
    
    $ComposeContent = @"
version: '3.8'

services:
  db:
    image: mysql:8.0
    container_name: vst-mysql-$Environment
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: `${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: virtual_stock_trader
    ports:
      - "$MysqlPort:3306"
    volumes:
      - mysql-$Environment-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  backend:
    image: cavinsam/virtual-stock-trading-app-backend:$Version
    container_name: vst-backend-$Environment
    restart: unless-stopped
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "$BackendPort:8081"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/virtual_stock_trader
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=`${MYSQL_ROOT_PASSWORD}
      - JWT_SECRET=`${JWT_SECRET}
      - ALPHAVANTAGE_API_KEY=`${ALPHAVANTAGE_API_KEY}

  frontend:
    image: cavinsam/virtual-stock-trading-app-frontend:$Version
    container_name: vst-frontend-$Environment
    restart: unless-stopped
    depends_on:
      - backend
    ports:
      - "$FrontendPort:80"

volumes:
  mysql-$Environment-data:
"@
    
    $ComposeContent | Out-File -FilePath $ComposeFile -Encoding UTF8
    Write-Success "Created $ComposeFile"
}

# Create environment file
function New-EnvFile {
    Write-Info "Creating environment file..."
    
    $EnvFile = ".env.$Environment"
    
    if (-not (Test-Path $EnvFile)) {
        $EnvContent = @"
# $($Environment.ToUpper()) Environment Configuration
MYSQL_ROOT_PASSWORD=change_this_password_$Environment
JWT_SECRET=change_this_jwt_secret_$Environment
ALPHAVANTAGE_API_KEY=your_alphavantage_api_key_here
"@
        $EnvContent | Out-File -FilePath $EnvFile -Encoding UTF8
        Write-Warning "Created $EnvFile. Please update the values before deployment."
    }
    else {
        Write-Info "Using existing $EnvFile"
    }
}

# Deploy the application
function Invoke-Deploy {
    Write-Info "Deploying to $Environment environment..."
    
    $ComposeFile = "docker-compose.$Environment.yml"
    $EnvFile = ".env.$Environment"
    
    # Stop existing containers
    Write-Info "Stopping existing containers..."
    docker-compose -f $ComposeFile --env-file $EnvFile down
    
    # Start new containers
    Write-Info "Starting new containers..."
    docker-compose -f $ComposeFile --env-file $EnvFile up -d
    
    # Wait for services to be ready
    Write-Info "Waiting for services to be ready..."
    Start-Sleep -Seconds 30
    
    # Health check
    Write-Info "Performing health checks..."
    
    $BackendUrl = if ($Environment -eq "staging") { "http://localhost:8082" } else { "http://localhost:8081" }
    $FrontendUrl = if ($Environment -eq "staging") { "http://localhost:3000" } else { "http://localhost:80" }
    
    # Check backend
    try {
        Invoke-WebRequest -Uri "$BackendUrl/api/auth/test" -UseBasicParsing | Out-Null
        Write-Success "Backend is healthy"
    }
    catch {
        Write-Error "Backend health check failed"
        exit 1
    }
    
    # Check frontend
    try {
        Invoke-WebRequest -Uri $FrontendUrl -UseBasicParsing | Out-Null
        Write-Success "Frontend is healthy"
    }
    catch {
        Write-Error "Frontend health check failed"
        exit 1
    }
    
    # Check API proxy
    try {
        Invoke-WebRequest -Uri "$FrontendUrl/api/auth/test" -UseBasicParsing | Out-Null
        Write-Success "API proxy is working"
    }
    catch {
        Write-Error "API proxy health check failed"
        exit 1
    }
}

# Main deployment function
function Start-Deployment {
    Write-Info "Starting deployment to $Environment environment with version $Version"
    
    Test-Docker
    Invoke-PullImages
    New-ComposeFile
    New-EnvFile
    Invoke-Deploy
    
    Write-Success "Deployment completed successfully!"
    Write-Info "Application is available at:"
    
    if ($Environment -eq "staging") {
        Write-Host "  Frontend: http://localhost:3000" -ForegroundColor Cyan
        Write-Host "  Backend:  http://localhost:8082" -ForegroundColor Cyan
        Write-Host "  Database: localhost:3307" -ForegroundColor Cyan
    }
    else {
        Write-Host "  Frontend: http://localhost:80" -ForegroundColor Cyan
        Write-Host "  Backend:  http://localhost:8081" -ForegroundColor Cyan
        Write-Host "  Database: localhost:3306" -ForegroundColor Cyan
    }
}

# Run main function
Start-Deployment
