# Kubernetes Deployment

This directory contains Kubernetes manifests for deploying the Virtual Stock Trader App.

## Prerequisites

- Kubernetes cluster (1.20+)
- `kubectl` configured to access your cluster
- Docker images pushed to Docker Hub:
  - `cavinsam/virtual-stock-trading-app-backend:latest`
  - `cavinsam/virtual-stock-trading-app-frontend:latest`

## Files Overview

- `namespace.yaml` - Creates the `virtual-stock-trader` namespace
- `configmap.yaml` - Application configuration (database URL, API keys, etc.)
- `secret.yaml` - Sensitive data (database password, JWT secret) ⚠️ **EDIT BEFORE DEPLOYING**
- `mysql-deployment.yaml` - MySQL database deployment
- `backend-deployment.yaml` - Spring Boot backend with 2 replicas
- `frontend-deployment.yaml` - React frontend with 2 replicas
- `ingress.yaml` - NGINX Ingress for routing (requires ingress controller)
- `kustomization.yaml` - Kustomize overlay for easy deployment

## Quick Start

### 1. Update Secrets (IMPORTANT)

Edit `secret.yaml` and change the default values:
```bash
kubectl edit secret app-secrets -n virtual-stock-trader
```

Or edit the file before applying:
```yaml
stringData:
  MYSQL_ROOT_PASSWORD: "your_secure_password"
  JWT_SECRET: "your_long_secure_jwt_secret_key"
```

### 2. Deploy to Kubernetes

Using kubectl directly:
```bash
kubectl apply -f .
```

Or using Kustomize:
```bash
kubectl apply -k .
```

### 3. Verify Deployment

```bash
# Check namespace
kubectl get ns

# Check all resources
kubectl get all -n virtual-stock-trader

# Check pods status
kubectl get pods -n virtual-stock-trader -w

# Check services
kubectl get svc -n virtual-stock-trader

# View logs
kubectl logs -n virtual-stock-trader -l app=backend --tail=100
kubectl logs -n virtual-stock-trader -l app=frontend --tail=100
```

### 4. Access the Application

#### Option A: Port Forwarding (Development)
```bash
# Frontend
kubectl port-forward -n virtual-stock-trader svc/frontend-service 8080:80

# Backend
kubectl port-forward -n virtual-stock-trader svc/backend-service 8081:8081
```

Then access:
- Frontend: http://localhost:8080
- Backend: http://localhost:8081/api/auth/status

#### Option B: Ingress (Production)
Configure your DNS and ingress controller, then access via `app.example.com`.

## Configuration

### Environment Variables

Edit `configmap.yaml` to customize:
- `SPRING_DATASOURCE_URL` - MySQL connection string
- `ALPHAVANTAGE_API_KEY` - Alpha Vantage API key for stock data

### Scaling

Scale deployments:
```bash
kubectl scale deployment backend --replicas=3 -n virtual-stock-trader
kubectl scale deployment frontend --replicas=3 -n virtual-stock-trader
```

### Update Images

Update image tags in deployments:
```bash
kubectl set image deployment/backend backend=cavinsam/virtual-stock-trading-app-backend:v1.0.0 -n virtual-stock-trader
kubectl set image deployment/frontend frontend=cavinsam/virtual-stock-trading-app-frontend:v1.0.0 -n virtual-stock-trader
```

## Persistent Storage (Production)

The current MySQL deployment uses `emptyDir` storage (data lost on pod restart).

For production, use PersistentVolumeClaim:
```yaml
volumes:
- name: mysql-storage
  persistentVolumeClaim:
    claimName: mysql-pvc
```

Create a `pvc.yaml`:
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-pvc
  namespace: virtual-stock-trader
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 20Gi
  storageClassName: standard
```

## Troubleshooting

### Pods not starting
```bash
kubectl describe pod <pod-name> -n virtual-stock-trader
```

### Database connection errors
```bash
# Verify MySQL pod is running
kubectl get pods -n virtual-stock-trader | grep mysql

# Check MySQL logs
kubectl logs -n virtual-stock-trader -l app=mysql
```

### Backend health check failing
```bash
# Test backend endpoint
kubectl exec -it <backend-pod> -n virtual-stock-trader -- curl http://localhost:8081/api/auth/status
```

## Uninstall

```bash
kubectl delete namespace virtual-stock-trader
```

## Next Steps

1. Set up HTTPS with cert-manager and Let's Encrypt
2. Configure horizontal pod autoscaling (HPA)
3. Set up monitoring with Prometheus and Grafana
4. Implement GitOps with ArgoCD or Flux
