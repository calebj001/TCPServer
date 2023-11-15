import java.io.*;
import java.net.*;

public class myFirstTCPClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java myFirstTCPClient <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            Socket clientSocket = new Socket(hostname, port);
            System.out.println("Connected to server: " + hostname + ":" + port);

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

            long minRTT = Long.MAX_VALUE;
            long maxRTT = Long.MIN_VALUE;
            long totalRTT = 0;

            for (int i = 0; i < 7; i++) {
                System.out.print("Enter a sentence (0 to 216-1): ");
                String sentence = inFromUser.readLine();

                // Convert sentence to byte array
                byte[] sentenceBytes = sentence.getBytes("UTF-16BE");

                // Display byte per byte in hexadecimal
                System.out.print("Sent bytes: ");
                for (byte b : sentenceBytes) {
                    System.out.print(String.format("0x%02X ", b));
                }
                System.out.println();

                // Send sentence to server
                outToServer.write(sentenceBytes);

                // Receive response from server
                short response = inFromServer.readShort();

                // Measure RTT
                long startTime = System.currentTimeMillis();
                long endTime;

                // Display received bytes
                System.out.print("Received bytes: ");
                System.out.print(String.format("0x%02X ", (byte) (response >> 8)));
                System.out.print(String.format("0x%02X ", (byte) (response & 0xFF)));
                System.out.println();

                endTime = System.currentTimeMillis();
                long rtt = endTime - startTime;

                // Update min, max, and total RTT
                minRTT = Math.min(minRTT, rtt);
                maxRTT = Math.max(maxRTT, rtt);
                totalRTT += rtt;

                System.out.println("Received response: " + response + " (RTT: " + rtt + "ms)");
            }

            // Calculate average RTT
            long avgRTT = totalRTT / 7;

            System.out.println("Min RTT: " + minRTT + "ms");
            System.out.println("Max RTT: " + maxRTT + "ms");
            System.out.println("Average RTT: " + avgRTT + "ms");

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
