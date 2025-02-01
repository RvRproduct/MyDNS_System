import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class DNSServer 
{
    public static void RunDNSServer() throws IOException
    {
        int serverPort = 5678;
        System.out.println("Listening at " + serverPort);
        DatagramSocket socket = new DatagramSocket(serverPort);

        byte[] buffer = new byte[512];
        DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
        for (int count = 1; true; count++)
        {
            socket.receive(pkt);

            System.out.println(count + " Heard from " + pkt.getAddress() + " " + pkt.getPort());
            for (int i = 0; i < pkt.getLength(); i++)
            {
                System.out.printf(" %d", buffer[i]);
            }
            System.out.print("\n");
        }
    }
}
