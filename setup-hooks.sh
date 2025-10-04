#!/bin/bash

# Git Hooks Setup Script
# This script installs Git hooks from .githooks directory to .git/hooks

echo "Installing Git hooks..."

# Create hooks directory if it doesn't exist
mkdir -p .git/hooks

# Copy all hooks from .githooks to .git/hooks
if [ -d ".githooks" ]; then
    for hook in .githooks/*; do
        if [ -f "$hook" ]; then
            hook_name=$(basename "$hook")
            cp "$hook" ".git/hooks/$hook_name"
            chmod +x ".git/hooks/$hook_name"
            echo "✓ Installed $hook_name hook"
        fi
    done
    echo ""
    echo "[FATAL] Git hooks installed successfully!"
    echo ""
    echo "Commit messages must now follow conventional commit format:"
    echo "  <type>: <description>"
    echo ""
    echo "Example: feat: add user authentication"
else
    echo "[ERROR] Error: .githooks directory not found"
    exit 1
fi