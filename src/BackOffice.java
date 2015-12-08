import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by jackli on 2015-11-11.
 * <p>
 * Class represents Quibble Back Office operations such as merging events
 * transaction files, creating new master events file, creating new current
 * events files, etc.
 * t
 */
public class BackOffice
{
    /**
     * Process the previous day's transactions against the master events
     * file, updates current events file and master events file with the newly
     * processed info
     *
     * @param aInMasterEventFile
     * @param aInMergedEventTransactionFiles
     */
    public static void processTransactions(File aInMasterEventFile, File
            aInCurrentEventsFile, File aInMergedEventTransactionFiles)
    {
        try
        {
            FileReader lFileReader = new FileReader
                    (aInMasterEventFile);
            BufferedReader lBufferedReader = new BufferedReader(lFileReader);

            // Store info read from master events file
            // Key is event name, value is MasterEventTransactionsInfo object
            // that contains event info such as date, number of tickets and 
            // event name
            Map<String, MasterEventTransactionsInfo> lMasterMap = new
                    HashMap<>();

            // Store current line
            String lLine;

            MasterEventTransactionsInfo lMasterLineInfo;

            // Read master events file line by line, store info in a HashMap
            while ((lLine = lBufferedReader.readLine()) != null)
            {
                String[] lLineArray = new String[3];

                lLineArray[0] = lLine.substring(0, 6);
                lLineArray[1] = lLine.substring(7, 12);
                lLineArray[2] = lLine.substring(13);

                String lDate = lLineArray[0];
                String lNumTickets = lLineArray[1];
                String lMasterEventName = lLineArray[2];

                lMasterLineInfo = new MasterEventTransactionsInfo(lDate,
                        lNumTickets, lMasterEventName);

                lMasterMap.put(lMasterEventName, lMasterLineInfo);
            }

            // todo remove
            System.out.println("***lMasterMap: ");
            for (String lEventName : lMasterMap.keySet())
            {
                System.out.println("lEventName: " + lEventName + " Date: " +
                        lMasterMap.get(lEventName).date);
            }


            lFileReader = new FileReader
                    (aInMergedEventTransactionFiles);
            lBufferedReader = new BufferedReader(lFileReader);

            // Read aInMergedEventTransactionFiles line-by-line
            while ((lLine = lBufferedReader.readLine()) != null)
            {
                // todo remove
                System.out.println("YOa");

                // Parse the line to get the transaction code (read first two
                // characters)
                String lTransactionCode = lLine.substring(0, 2);
                String lTransactionEventName = lLine.substring(3, 23);

                // Date (This is only used if the transaction is "create")
                String lDate = lLine.substring(24, 30);

                // todo remove
                System.out.println("lLine: " + lLine);
                System.out.println("Date: " + lDate);

                int lYear = Integer.parseInt(lDate.substring
                        (0, 2));
                int lMonth = Integer.parseInt(lDate.substring
                        (2, 4));
                int lDay = Integer.parseInt(lDate.substring
                        (4, 6));

                // Only create transaction corresponds with a real date
                // All other transactions has a default "000000" date so
                // we do not want to check the date unless it's a create 
                // transaction
                if (lYear != 0 && lMonth != 0 && lDay != 0)
                {
                    LocalDate lEventDate = LocalDate.of(2000 + lYear,
                            lMonth, lDay);
                    LocalDate lToday = LocalDate.now();

                    // If the event's date has passed, do not include it in the
                    // new master events file and do not process the transaction
                    if (lEventDate.isBefore(lToday))
                    {
                        // todo remove
                        System.out.println("YOb");
                        // todo remove
                        System.out.println(lLine + aInMasterEventFile.getPath
                                ());

                        System.out.println(Constants.BACK_OFFICE + Constants
                                .ERROR_EVENT_DATE_PAST);
                        continue;
                    }
                }

                // Number of tickets in the transaction
                String lNumTicketsTransaction = lLine.substring(31);

                int lNewNumTickets = 0;

                // Process the transaction against the master events file
                switch (lTransactionCode)
                {
                    // sell
                    case "01":
                        lNewNumTickets = Integer.parseInt(lMasterMap.get
                                (lTransactionEventName).numberTickets) - Integer
                                .parseInt(lNumTicketsTransaction);

                        // Check that there are a positive number of tickets
                        // remaining. We stored all the event names and updated 
                        // number of tickets in a HashMap and the values aren't 
                        // written to the master events file until after 
                        // processing so we can just check the number of 
                        // tickets 
                        // value stored in the HashMap and throw and error
                        // if the updated value is less than 0
                        if (lNewNumTickets < 0)
                        {
                            System.out.println(Constants.BACK_OFFICE + Constants
                                    .ERROR_INSUFFICIENT_TICKETS);
                            continue;
                        }

                        lMasterLineInfo = new MasterEventTransactionsInfo
                                (lDate, addStringZeros(lNewNumTickets),
                                        lTransactionEventName);

                        // Update lMasterMap with new info
                        lMasterMap.put(lTransactionEventName, lMasterLineInfo);
                        break;

                    // return
                    case "02":
                        // todo remove
                        System.out.println("YOc");

                        // todo remove
                        System.out.println("lTransactionEventName: " +
                                lTransactionEventName);

                        lNewNumTickets = Integer.parseInt(lMasterMap.get
                                (lTransactionEventName).numberTickets) + Integer
                                .parseInt(lNumTicketsTransaction);

                        lMasterLineInfo = new MasterEventTransactionsInfo
                                (lDate, addStringZeros(lNewNumTickets),
                                        lTransactionEventName);

                        // Update lMasterMap with new info
                        lMasterMap.put(lTransactionEventName, lMasterLineInfo);
                        break;

                    // create
                    case "03":
                        // Check that there is no event already existing for 
                        // the transaction
                        // todo remove
                        System.out.println("case 03 lTransactionEventName: "
                                + lTransactionEventName);

                        if (lMasterMap.containsKey(lTransactionEventName))
                        {
                            System.out.println(Constants.BACK_OFFICE +
                                    Constants.ERROR_EVENT_ALREADY_EXISTS);
                            continue;
                        }

                        lMasterLineInfo = new MasterEventTransactionsInfo
                                (lDate, lNumTicketsTransaction,
                                        lTransactionEventName);

                        lMasterMap.put(lTransactionEventName, lMasterLineInfo);
                        break;

                    // add
                    case "04":
                        int lOldTickets = Integer.parseInt(lMasterMap.get
                                (lTransactionEventName).numberTickets);
                        int lMoreTickets = Integer
                                .parseInt(lNumTicketsTransaction);

                        lNewNumTickets = lOldTickets + lMoreTickets;

                        lMasterLineInfo = new MasterEventTransactionsInfo
                                (lDate, addStringZeros(lNewNumTickets),
                                        lTransactionEventName);

                        // Update lMasterMap with new info
                        lMasterMap.put(lTransactionEventName, lMasterLineInfo);
                        break;

                    // delete
                    case "05":
                        lMasterMap.remove(lTransactionEventName);
                        break;

                    default:
                        // todo error
                        break;
                }
            }

            lBufferedReader.close();

            // Clear current content from master events file
            FileWriter lFW_Master = new FileWriter(aInMasterEventFile);
            lFW_Master.write("");
            lFW_Master.close();

            // Clear current content from current events file
            FileWriter lFW_CurrentEvents = new FileWriter(aInCurrentEventsFile);
            lFW_CurrentEvents.write("");
            lFW_CurrentEvents.close();

            // Instantiate new FileWriter that overwrites instead of appends
            // for both master events file and current events file because we 
            // start with fresh files for these each day
            FileWriter lFW_MasterEvent = new FileWriter(aInMasterEventFile,
                    false);
            FileWriter lFW_Current_Events = new FileWriter
                    (aInCurrentEventsFile, true);

            // List to store MasterEventTransactionsInfo objects, ordered by
            // event date
            List<MasterEventTransactionsInfo> lEventsByDateList =
                    new ArrayList<>();

            // Parse date and store date components in these variables from
            // lMasterMap event
            int lMasterEventYear;
            int lMasterEventMonth;
            int lMasterEventDay;

            // Parse date and store date components in these variables from
            // lEventsByDateList event
            int lListEventYear;
            int lListEventMonth;
            int lListEventDay;

            // todo remove
            System.out.println("***lMasterMap");
            for (String lEventName : lMasterMap.keySet())
            {
                System.out.println("lEventName: " + lEventName + " Date: " +
                        lMasterMap.get(lEventName).date);
            }

            // Order Events by date in a List
            for (MasterEventTransactionsInfo lMasterMapEventObject :
                    lMasterMap.values())
            {
                // todo remove
                System.out.println("NO YO!!");
                System.out.println("lMasterMapEventObject: " +
                        lMasterMapEventObject.eventName);

                lEventsByDateList.add(lMasterMapEventObject);
                
//                if (lEventsByDateList.isEmpty())
//                {
//                    // Insert first MasterEventTransactionsInfo object
//                    lEventsByDateList.add(lMasterMapEventObject);
//                }
//                else
//                {


                // Iterate entire lEventsByDateList and compare dates to 
                    // check where it should be inserted
//                    for (int lCount = 0; lCount < lMasterMap.size();
//                         lCount++)
//                    {
//                        // todo remove
//                        System.out.println("lCount: " + lCount);
//                        System.out.println("lEventsByDateList size: " + lEventsByDateList.size());

//                        // If the event we are comparing is the same as the 
//                        // first one we inserted, skip it
//                        if (lCount < lEventsByDateList.size() && 
//                                (lMasterMapEventObject.eventName).equals
//                                (lEventsByDateList.get(lCount)
//                                        .eventName))
//                        {
//                            continue;
//                        }
                        
                        

//                        if (lMasterEventYear < lListEventYear)
//                        {
//                            // todo remove
//                            System.out.println("Inserting if");
//
//                            lEventsByDateList.add(lCount,
//                                    lMasterMapEventObject);
//                            continue;
//                        }
//                        else if (lMasterEventMonth < lListEventMonth)
//                        {
//                            // todo remove
//                            System.out.println("Inserting else ifA");
//
//                            lEventsByDateList.add(lCount,
//                                    lMasterMapEventObject);
//                            continue;
//                        }
//                        else if (lMasterEventDay < lListEventDay)
//                        {
//                            // todo remove
//                            System.out.println("Inserting else ifB");
//
//                            lEventsByDateList.add(lCount,
//                                    lMasterMapEventObject);
//                            continue;
//                        }
//                        else
//                        {
//                            // todo remove
//                            System.out.println("Inserting elseA");
//
//                            // todo remove
//                            System.out.println("lMasterMapEventObject event " +
//                                    "name: " + lMasterMapEventObject.eventName);
//                            System.out.println("lMasterMap.size(): " +
//                                    lMasterMap.size());
//
//                            lEventsByDateList.add(lMasterMapEventObject);
//                        }
//                    }
//                }

                // Reset date variables
                lMasterEventYear = 0;
                lMasterEventMonth = 0;
                lMasterEventDay = 0;

                lListEventYear = 0;
                lListEventMonth = 0;
                lListEventDay = 0;
            }

            // todo
            // Sort the lEventsByDateList by date
            Collections.sort(lEventsByDateList);

            // todo remove
            System.out.println("***lMasterMap: ");
            for (String lEventName : lMasterMap.keySet())
            {
                System.out.println("lEventName: " + lEventName + " Date: " +
                        lMasterMap.get(lEventName).date);
            }

            // todo remove
            System.out.println("***lEventsByDateList: ");
            for (MasterEventTransactionsInfo lEvent : lEventsByDateList)
            {
                System.out.println("lEvent: " + lEvent.eventName + " Date: "
                        + lEvent.date);
            }
            
            // Write lEventsByDateList line by line to master events file and
            // content
            for (int lCount = 0; lCount < lEventsByDateList.size(); lCount++)
            {
                lFW_MasterEvent.write(lEventsByDateList.get(lCount).date + " ");
                lFW_MasterEvent.write(lEventsByDateList.get(lCount)
                        .numberTickets + " ");

                // lKey is the event name
                lFW_MasterEvent.write(lEventsByDateList.get(lCount).eventName);

                lFW_Current_Events.write(lEventsByDateList.get(lCount)
                        .eventName + " ");
                lFW_Current_Events.write(lEventsByDateList.get(lCount)
                        .numberTickets);

                if (lCount < lEventsByDateList.size())
                {
                    lFW_MasterEvent.write("\n");
                    lFW_Current_Events.write("\n");
                }
            }

            lFW_MasterEvent.close();
            lFW_Current_Events.close();
        }
        catch (FileNotFoundException e)
        {
            // Failed constraint log (just print error message to console)
            System.out.println(Constants.ERROR_READ_FILE);
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e)
        {
            // Failed constraint log (just print error message to console)
            System.out.println(Constants.ERROR_READ_FILE);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Add zeros to number of tickets and return sequence as a String
     *
     * @param aInInt integer to append 0's to
     * @return String with appropriate # of 0's added
     */
    public static String addStringZeros(int aInInt)
    {
        StringBuilder lSB = new StringBuilder();

        // Append number of tickets
        int lNumZeros = 5 - String.valueOf(aInInt).length();

        for (int lCounter = 0; lCounter < lNumZeros; lCounter++)
        {
            lSB.append(0);
        }

        lSB.append(String.valueOf(aInInt));

        return lSB.toString();
    }

    public static void main(String[] args)
    {
        String lFilesDir = "../../Quibble_Files_Assignment6/";

        File lMasterEventsFile = new File
                (lFilesDir + "Master_Events_File");
        File lCurrentEventsFile = new File(lFilesDir + "Current_Events_File");
        File lMergedTransactionFile = new File(lFilesDir +
                "Merged_Event_Transaction_Files");

        processTransactions(lMasterEventsFile, lCurrentEventsFile,
                lMergedTransactionFile);
    }
}