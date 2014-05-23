if [ -z "$1" ]; then
	echo "Usage: ./logging.sh LOG_PATH"
	exit
fi

LOGPATH=$1

# cli batch for logging
# 3 different handlers (appenders)
# PEK-ALL: everything from INFO level
# PEK-WARN: only WARN messages
# PEK-ERROR: only ERROR messages
#
# none of the hu.sch logger output will make it into the server.log
CLIBATCH=$(cat <<BATCH
/path=pek.log.dir:add(path="$LOGPATH")
/subsystem=logging/periodic-rotating-file-handler=PEK-ALL:add(append=true, autoflush=true,named-formatter=PATTERN, file={relative-to="pek.log.dir", path="pek-all.log"}, level=INFO, suffix=".yyyy-MM-dd")
/subsystem=logging/periodic-rotating-file-handler=PEK-WARN:add(append=true, autoflush=true,named-formatter=PATTERN, file={relative-to="pek.log.dir", path="pek-warn.log"}, level=WARN, suffix=".yyyy-MM-dd", filter-spec="levels(WARN)")
/subsystem=logging/periodic-rotating-file-handler=PEK-ERROR:add(append=true, autoflush=true,named-formatter=PATTERN, file={relative-to="pek.log.dir", path="pek-error.log"}, level=ERROR, suffix=".yyyy-MM-dd", filter-spec="levels(ERROR)")
/subsystem=logging/logger=hu.sch:add(use-parent-handlers=false, handlers=["PEK-ALL", "PEK-WARN", "PEK-ERROR"])
BATCH
)

echo "$CLIBATCH" > logging.cli
