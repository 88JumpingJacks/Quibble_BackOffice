/**
 * Created by jackli on 2015-11-17.
 * Class represents each line of the master event transaction file
 */
public class MasterEventTransactionsInfo
{
    public String date;
    public String numberTickets;
    public String eventName;

    public MasterEventTransactionsInfo(String aInDate, String
            aInNumberTickets, String aInEventName)
    {
        date = aInDate;
        numberTickets = aInNumberTickets;
        eventName = aInEventName;
    }
}
