import java.time.LocalDate;
import java.util.Comparator;

/**
 * Created by jackli on 2015-11-17.
 * Class represents each line of the master event transaction file
 */
public class MasterEventTransactionsInfo implements Comparable<MasterEventTransactionsInfo>
{
    public String date;
    public String numberTickets;
    public String eventName;
    public LocalDate localDate;

    public MasterEventTransactionsInfo(String aInDate, String
            aInNumberTickets, String aInEventName)
    {
        date = aInDate;
        numberTickets = aInNumberTickets;
        eventName = aInEventName;
        
        int year = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(2, 4));
        int day = Integer.parseInt(date.substring(4));

        localDate = LocalDate.of(2000 + year, month, day);
    }

    public LocalDate getDate()
    {
        return localDate;
    }
    
    @Override
    public int compareTo(MasterEventTransactionsInfo aInEventObject)
    {
        return getDate().compareTo(aInEventObject.getDate());
    }
}
