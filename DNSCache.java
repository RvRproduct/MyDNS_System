
import java.util.Arrays;
import java.util.HashMap;

public class DNSCache 
{
    /* You can just store the first answer for any question in the cache
     * (a response for google.com might return 10 IP addresses, just store
     * the first one). This class should have methods for querying and inserting
     * records into the cache. When you look up an entry, if its too old (its
     * TTL has expired), remove it and return "not found."
     */
    public HashMap<DNSQuestion, DNSRecord> localCache = new HashMap<>();

    /* Query a record from the cache */
    public DNSRecord QueryRecord(DNSQuestion question)
    {
        if (localCache.containsKey(question))
        {
            DNSRecord record = localCache.get(question);

            if (record.IsExpired())
            {
                System.out.println("CACHE entry expired for: " + question.QNAME);
                localCache.remove(question);
                return null;
            }

            System.out.println("CACHE FOUND: " + question.QNAME);
            return record;
        }

        System.out.println("CACHE NOT FOUND: " + question.QNAME);
        return null;
    }

    /* Insert a Record to the cache */
    public void InsertRecord(DNSQuestion question, DNSRecord[] answers)
    {
        if (answers.length > 0)
        {
            DNSRecord firstAnswer = answers[0];
            if (!localCache.containsKey(question))
            {
                localCache.put(question, firstAnswer);
                System.out.println("Inserted into CACHE: " + question.QNAME + " -> " + Arrays.toString(firstAnswer.RDATA));
            }
            else
            {
                System.out.println("CACHE already contains: " + question.QNAME);
            }
        }
        else
        {
            System.out.println("No valid answer received, skipping CACHE.");
        }
        
    }

}
