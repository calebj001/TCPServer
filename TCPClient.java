import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient {
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

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

            // Instantiate return time values for later use
            long minRTT = Long.MAX_VALUE;
            long maxRTT = Long.MIN_VALUE;
            long totalRTT = 0;
            int count = 0;
            long avgRTT = 0;
            
            while (true) {
                // Prompt the user for a sentence
                System.out.print("Enter an integer between -32768 and 32767: ");
                Scanner input = new Scanner(System.in);
                Short num = input.nextShort();

                // Convert short 'num' to bytes
                byte[] sendData = new byte[] {(byte)((num & 0xFF00) >> 8), (byte)(num & 0x00FF)};

                // Print num after converting to bytes prior to sending request
                System.out.print("Byte stream being sent: ");
                for (byte b : sendData) {
                    String st = String.format("%02X", b);
                    System.out.print("0x" + st + " ");
                }

                // Measure RTT
                long startTime = System.currentTimeMillis();
            
                // Send sentence to server
                outToServer.write(sendData);

                // Receive response from server
                short response = inFromServer.readShort();

                long endTime;
                count++;

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
        
                /* Prompts user to enter Y (yes) or N (no) depending on if they wish 
                to continue sending from client */
                System.out.print("Do you wish to continue? Y/N: ");
                String exitCode = input.next().toUpperCase();
    
                // Closes scanner and exits loop if user wishes to stop (enters N)
                if (exitCode != null && exitCode.contains("N")) {
                    System.out.println("Exiting");
                    input.close();
                    break;
                }
            }
            // Calculate average RTT
            avgRTT = totalRTT / count;

            System.out.println("Min RTT: " + minRTT + "ms");
            System.out.println("Max RTT: " + maxRTT + "ms");
            System.out.println("Average RTT: " + avgRTT + "ms");

            clientSocket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}