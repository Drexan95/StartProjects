import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimePeriod implements Comparable<TimePeriod>
{
    private long from;
    private long to;
    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
    /**
     * Time period within one day
     * @param from
     * @param to
     */
    public TimePeriod(long from, long to)
    {
        this.from = from;
        this.to = to;

        if(!dayFormat.format(new Date(from)).equals(dayFormat.format(new Date(to))))
            throw new IllegalArgumentException("Dates 'from' and 'to' must be within ONE day!");
    }

    public TimePeriod(Date from, Date to)
    {
        this.from = from.getTime();
        this.to = to.getTime();
        if(!dayFormat.format(from).equals(dayFormat.format(to)))
            throw new IllegalArgumentException("Dates 'from' and 'to' must be within ONE day!");
    }

    public void appendTime(long visitTime)
    {
        if(!dayFormat.format(new Date(from)).equals(dayFormat.format(new Date(visitTime))))
            throw new IllegalArgumentException("Visit time must be within the same day as the current TimePeriod!");

        if(visitTime < from) {
            from = visitTime;
        }
        if(visitTime > to) {
            to = visitTime;
        }
    }

    public String toString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");


        return dateFormat.format(this.from) + "-" + timeFormat.format(this.to);
    }

    @Override
    public int compareTo(TimePeriod period) {

        return dayFormat.format(new Date(from)).compareTo(dayFormat.format(new Date(period.from)));

    }
}

