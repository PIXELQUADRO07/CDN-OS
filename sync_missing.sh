#!/usr/bin/env bash
#
# sync_missing.sh
# Sincronizza solo i progetti AOSP mancanti o incompleti, uno alla volta.
# Da lanciare dalla root dell'albero aosp (dove c'è la cartella .repo).
#
set -uo pipefail

LOGFILE="sync_missing.log"
> "$LOGFILE"

if [ ! -d ".repo" ]; then
    echo "Errore: non trovo la cartella .repo qui. Lancia lo script dalla root di ~/aosp."
    exit 1
fi

echo "Leggo la lista completa dei progetti dal manifest..."
mapfile -t PROJECTS < <(repo list -a 2>/dev/null | awk -F' : ' '{print $1}')

TOTAL=${#PROJECTS[@]}
if [ "$TOTAL" -eq 0 ]; then
    echo "Nessun progetto trovato. 'repo list -a' ha restituito output vuoto."
    exit 1
fi

echo "Trovati $TOTAL progetti nel manifest."
echo "Verifico quali sono mancanti o incompleti..."
echo ""

TO_SYNC=()

for path in "${PROJECTS[@]}"; do
    if [ ! -d "$path" ]; then
        TO_SYNC+=("$path")
        continue
    fi
    if [ ! -e "$path/.git" ]; then
        TO_SYNC+=("$path")
        continue
    fi
    # cartella e .git esistono: verifica che HEAD sia risolvibile
    if ! git -C "$path" rev-parse --verify HEAD >/dev/null 2>&1; then
        TO_SYNC+=("$path")
        continue
    fi
done

MISSING_TOTAL=${#TO_SYNC[@]}

if [ "$MISSING_TOTAL" -eq 0 ]; then
    echo "Tutti i progetti risultano già presenti e completi. Niente da fare."
    exit 0
fi

echo "Progetti mancanti o incompleti da sincronizzare: $MISSING_TOTAL / $TOTAL"
echo "Log dettagliato in: $LOGFILE"
echo ""

COUNT=0
FAILED=()
BARLEN=40

for path in "${TO_SYNC[@]}"; do
    COUNT=$((COUNT + 1))
    PERCENT=$(( COUNT * 100 / MISSING_TOTAL ))
    FILLED=$(( PERCENT * BARLEN / 100 ))
    BAR=$(printf '%*s' "$FILLED" '' | tr ' ' '#')
    EMPTY=$(printf '%*s' $((BARLEN - FILLED)) '')

    printf "\r[%s%s] %3d%% (%d/%d) %-50s" "$BAR" "$EMPTY" "$PERCENT" "$COUNT" "$MISSING_TOTAL" "${path:0:50}"

    {
        echo "=== $(date '+%H:%M:%S') Sync: $path ==="
    } >> "$LOGFILE"

    if repo sync -j1 --force-sync --fail-fast "$path" >> "$LOGFILE" 2>&1; then
        :
    else
        FAILED+=("$path")
    fi
done

echo ""
echo ""
echo "Completato: $COUNT/$MISSING_TOTAL progetti processati."

if [ ${#FAILED[@]} -gt 0 ]; then
    echo ""
    echo "Progetti FALLITI (${#FAILED[@]}):"
    printf '  - %s\n' "${FAILED[@]}"
    echo ""
    echo "Puoi rilanciare lo script: quelli riusciti verranno saltati automaticamente."
else
    echo "Tutti i progetti mancanti sono stati sincronizzati con successo."
    echo ""
    echo "Consiglio: lancia comunque un 'repo sync -j4 --fail-fast' finale"
    echo "per un controllo di coerenza completo (sarà veloce, la maggior parte è già ok)."
fi
