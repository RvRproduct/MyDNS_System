import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class DNSRecord 
{
    private long creationTime = System.currentTimeMillis();
    String NAME;
    int TYPE;
    int CLASS;
    int TTL;
    int RDLENGTH;
    byte[] RDATA;

    // Decode Record
    public static DNSRecord DecodeRecord(InputStream inputStream, DNSMessage message) throws IOException
    {
        DNSRecord record = new DNSRecord();

        String[] domainLabels = message.ReadDomainName(inputStream);
        record.NAME = message.JoinDomainName(domainLabels);

        record.TYPE = ((inputStream.read() & 0xFF) << 8) | (inputStream.read() & 0xFF);

        record.CLASS = ((inputStream.read() & 0xFF) << 8) | (inputStream.read() & 0xFF);

        record.TTL = ((inputStream.read() & 0xFF) << 24) |
                     ((inputStream.read() & 0xFF) << 16) |
                     ((inputStream.read() & 0xFF) << 8)  |
                     (inputStream.read() & 0xFF);

        record.RDLENGTH = ((inputStream.read() & 0xFF) << 8) | (inputStream.read() & 0xFF);

        record.RDATA = new byte[record.RDLENGTH];
        inputStream.read(record.RDATA);

        return record;
    }

    // Write Bytes
    public void WriteBytes(ByteArrayOutputStream byteArrayOutputStream, HashMap<String, Integer> domainNameLocations) throws IOException
    {
        String[] domainPieces = NAME.split("\\.");
        DNSMessage.writeDomainName(byteArrayOutputStream, domainNameLocations, domainPieces);

        byteArrayOutputStream.write((TYPE >> 8) & 0xFF);
        byteArrayOutputStream.write(TYPE & 0xFF);

        byteArrayOutputStream.write((CLASS >> 8) & 0xFF);
        byteArrayOutputStream.write(CLASS & 0xFF);

        byteArrayOutputStream.write((TTL >> 24) & 0xFF);
        byteArrayOutputStream.write((TTL >> 16) & 0xFF);
        byteArrayOutputStream.write((TTL >> 8) & 0xFF);
        byteArrayOutputStream.write(TTL & 0xFF);

        byteArrayOutputStream.write((RDLENGTH >> 8) & 0xFF);
        byteArrayOutputStream.write(RDLENGTH & 0xFF);

        byteArrayOutputStream.write(RDATA);
    }

    /*  ToString() */

    /*  Return whether the creation date + the time to live is after the current time. The Date
     *  and Calendar classes will be useful for this.
     */
    public boolean IsExpired()
    {
        long currentTime = System.currentTimeMillis();
        return (currentTime - creationTime) / 1000 > TTL;
    }
}
