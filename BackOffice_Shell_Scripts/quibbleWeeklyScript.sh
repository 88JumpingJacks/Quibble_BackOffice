#!/bin/bash
# This script is assumed to be run from the BackOffice_Shell_Scripts directory
# of the Java project.
# Script runs the daily script five separate times to simulate a five day 
# work week. 

# Directory where input files for the daily script are located
# This directory should contain five directories with the names 
# "Day_Input<Day Number>" (ex. Day_Input1) that contain the input file for the
# given day
INPUT_DIR=$1

# Get current date and time
YEAR=`date '+%Y'`
MONTH=`date '+%m'`
DAY=`date '+%d'`
HOUR=`date '+%H'`
MINUTE=`date '+%M'`
SECOND=`date '+%S'`

# Test output directory for transactions output
TRANSACTIONS_OUTPUT_DIR=../BackOffice_Script_Output_Files/Transactions_test_${YEAR}_${MONTH}_${DAY}-${HOUR}_${MINUTE}_${SECOND}

# Create the test output folder for this run
# The parent folder "../BackOffice_Script_Output_Files" already exists
mkdir $TRANSACTIONS_OUTPUT_DIR

FILES_DIR="../../Quibble_Files_Assignment6/"
MASTER_EVENTS_FILE="$FILES_DIR/Master_Events_File"
CURRENT_EVENTS_FILE="$FILES_DIR/Current_Events_File"

# Quibble Files are located here
FILES_DIR="../../Quibble_Files_Assignment6/"

# Clear Current and Master Events Files
echo -n >| $MASTER_EVENTS_FILE 
echo -n >| $CURRENT_EVENTS_FILE

echo "Running..."

NUM_DAYS=1
while [ $NUM_DAYS -le 5 ] 
do
    # Directory where Merged Transaction File and Master Events File will be 
    # outputted
    DAY_OUTPUT_DIR=$TRANSACTIONS_OUTPUT_DIR/Day_${NUM_DAYS}_Output
    mkdir $DAY_OUTPUT_DIR
    
    # Input directory for the day $NUM_DAYS containing front end input 
    FRONTEND_INPUT_DIR_PREFIX=$INPUT_DIR/Day_Input
    ./quibbleDailyScript.sh $FRONTEND_INPUT_DIR_PREFIX${NUM_DAYS} $DAY_OUTPUT_DIR
    
    # Copy contents of Merged Transaction File and Master Events File
    # to the DAY_OUTPUT_DIR directory
    cp $FILES_DIR/Merged_Event_Transaction_Files $DAY_OUTPUT_DIR
    cp $FILES_DIR/Master_Events_File $DAY_OUTPUT_DIR
    
    NUM_DAYS=$(( NUM_DAYS+1 ))
done

echo "Finished"