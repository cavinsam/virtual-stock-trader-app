# Ansible Playbook Documentation

## Overview

This Ansible automation suite provides end-to-end deployment orchestration for the Virtual Stock Trader application. It supports multiple deployment methods (Kubernetes and Docker Compose) across local and remote environments.

## Directory Structure

```
ansible/
├── site.yml                 # Main orchestration playbook
├── inventory.ini           # Ansible inventory (hosts and groups)
├── ansible.cfg            # Ansible configuration
├── README.md              # This file
│
├── tasks/
│   ├── main.yml           # Main orchestration flow
│   ├── deploy_kubernetes.yml    # K8s deployment logic
│   ├── deploy_docker_compose.yml # Docker Compose deployment logic
│   └── validate_deployment.yml   # Post-deployment validation
│
├── templates/
│   ├── docker-compose.env.j2    # Docker Compose environment template
│   └── k8s-secret.yml.j2        # Kubernetes Secret template
│
├── roles/
│   ├── prerequisites/      # System prerequisites installation
│   ├── docker/            # Docker and Docker Compose setup
│   ├── kubernetes/        # Kubernetes deployment
│   ├── docker-compose/    # Docker Compose deployment
│   ├── secrets-vault/     # Secret management
│   └── monitoring/        # Monitoring stack setup (optional)
│
├── group_vars/
│   ├── local.yml          # Local deployment variables
│   └── remote.yml         # Remote deployment variables
│
└── vault.yml              # Encrypted secrets (git-ignored)
```

## Quick Start

### Prerequisites

- Ansible >= 2.9
- Python 3.8+
- For Kubernetes: kubectl and kubeconfig configured
- For Docker Compose: Docker and Docker Compose installed

### Installation

```bash
# Install required Ansible collections
ansible-galaxy collection install community.docker
ansible-galaxy collection install kubernetes.core

# Install Python dependencies
pip install pyyaml requests
```

### Local Deployment

#### Deploy to Kubernetes (Docker Desktop)

```bash
# Deploy with Kubernetes method to local cluster
ansible-playbook site.yml \
  -i inventory.ini \
  -e deployment_target=local \
  -e deployment_method=kubernetes \
  --check  # Run in check mode first to preview changes
```

Then apply without `--check`:

```bash
ansible-playbook site.yml \
  -i inventory.ini \
  -e deployment_target=local \
  -e deployment_method=kubernetes
```

#### Deploy with Docker Compose

```bash
ansible-playbook site.yml \
  -i inventory.ini \
  -e deployment_target=local \
  -e deployment_method=docker_compose \
  -e mysql_root_password=your_password \
  -e spring_datasource_password=your_password \
  -e jwt_secret=your_secret
```

### Remote Deployment

```bash
# Deploy to remote server using Kubernetes
ansible-playbook site.yml \
  -i inventory.ini \
  -e deployment_target=remote \
  -e deployment_method=kubernetes \
  -e remote_host=your.server.com \
  -e docker_hub_username=your_username \
  -e docker_hub_password=your_password
```

## Configuration

### Inventory

Edit `inventory.ini` to define deployment targets:

```ini
[local]
localhost ansible_connection=local

[remote]
prod.example.com ansible_user=deploy
```

### Environment Variables

Key environment variables for deployment:

- `deployment_target`: `local` or `remote`
- `deployment_method`: `kubernetes` or `docker_compose`
- `mysql_root_password`: MySQL root password
- `spring_datasource_password`: Spring Boot DB password
- `jwt_secret`: JWT secret key
- `docker_hub_username`: Docker Hub credentials (remote only)
- `docker_hub_password`: Docker Hub credentials (remote only)

### Group Variables

- **local.yml**: Development environment settings
- **remote.yml**: Production environment settings with HA, TLS, monitoring

## Playbook Roles

### 1. Prerequisites

Installs system dependencies:
- Git, curl, wget, jq
- Python 3 and pip
- Build tools

### 2. Docker

Sets up Docker environment:
- Installs Docker Engine
- Installs Docker Compose
- Configures daemon settings
- Authenticates with Docker Hub

### 3. Kubernetes

Deploys to Kubernetes:
- Installs kubectl, helm, kustomize
- Creates namespace
- Deploys manifests via kustomize
- Waits for pod readiness

### 4. Docker Compose

Deploys via Docker Compose:
- Creates .env file
- Pulls container images
- Starts services
- Waits for health checks

### 5. Secrets Vault

Manages secrets:
- Creates Kubernetes Secrets
- Generates .env files
- Encrypts sensitive data (with vault.yml)
- Validates secret configuration

### 6. Monitoring

Sets up monitoring (optional):
- Prometheus for metrics
- Grafana for visualization
- ELK Stack for logging
- Alert rules configuration

