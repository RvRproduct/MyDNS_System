
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DNSMessage 
{
   DNSHeader dnsHeader;
   DNSQuestion[] questions;
   DNSRecord[] answers;
   DNSRecord[] authorityRecords;
   DNSRecord[] additionalRecords;
   byte[] fullMessage;

   /*  You should also store the byte array containing the complete message in this class.
    *  You'll need it to handle the compression technique described above
   */
  
   public static DNSMessage DecodeMessage(byte[] bytes) throws IOException
   {
      DNSMessage message = new DNSMessage();
      message.fullMessage = bytes;

      try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes))
      {
         // Header
         message.dnsHeader = DNSHeader.DecodeHeader(inputStream);

         // Questions
         message.questions = new DNSQuestion[message.dnsHeader.QDCOUNT];
         for (int i = 0; i < message.dnsHeader.QDCOUNT; i++)
         {
            message.questions[i] = DNSQuestion.DecodeQuestion(inputStream, message);
         }

         // Answers
         message.answers = new DNSRecord[message.dnsHeader.ANCOUNT];
         for (int i = 0; i < message.dnsHeader.ANCOUNT; i++)
         {
            message.answers[i] = DNSRecord.DecodeRecord(inputStream, message);
         }

         // Authority Records
         message.authorityRecords = new DNSRecord[message.dnsHeader.NSCOUNT];
         for (int i = 0; i < message.dnsHeader.NSCOUNT; i++)
         {
            message.authorityRecords[i] = DNSRecord.DecodeRecord(inputStream, message);
         }

         // Additional Records
         message.additionalRecords = new DNSRecord[message.dnsHeader.ARCOUNT];
         for (int i = 0; i < message.dnsHeader.ARCOUNT; i++)
         {
            message.additionalRecords[i] = DNSRecord.DecodeRecord(inputStream, message);
         }

      }
      catch (IOException e)
      {
         System.err.println("Error decoding DNS message: " + e.getMessage());
      }

      return message;
   }

   /* Read the pieces of a domain name starting from the current position of the input stream */
   public String[] ReadDomainName(InputStream inputStream) throws IOException
   {
      List<String> labels = new ArrayList<>();
      int length;

      while ((length = inputStream.read()) > 0)
      {
         if ((length & 0xC0) == 0xC0)
         {
            int pointer = ((length & 0x3F) << 8) | inputStream.read();
            return ReadDomainName(pointer);
         }
         byte[] labelBytes = new byte[length];
         inputStream.read(labelBytes);
         labels.add(new String(labelBytes));
      }

      return labels.toArray(new String[0]);
   }

     /* Same, but used when there's compression and we need to find the domain from earlier in the
      * message. This method should make a ByteArrayInputStream that starts at the specified byte
      * and call the other version of this method.
      */
   public String[] ReadDomainName(int firstByte)
   {
      try
      {
         ByteArrayInputStream inputStream = new ByteArrayInputStream(fullMessage);
         inputStream.skip(firstByte);
         return ReadDomainName(inputStream);
      }
      catch (IOException e)
      {
         return new String[]{"ERROR"};
      }
   }

   /* Build a response based on the request and the answers you intend to send back. */
   public static DNSMessage BuildResponse(DNSMessage request, DNSRecord[] answers)
   {
      DNSMessage response = new DNSMessage();

      // Header
      response.dnsHeader = DNSHeader.BuildHeaderForResponse(request, response);

      // Questions
      response.questions = request.questions;

      // Answers
      response.answers = answers;

      // Authority and Additional Records
      response.authorityRecords = new DNSRecord[0];
      response.additionalRecords = new DNSRecord[0];

      return response;
   }

   /* Get the bytes to put in a packet and send back */
   public byte[] ToBytes()
   {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      HashMap<String, Integer> domainNameLocations = new HashMap<>();

      try
      {
         // Header
         dnsHeader.WriteBytes(outputStream);

         // Questions
         for (DNSQuestion question : questions)
         {
            question.WriteBytes(outputStream, domainNameLocations);
         }

         // Answers
         for (DNSRecord answer : answers)
         {
            answer.WriteBytes(outputStream, domainNameLocations);
         }

         // Authority Records
         for (DNSRecord record : authorityRecords)
         {
            record.WriteBytes(outputStream, domainNameLocations);
         }

         // Additional Records
         for (DNSRecord record : additionalRecords)
         {
            record.WriteBytes(outputStream, domainNameLocations);
         }
      }
      catch (IOException e)
      {
         System.err.println("Error encoding DNS message: " + e.getMessage());
      }

      return outputStream.toByteArray();
   }

   /* If this is the first time we've seen this domain name in the packet, write it using
   * the DNS encoding (each segment of the domain prefixed with its length, 0 at the end),
   * and add it to the hash map. Otherwise, write a back pointer to where the domain has
   * been seen previously.
   */
   public static void writeDomainName(ByteArrayOutputStream byteArrayOutputStream,
   HashMap<String, Integer> domainNameLocations,
   String[] domainPieces) throws IOException
   {
      String domainName = JoinDomainName(domainPieces);

      if (domainNameLocations.containsKey(domainName))
      {
         int pointer = domainNameLocations.get(domainName);
         byteArrayOutputStream.write((pointer >> 8) | 0xC0);
         byteArrayOutputStream.write(pointer & 0xFF);
         return;
      }

      domainNameLocations.put(domainName, byteArrayOutputStream.size());

      for (String label : domainPieces)
      {
         byteArrayOutputStream.write(label.length());
         byteArrayOutputStream.write(label.getBytes());
      }

      byteArrayOutputStream.write(0);
   }

   /* Join the pieces of a domain name with dots (["utah", "edu"] -> "utah.edu") */
   public static String JoinDomainName(String[] pieces)
   {
      return String.join(".", pieces);
   }

   // String ToString()
}
