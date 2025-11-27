# Contributing to Virtual Stock Trader

Thank you for your interest in contributing to the Virtual Stock Trader application! This document provides guidelines and instructions for developers.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Local Development](#local-development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Git Workflow](#git-workflow)
- [Code Style](#code-style)
- [Security](#security)

## Getting Started

### Prerequisites

- **Java 17+** (for backend development)
- **Node.js 20+** (for frontend development)
- **Docker & Docker Compose** (for containerized development)
- **Git** (for version control)
- **Maven 3.8+** (for Java dependency management)
- **npm or yarn** (for JavaScript package management)

### Clone the Repository

```bash
git clone https://github.com/cavinsam/virtual-stock-trader-app.git
cd virtual-stock-trader-app
```

## Development Setup

### Backend Setup (Java/Spring Boot)

1. **Install Dependencies**

```bash
cd backend/virtual-stock-trader
mvn clean install
```

2. **Configure Application Properties**

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8081
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/virtual_stock_trader
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update

  # JWT Configuration
  security:
    jwt:
      secret: your_jwt_secret_key_change_me
      expiration: 86400000  # 24 hours in ms
```

3. **Start the Backend**

```bash
# Using Maven
mvn spring-boot:run

# Or using IDE (IntelliJ, VS Code, Eclipse)
```

Backend will be available at: `http://localhost:8081`

### Frontend Setup (React/Vite)

1. **Install Dependencies**

```bash
cd frontend/virtual-stock-trader/frontend
npm install
# or
yarn install
```

2. **Configure API Endpoint**

Create `.env.local`:

```
VITE_API_URL=http://localhost:8081
```

3. **Start Development Server**

```bash
npm run dev
# or
yarn dev
```

Frontend will be available at: `http://localhost:5173`

### Database Setup (MySQL)

#### Option 1: Using Docker Compose

```bash
# From project root
docker-compose up -d
```

MySQL will be available at `localhost:3306` with:
- Username: `root`
- Password: `vst_prod_password_change_me` (see docker-compose.yml)
- Database: `virtual_stock_trader`

#### Option 2: Manual Installation

```bash
# Create database
mysql -u root -p -e "CREATE DATABASE virtual_stock_trader;"

# Tables will be created automatically by Hibernate (ddl-auto: update)
```

## Local Development

### Running Full Stack

#### Using Docker Compose (Recommended)

```bash
# Start all services
docker-compose up

# Or in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### Using IDE + Docker

```bash
# Start only MySQL
docker-compose up mysql

# Then start backend in IDE
# And start frontend from command line
npm run dev
```

### Development Workflow

1. **Create a feature branch**

```bash
git checkout -b feature/your-feature-name
```

2. **Make your changes**

3. **Test your changes** (see [Testing](#testing) section)

4. **Commit with clear messages**

```bash
git add .
git commit -m "feat: add new feature description"
```

5. **Push to your fork and create a Pull Request**

## Testing

### Backend Testing

1. **Run All Tests**

```bash
cd backend/virtual-stock-trader
mvn test
```

2. **Run Specific Test**

```bash
mvn test -Dtest=PortfolioControllerTest
```

3. **Run with Coverage**

```bash
mvn clean test jacoco:report
# Coverage report: target/site/jacoco/index.html
```

### Frontend Testing

1. **Run All Tests**

```bash
cd frontend/virtual-stock-trader/frontend
npm run test
```

2. **Run Tests in Watch Mode**

```bash
npm run test:watch
```

3. **Generate Coverage Report**

```bash
npm run test:coverage
```

### Integration Testing

Test the full stack locally:

```bash
# Ensure all services are running
docker-compose up

# Run health check
curl http://localhost:8081/health

# Test API endpoints
curl -X GET http://localhost:8081/api/portfolio \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Test frontend access
open http://localhost:3000
```

## Deployment

### Local Deployment with Ansible

1. **Install Ansible**

```bash
pip install ansible>=2.9
```

2. **Deploy to Kubernetes (Docker Desktop)**

```bash
cd ansible
make init
make deploy DEPLOYMENT_METHOD=kubernetes
```

3. **Deploy with Docker Compose**

```bash
cd ansible
make deploy DEPLOYMENT_METHOD=docker_compose
```

See [ansible/README.md](ansible/README.md) for detailed deployment instructions.

### Manual Kubernetes Deployment

```bash
# Apply Kubernetes manifests
kubectl apply -k k8s/

# Verify deployment
kubectl get pods -n virtual-stock-trader
kubectl get services -n virtual-stock-trader

# View logs
kubectl logs -n virtual-stock-trader -f deployment/backend
```

### Manual Docker Compose Deployment

```bash
# Build images
docker-compose build

# Start services
docker-compose up -d

# Verify services
docker-compose ps
```

## Git Workflow

### Branch Naming Convention

- `feature/description` - New features
- `bugfix/description` - Bug fixes
- `docs/description` - Documentation updates
- `chore/description` - Maintenance tasks

### Commit Message Format

Follow conventional commits:

```
type(scope): brief description

Longer explanation if needed.
- Point 1
- Point 2

Fixes #123
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation
- `style` - Code style changes
- `refactor` - Code refactoring
- `test` - Test additions/updates
- `chore` - Build/dependency updates

### Pull Request Process

1. Fork the repository
2. Create a feature branch
3. Make commits with clear messages
4. Ensure tests pass and coverage maintained
5. Submit PR with:
   - Clear title and description
   - Reference to related issues
   - Screenshots for UI changes
   - Testing instructions

### Code Review Guidelines

When reviewing pull requests, check:
- Code follows style guide
- Tests are included and passing
- Documentation is updated
- No security vulnerabilities introduced
- Performance impact considered

## Code Style

### Backend (Java)

**Formatting:**
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Use meaningful variable names

**Naming Conventions:**
- Classes: PascalCase
- Methods/Variables: camelCase
- Constants: UPPER_SNAKE_CASE

**Example:**

```java
public class PortfolioService {
    private static final String PORTFOLIO_NOT_FOUND = "Portfolio not found";
    
    public Portfolio getPortfolio(Long portfolioId) {
        // Implementation
    }
}
```

### Frontend (JavaScript/React)

**Formatting:**
- Use 2 spaces for indentation
- Use single quotes for strings
- Use semicolons
- Format with Prettier

**Naming Conventions:**
- Components: PascalCase
- Functions/Variables: camelCase
- Constants: UPPER_SNAKE_CASE

**Example:**

```javascript
export const PortfolioCard = ({ portfolio }) => {
  const [isLoading, setIsLoading] = useState(false);

  const handleRefresh = () => {
    // Implementation
  };

  return <div>{/* JSX */}</div>;
};
```

**ESLint Configuration:**
Run linter: `npm run lint`
Fix issues: `npm run lint:fix`

## Security

### Secret Management

**Never commit secrets!**

1. **GitHub Actions Secrets**

   Set in repository Settings → Secrets:
   - `DOCKERHUB_USERNAME` - Docker Hub username
   - `DOCKERHUB_TOKEN` - Docker Hub access token
   - `KUBECONFIG` - Base64 encoded kubeconfig for K8s deployment

2. **Local Development**

   Use environment variables:
   ```bash
   export MYSQL_ROOT_PASSWORD=your_password
   export JWT_SECRET=your_secret
   export SPRING_DATASOURCE_PASSWORD=your_password
   ```

3. **Ansible Secrets**

   Use Ansible Vault:
   ```bash
   cd ansible
   ansible-vault create vault.yml
   ```

### Authentication & Authorization

- **Backend:** JWT token-based authentication
- **Database:** Use strong passwords (minimum 12 characters)
- **API Endpoints:** Implement role-based access control

### Dependency Security

1. **Check for Vulnerabilities**

   ```bash
   # Backend
   mvn dependency-check:check

   # Frontend
   npm audit
   ```

2. **Update Dependencies Regularly**

   ```bash
   # Backend
   mvn versions:display-dependency-updates

   # Frontend
   npm outdated
   ```

### Code Security

- Input validation on all API endpoints
- SQL injection protection via JPA/Hibernate ORM
- CORS properly configured
- HTTPS enforcement in production
- Rate limiting on sensitive endpoints

## Performance Considerations

### Backend Optimization

- Use database indexes for frequently queried fields
- Implement caching for stock data
- Optimize N+1 queries with eager loading
- Profile performance with Spring Boot actuator

### Frontend Optimization

- Code splitting for lazy loading
- Image optimization and compression
- Bundle size monitoring
- Minimize API calls with proper caching

## Troubleshooting

### Common Issues

**MySQL Connection Error**

```
Error: java.sql.SQLException: Host 'xxx.xxx.xxx.xxx' is not allowed to connect
```

Solution: Update MySQL root host in docker-compose.yml or K8s secret:
```bash
MYSQL_ROOT_HOST=% # Allow all hosts
```

**Frontend Cannot Connect to Backend**

```
Error: Failed to fetch http://localhost:8081/api/...
```

Solution:
- Verify backend is running on port 8081
- Check VITE_API_URL in .env.local
- Check CORS configuration in Spring Boot

**Port Already in Use**

```bash
# Kill process using port
# Linux/macOS
lsof -ti:8081 | xargs kill -9

# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

## Documentation

- Update README.md for significant changes
- Add inline code comments for complex logic
- Update API documentation for new endpoints
- Keep CHANGELOG.md updated

## Questions or Need Help?

- Check existing [GitHub Issues](https://github.com/cavinsam/virtual-stock-trader-app/issues)
- Review documentation in `/docs` directory
- Check the [README](README.md) for project overview
- Check [CI/CD Setup Guide](docs/CI-CD-SETUP.md)

## License

This project is licensed under the MIT License - see LICENSE file for details.

---

Thank you for contributing to Virtual Stock Trader!
