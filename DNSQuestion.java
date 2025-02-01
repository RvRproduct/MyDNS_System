import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class DNSQuestion 
{
    
    /*  Read a question from the input stream. Due to compression, you may have to ask
        the DNSMessage containing this question to read some of the fields
     */
    public static DNSQuestion DecodeQuestion(InputStream inputStream, DNSMessage message)
    {
        return new DNSQuestion();
    }

    /*  Write the question bytes which will be sent to the client. The hash map is used
     *  for us to compress the message, see the DNSMessage class below
     */
    public void WriteBytes(ByteArrayOutputStream byteArrayOutputStream, HashMap<String, Integer> domainNameLocations)
    {

    }

    /*  toString(), equals(), and hashCode() -- Let your IDE generate these. They're needed to
     *  use a question as a HashMap key, and to get a human readable string.
     */
}