## Deployment Flow

```
site.yml (main playbook)
    ↓
1. Display configuration
    ↓
2. Include deployment method tasks
    ├─ deploy_kubernetes.yml
    │   ├ Create namespace
    │   ├ Deploy manifests (kustomize)
    │   ├ Wait for pods
    │   └ Verify deployments
    │
    └─ deploy_docker_compose.yml
        ├ Create .env file
        ├ Pull images
        ├ Start services
        └ Wait for health checks
    ↓
3. Run validation
    ├ Get pod/container status
    ├ Health checks (frontend, backend)
    └ Display results
    ↓
4. Display summary
```

## Validation & Health Checks

The playbook includes comprehensive validation:

- **Pod Status**: Checks Ready/Running status for all pods
- **Frontend Health**: HTTP 200 response from frontend endpoint
- **Backend Health**: HTTP 200 response from backend /health endpoint
- **Service Connectivity**: Verifies inter-service communication

## Troubleshooting

### Kubernetes Deployment Issues

```bash
# Check pod status
kubectl get pods -n virtual-stock-trader

# View pod logs
kubectl logs -n virtual-stock-trader <pod-name>

# Describe pod for events
kubectl describe pod -n virtual-stock-trader <pod-name>

# Check service connectivity
kubectl exec -n virtual-stock-trader <pod> -- curl backend-service:8081/health
```

### Docker Compose Deployment Issues

```bash
# View container logs
docker-compose logs -f backend

# Check container status
docker-compose ps

# Verify network connectivity
docker network inspect virtual-stock-trader_vst-network
```

### Common Issues

**Backend pod not becoming Ready:**
- Extended startup time (120s+) due to Spring Boot initialization
- Check resource allocation (CPU/memory constraints)
- Verify database connectivity in logs

**Frontend cannot reach backend:**
- Verify service DNS resolution
- Check nginx.conf upstream configuration
- Ensure backend-service port is correct (8081)

**MySQL connection errors:**
- Verify SPRING_DATASOURCE_PASSWORD in Secret matches MYSQL_ROOT_PASSWORD
- Check MYSQL_ROOT_HOST is set to '%' for remote connections
- Verify database initialization completed

## Advanced Usage

### Dry Run (Check Mode)

```bash
ansible-playbook site.yml -i inventory.ini --check -e deployment_target=local
```

### Verbose Output

```bash
ansible-playbook site.yml -i inventory.ini -vvv
```

### Specific Role Execution

```bash
ansible-playbook site.yml -i inventory.ini --tags prerequisites,docker
```

### Skip Certain Steps

```bash
ansible-playbook site.yml -i inventory.ini --skip-tags validation
```

## Secrets Management

### Using Ansible Vault

Create encrypted vault file:

```bash
ansible-vault create vault.yml
```

Edit vault file:

```bash
ansible-vault edit vault.yml
```

Run playbook with vault password:

```bash
ansible-playbook site.yml -i inventory.ini --ask-vault-pass
```

## Performance Optimization

- **Fact Caching**: Enabled by default (24h cache)
- **Parallel Execution**: 10 forks by default (configurable in ansible.cfg)
- **Connection Reuse**: SSH multiplexing enabled

## Security Considerations

1. **Secrets Management**:
   - Use Ansible Vault for sensitive data in git
   - Never commit plaintext secrets
   - Rotate secrets regularly

2. **Docker Registry**:
   - Use strong credentials
   - Consider using pull secrets in K8s

3. **Kubernetes**:
   - Implement RBAC policies
   - Use NetworkPolicies for pod isolation
   - Enable TLS for all communication

4. **Monitoring**:
   - Secure Prometheus endpoint (basic auth)
   - Use strong Grafana admin passwords
   - Implement ELK authentication

## Best Practices

1. **Test in Check Mode**: Always run `--check` first
2. **Review Changes**: Examine output before applying
3. **Version Control**: Keep playbooks in git
4. **Documentation**: Document custom variables
5. **Backup Configurations**: Save working configurations
6. **Monitor Deployments**: Use logs and monitoring after deployment

## Support and Debugging

For detailed logging:

```bash
ANSIBLE_DEBUG=true ansible-playbook site.yml -vvv
```

View Ansible logs:

```bash
tail -f ansible.log
```

## Related Documentation

- [Kubernetes Manifests](../k8s/README.md)
- [Docker Compose Setup](../docker-compose.yml)
- [CI/CD Pipeline](../docs/CI-CD-SETUP.md)
- [Backend Configuration](../backend/virtual-stock-trader/HELP.md)

## License

This Ansible automation suite is part of the Virtual Stock Trader application.
