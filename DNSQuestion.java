import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DNSQuestion 
{
    String QNAME;
    int QTYPE;
    int QCLASS;
    
    /*  Read a question from the input stream. Due to compression, you may have to ask
        the DNSMessage containing this question to read some of the fields
     */
    public static DNSQuestion DecodeQuestion(InputStream inputStream, DNSMessage message) throws IOException
    {
        
        DNSQuestion question = new DNSQuestion();
        
        String[] domainLabels = message.ReadDomainName(inputStream);

        question.QNAME = message.JoinDomainName(domainLabels);

        question.QTYPE = ((inputStream.read() & 0xFF) << 8) | (inputStream.read() & 0xFF);

        question.QCLASS = ((inputStream.read() & 0xFF) << 8) | (inputStream.read() & 0xFF);

        return question;
    }

    /*  Write the question bytes which will be sent to the client. The hash map is used
     *  for us to compress the message, see the DNSMessage class below
     */
    public void WriteBytes(ByteArrayOutputStream byteArrayOutputStream, HashMap<String, Integer> domainNameLocations) throws IOException
    {
        String[] domainPieces = QNAME.split("\\.");

        DNSMessage.writeDomainName(byteArrayOutputStream, domainNameLocations, domainPieces);

        byteArrayOutputStream.write((QTYPE >> 8) & 0xFF);
        byteArrayOutputStream.write((QTYPE & 0xFF));

        byteArrayOutputStream.write((QCLASS >> 8) & 0xFF);
        byteArrayOutputStream.write(QCLASS & 0xFF);
    }

    /*  toString(), equals(), and hashCode() -- Let your IDE generate these. They're needed to
     *  use a question as a HashMap key, and to get a human readable string.eBytes(ByteArrayOutputStream byteArrayOutputStream, HashMap<String, Integer> domainNameLocations)
     */
}
