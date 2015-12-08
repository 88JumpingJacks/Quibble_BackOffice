#!/bin/bash
# This script is called by quibbleRunTests.sh (located in Shell_Scripts 
# folder of Quibble Java project)
# That script is assumed to be run from the Shell_Scripts directory of 
# the Java Quibble project as noted in quibbleRunTests.sh
# This script represents the overnight batch processor for the backend.

# Directory where transaction files created by Quibble are located
DIR=$1

# Quibble Files are located here
FILES_DIR="../../Quibble_Files_Assignment6/"

# Back Office files
NEW_MASTER_EVENTS_FILE="$FILES_DIR/Master_Events_File"
NEW_CURRENT_EVENTS_FILE="$FILES_DIR/Current_Events_File"
MERGED_EVENT_TRANSACTION_FILES="$FILES_DIR/Merged_Event_Transaction_Files"

# Clear the previous contents from the merged transaction file
echo -n >| $MERGED_EVENT_TRANSACTION_FILES

# Automated test of .trn files
for TRANSACTION_FILE in $(find "$DIR" -name "*.trn")
do
	#remove this
	echo "YO $TRANSACTION_FILE"

	# Merge every individual transaction file to MERGED_EVENT_TRANSACTION_FILES
	paste $TRANSACTION_FILE >> $MERGED_EVENT_TRANSACTION_FILES
done

# Go to src folder of Java project
cd ../src
javac BackOffice.java Constants.java MasterEventTransactionsInfo.java

java BackOffice