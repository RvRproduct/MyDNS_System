
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class DNSMessage 
{
    DNSHeader dnsHeader;
    DNSQuestion[] questions;
    DNSRecord[] answers;
    // an array of "authority records" which we'll ignore
    // an array of "additional records" which we'll almost ignore

    /*  You should also store the byte array containing the complete message in this class.
     *  You'll need it to handle the compression technique described above
     */

     public static DNSMessage DecodeMessage(byte[] bytes)
     {
        return new DNSMessage();
     }

     /* Read the pieces of a domain name starting from the current position of the input stream */
     public String[] ReadDomainName(InputStream inputStream)
     {
        String[] word = {"w"};
        return word;
     }

     /* Same, but used when there's compression and we need to find the domain from earlier in the
      * message. This method should make a ByteArrayInputStream that starts at the specified byte
      * and call the other version of this method.
      */
     public String[] ReadDomainName(int firstByte)
     {
        String[] word = {"w"};
        return word;
     }

     /* Build a response based on the request and the answers you intend to send back. */
     public static DNSMessage BuildResponse(DNSMessage request, DNSRecord[] answers)
     {
        return new DNSMessage();
     }

     /* Get the bytes to put in a packet and send back */
     public byte[] ToBytes()
     {
        byte[] bytesA = new byte[1];

        return bytesA;
     }

     /* If this is the first time we've seen this domain name in the packet, write it using
      * the DNS encoding (each segment of the domain prefixed with its length, 0 at the end),
      * and add it to the hash map. Otherwise, write a back pointer to where the domain has
      * been seen previously.
      */
     public static void writeDomainName(ByteArrayOutputStream byteArrayOutputStream,
      HashMap<String, Integer> domainNameLocations,
      String[] domainPieces)
      {

      }

      /* Join the pieces of a domain name with dots (["utah", "edu"] -> "utah.edu") */
      public String JoinDomainName(String[] pieces)
      {
        return "";
      }

      // String ToString()
}
