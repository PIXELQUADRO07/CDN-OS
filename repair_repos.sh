#!/usr/bin/env bash

set -e

echo "Cerco i repository corrotti..."

MISSING=$(repo forall -c 'git rev-parse --is-inside-work-tree >/dev/null 2>&1 || echo $REPO_PATH')

if [ -z "$MISSING" ]; then
    echo "✅ Nessun repository da riparare!"
    exit 0
fi

echo
echo "Repository da riparare:"
echo "$MISSING"
echo

for repo in $MISSING
do
    repo=${repo%/}

    echo "==============================================="
    echo "Riparazione: $repo"
    echo "==============================================="

    rm -rf ".repo/projects/${repo}.git"
    rm -rf ".repo/project-objects/platform/${repo}.git"
    rm -rf "$repo"

    if repo sync "$repo" -j1 --fail-fast; then
        echo "✅ $repo completato"
    else
        echo "❌ Errore su $repo"
    fi

    echo
done

echo "==============================================="
echo "Controllo finale..."
repo forall -c 'git rev-parse --is-inside-work-tree >/dev/null 2>&1 || echo $REPO_PATH'
echo "==============================================="
echo "Operazione terminata."
