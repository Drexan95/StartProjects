import java.text.SimpleDateFormat;
import java.util.Date;

public class Voter
{
    private String name;
    private String birthDay;

    public Voter(String name, String birthDay)
    {
        this.name = name;
        this.birthDay = birthDay;
    }

    @Override
    public boolean equals(Object obj)
    {
        Voter voter = (Voter) obj;
        return name.equals(voter.name) && birthDay.equals(voter.birthDay);
    }

    @Override
    public int hashCode()
    {
        long code = name.hashCode() + birthDay.hashCode();
        while(code > Integer.MAX_VALUE) {
            code = code/10;
        }
        return (int) code;
    }

    public String toString()
    {

        return name + " (" + birthDay + ")";
    }

    public String getName()
    {
        return name;
    }

    public String getBirthDay()
    {
        return birthDay;
    }
}
