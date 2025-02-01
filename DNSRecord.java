import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;


public class DNSRecord 
{
    public static DNSRecord DecodeRecord(InputStream inputStream, DNSMessage message)
    {
        return new DNSRecord();
    }

    public void WriteBytes(ByteArrayOutputStream byteArrayOutputStream, HashMap<String, Integer> domainNameLocations)
    {

    }

    /*  ToString() */

    /*  Return whether the creation date + the time to live is after the current time. The Date
     *  and Calendar classes will be useful for this.
     */
    public boolean IsExpired()
    {
        return false;
    }
}
