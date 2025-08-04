#!/bin/sh

log_and_run() {
  echo "===== Starting: $1 ====="
  # Run the command passed as argument
  # Output both stdout and stderr, prefixing each line with a label
  # Using 'tee' to both show output and save it if needed
  sh -c "$1" 2>&1 | sed "s/^/[$1] /"
  ret=${PIPESTATUS[0]:-0} # capture exit status of the command
  if [ $ret -eq 0 ]; then
    echo "===== Finished: $1 SUCCESS ====="
  else
    echo "===== Finished: $1 FAILURE with code $ret ====="
    exit $ret
  fi
}

#log_and_run "mvn clean install"
#log_and_run "sh \$ZK_HOME/bin/zkServer.sh start-foreground"
#log_and_run "java -jar ./target/sistema-matriculas-0.0.1-SNAPSHOT.jar"
