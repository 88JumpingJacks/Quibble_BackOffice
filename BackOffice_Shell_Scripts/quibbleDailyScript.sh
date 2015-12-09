#!/bin/bash
# This script is assumed to be run from the BackOffice_Shell_Scripts directory
# of the Java project.
# Script functionalities:
# i) Runs the front end over a number of transaction sessions,
#       saving the output Transaction Summary File for each session in a 
#       separate file
# ii) Concatenates the separate Transaction Summary Files into a Merged 
#       Transaction Summary file
# iii) Runs your Back Office with the Merged Transaction Summary File as input.

# Directory where input files for this daily script are located
INPUT_DIR=$1

# Test output directory for transactions output
TRANSACTIONS_OUTPUT_DIR=$2

# Shell script directory
SHELL_SCRIPT_DIR=`pwd`

# cd to INPUT_DIR
cd $INPUT_DIR

# Get the full directory name of where INPUT_FILE is located
INPUT_FILE_FULL_DIR=`pwd`

# Change directories back to shell script directory
cd $SHELL_SCRIPT_DIR

# Change to directory where Quibble front end code is located
cd ../../Quibble_Assignment6/src
FRONT_END_DIR=`pwd`

# Compile front end Quibble Java class
javac Quibble.java Constants.java

# Execute the Quibble Java frontend code for each input file
# INPUT_FILE_FULL_DIR is assumed to contain only front end input files of .txt 
# format

for FILE in $(find "$INPUT_FILE_FULL_DIR" -name "*.txt")
do
    # Execute Java Quibble class to run the front end so we can execute the 
    # transactions
    java Quibble < $FILE > /dev/null
    
    # Change directories back to shell script directory
    cd $SHELL_SCRIPT_DIR

    DAILY_EVENT_TRANSACTION_FILE="../../Quibble_Files_Assignment6/Daily_Event_Transaction_File"

    FILE_BASENAME=`basename $FILE .txt`

    # Copy the daily event transaction file to the output transactions output 
    # directory as a ".trn" file
    cp $DAILY_EVENT_TRANSACTION_FILE $TRANSACTIONS_OUTPUT_DIR/"$FILE_BASENAME".trn
    
    # Change to directory where Quibble front end code is located
    cd $FRONT_END_DIR
done

# Change directories back to shell script directory
cd $SHELL_SCRIPT_DIR

# Quibble Files are located here
FILES_DIR="../../Quibble_Files_Assignment6/"

# Back Office files
NEW_MASTER_EVENTS_FILE="$FILES_DIR/Master_Events_File"
NEW_CURRENT_EVENTS_FILE="$FILES_DIR/Current_Events_File"
MERGED_EVENT_TRANSACTION_FILES="$FILES_DIR/Merged_Event_Transaction_Files"

# Clear the previous contents from the merged transaction file
echo -n >| $MERGED_EVENT_TRANSACTION_FILES

# Merge the daily transaction files
for TRANSACTION_FILE in $(find "$TRANSACTIONS_OUTPUT_DIR" -name "*.trn")
do
	# Merge every individual transaction file to MERGED_EVENT_TRANSACTION_FILES
	paste $TRANSACTION_FILE >> $MERGED_EVENT_TRANSACTION_FILES
done

# Go to src folder of Back Office Java project
cd ../src
javac BackOffice.java Constants.java MasterEventTransactionsInfo.java

java BackOffice > /dev/null