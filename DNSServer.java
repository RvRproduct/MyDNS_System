import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class DNSServer 
{
    private static final int SERVER_PORT = 5678;
    private static final String GOOGLE_DNS = "8.8.8.8";
    private static final int GOOGLE_DNS_PORT = 53;
    private static final int BUFFER_SIZE = 512;

    private static final DNSCache CACHE = new DNSCache();

    public static void RunDNSServer() throws IOException
    {
        DatagramSocket socket = new DatagramSocket(SERVER_PORT);
        System.out.println("DNS Server listening on port " + SERVER_PORT);

        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) 
        {
            DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(requestPacket);
            System.out.println("Received DNS query from " + requestPacket.getAddress() + ":" + requestPacket.getPort());

            DNSMessage requestMessage = DNSMessage.DecodeMessage(Arrays.copyOf(requestPacket.getData(), requestPacket.getLength()));

            DNSRecord cachedRecord = CACHE.QueryRecord(requestMessage.questions[0]);
            if (cachedRecord != null)
            {
                System.out.println("Found response in CACHE: sending CACHED response");
                SendResponse(socket, requestPacket, requestMessage, new DNSRecord[]{cachedRecord});
                continue;
            }

            System.out.println("Not Found no response in CACHE: forwarding query to Google DNS");
            byte[] responseBytes = ForwardQueryToGoogle(requestPacket.getData(), requestPacket.getLength());

            DNSMessage responseMessage = DNSMessage.DecodeMessage(responseBytes);

            if (responseMessage.answers.length > 0)
            {
                CACHE.InsertRecord(requestMessage.questions[0], responseMessage.answers[0]);
            }

            SendResponse(socket, requestPacket, responseMessage, responseMessage.answers);
        }
    }

    private static byte[] ForwardQueryToGoogle(byte[] queryData, int length) throws IOException
    {
        DatagramSocket googleSocket = new DatagramSocket();
        InetAddress googleAddress = InetAddress.getByName(GOOGLE_DNS);

        DatagramPacket googleRequest = new DatagramPacket(queryData, length, googleAddress, GOOGLE_DNS_PORT);
        googleSocket.send(googleRequest);

        byte[] responseBuffer = new byte[BUFFER_SIZE];
        DatagramPacket googleResponse = new DatagramPacket(responseBuffer, responseBuffer.length);
        googleSocket.receive(googleResponse);

        googleSocket.close();

        return Arrays.copyOf(responseBuffer, googleResponse.getLength());
    }

    private static void SendResponse(DatagramSocket socket,
    DatagramPacket requestPacket,
    DNSMessage requestMessage,
    DNSRecord[] answers) throws IOException
    {
        // Creating a DNS Response
        DNSMessage responseMessage = DNSMessage.BuildResponse(requestMessage, answers);

        byte[] responseBytes = responseMessage.ToBytes();

        DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, requestPacket.getAddress(), requestPacket.getPort());
        socket.send(responsePacket);
    }
}
