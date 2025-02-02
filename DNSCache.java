
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
                localCache.remove(question);
                return null;
            }

            return record;
        }

        return null;
    }

    /* Insert a Record to the cache */
    public void InsertRecord(DNSQuestion question, DNSRecord answer)
    {
        if (!localCache.containsKey(question))
        {
            localCache.put(question, answer);
        }
    }

}
