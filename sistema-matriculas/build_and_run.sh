#!/bin/bash

# Definindo cores
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função que executa comando e colore a saída
log_and_run() {
  local cmd="$1"
  local color="$2"

  echo -e "${color}>> Executando: $cmd${NC}"
  eval "$cmd" 2>&1 | while IFS= read -r line; do
    echo -e "${color}$line${NC}"
  done
}

# Primeiro executa o Maven (espera terminar)
log_and_run "mvn package" "$RED"

# Agora executa Zookeeper e a aplicação em paralelo
log_and_run "sh $ZK_HOME/bin/zkServer.sh start-foreground" "$GREEN" &
log_and_run "java -Djava.net.preferIPv4Stack=true -jar ./target/sistema-matriculas-0.0.1-SNAPSHOT.jar" "$BLUE" &
log_and_run "sh -c sleep infinity" "$NC" &

