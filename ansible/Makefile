.PHONY: help deploy deploy-k8s deploy-compose check clean vault-create vault-edit init

# Makefile for Virtual Stock Trader Ansible Automation
# Provides convenient commands for common deployment tasks

SHELL := /bin/bash
ANSIBLE_CMD := ansible-playbook
INVENTORY := inventory.ini
PLAYBOOK := site.yml
DEPLOYMENT_TARGET ?= local
DEPLOYMENT_METHOD ?= kubernetes

help:
	@echo "═════════════════════════════════════════════════════════════════"
	@echo "  Virtual Stock Trader - Ansible Deployment Helper"
	@echo "═════════════════════════════════════════════════════════════════"
	@echo ""
	@echo "Usage: make [target] [DEPLOYMENT_TARGET=local|remote] [DEPLOYMENT_METHOD=kubernetes|docker_compose]"
	@echo ""
	@echo "Common Targets:"
	@echo "  make help              - Show this help message"
	@echo "  make init              - Initialize Ansible environment"
	@echo "  make check             - Check playbook syntax and preview changes (dry-run)"
	@echo "  make deploy            - Deploy using specified method (default: local/kubernetes)"
	@echo "  make deploy-k8s        - Deploy to Kubernetes"
	@echo "  make deploy-compose    - Deploy using Docker Compose"
	@echo "  make validate          - Run post-deployment validation"
	@echo "  make clean             - Clean up deployment"
	@echo ""
	@echo "Secret Management:"
	@echo "  make vault-create      - Create encrypted vault.yml"
	@echo "  make vault-edit        - Edit encrypted vault.yml"
	@echo ""
	@echo "Examples:"
	@echo "  make check DEPLOYMENT_TARGET=local"
	@echo "  make deploy DEPLOYMENT_METHOD=kubernetes"
	@echo "  make deploy DEPLOYMENT_TARGET=remote DEPLOYMENT_METHOD=kubernetes"
	@echo ""

init:
	@echo "Initializing Ansible environment..."
	@echo "Installing required collections..."
	ansible-galaxy collection install community.docker kubernetes.core
	@echo "Installing Python dependencies..."
	pip install -r requirements.txt 2>/dev/null || pip install pyyaml requests cryptography
	@echo "✓ Initialization complete"

check:
	@echo "Running playbook syntax check and dry-run..."
	$(ANSIBLE_CMD) $(PLAYBOOK) \
		-i $(INVENTORY) \
		-e deployment_target=$(DEPLOYMENT_TARGET) \
		-e deployment_method=$(DEPLOYMENT_METHOD) \
		--check \
		-v

deploy: deploy-$(DEPLOYMENT_METHOD)

deploy-k8s:
	@echo "Deploying to Kubernetes (target: $(DEPLOYMENT_TARGET))..."
	$(ANSIBLE_CMD) $(PLAYBOOK) \
		-i $(INVENTORY) \
		-e deployment_target=$(DEPLOYMENT_TARGET) \
		-e deployment_method=kubernetes

deploy-compose:
	@echo "Deploying with Docker Compose (target: $(DEPLOYMENT_TARGET))..."
	$(ANSIBLE_CMD) $(PLAYBOOK) \
		-i $(INVENTORY) \
		-e deployment_target=$(DEPLOYMENT_TARGET) \
		-e deployment_method=docker_compose

validate:
	@echo "Running deployment validation..."
	$(ANSIBLE_CMD) $(PLAYBOOK) \
		-i $(INVENTORY) \
		-e deployment_target=$(DEPLOYMENT_TARGET) \
		-e deployment_method=$(DEPLOYMENT_METHOD) \
		--tags validate

clean:
	@echo "Cleaning up deployment..."
	@if [ "$(DEPLOYMENT_METHOD)" = "kubernetes" ]; then \
		echo "Removing Kubernetes namespace virtual-stock-trader..."; \
		kubectl delete namespace virtual-stock-trader --ignore-not-found=true; \
	else \
		echo "Stopping Docker Compose services..."; \
		docker-compose down -v || true; \
	fi
	@echo "✓ Cleanup complete"

vault-create:
	@echo "Creating encrypted vault.yml..."
	@if [ -f vault.yml ]; then \
		echo "vault.yml already exists. Use 'make vault-edit' to modify."; \
	else \
		cp vault.yml.template vault.yml; \
		ansible-vault encrypt vault.yml; \
		echo "✓ Vault created and encrypted"; \
	fi

vault-edit:
	@echo "Editing encrypted vault.yml..."
	ansible-vault edit vault.yml

verbose-deploy:
	@echo "Deploying with verbose output..."
	$(ANSIBLE_CMD) $(PLAYBOOK) \
		-i $(INVENTORY) \
		-e deployment_target=$(DEPLOYMENT_TARGET) \
		-e deployment_method=$(DEPLOYMENT_METHOD) \
		-vvv

syntax-check:
	@echo "Checking playbook syntax..."
	ansible-playbook $(PLAYBOOK) --syntax-check

list-tasks:
	@echo "Listing all tasks in playbook..."
	ansible-playbook $(PLAYBOOK) --list-tasks

list-hosts:
	@echo "Listing all hosts in inventory..."
	ansible-inventory -i $(INVENTORY) --list

version:
	@ansible --version
	@echo ""
	@docker --version
	@kubectl version --short 2>/dev/null || echo "kubectl not installed"

logs:
	@tail -f ansible.log 2>/dev/null || echo "No ansible.log found. Run a playbook first."

.DEFAULT_GOAL := help
