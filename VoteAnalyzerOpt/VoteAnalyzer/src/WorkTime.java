import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

public class WorkTime
{
    private TreeSet<TimePeriod> periods;
    private static SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    /**
     * Set of TimePeriod objects
     */
    public WorkTime()
    {
        periods = new TreeSet<>();
    }

    public void addVisitTime(String visitTime) throws ParseException {
        long time = visitDateFormat.parse(visitTime).getTime();
        TimePeriod newPeriod = new TimePeriod(time, time);
        for(TimePeriod period : periods)
        {
            if(period.compareTo(newPeriod) == 0)
            {
                period.appendTime(time);
                return;
            }
        }
        periods.add(newPeriod);
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(TimePeriod period : periods)
        {
            if(builder.length()!=0) {
               builder.append( ", ");
            }
            builder.append( period);
        }
        return builder.toString();
    }
}
