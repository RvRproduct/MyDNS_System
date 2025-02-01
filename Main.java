
import java.io.IOException;

public class Main 
{
    public static void main(String[] args) 
    {
        try
        {
            DNSServer.RunDNSServer();
        }
        catch (IOException e)
        {
            System.err.println("An error occurred while running the DNS server: " + e.getMessage());
        }
    }
}
