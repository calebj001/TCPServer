import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java myFirstTCPServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                DataInputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

                while (true) {
                    // Create a byte array to receive the client's packet
                    byte[] receiveData = new byte[1024];
                    short bytesRead = (short) inFromClient.read(receiveData);

                    if (bytesRead == -1) {
                        // End of stream reached, client disconnected
                        break;
                    }

                    // Display received bytes
                    System.out.print("Received bytes: ");
                    for (int j = 0; j < bytesRead; j++) {
                        System.out.print(String.format("0x%02X ", receiveData[j]));
                    }
                    System.out.println();

                    // Convert bytes to sentence (short integer)
                    String sentence = new String(receiveData, 0, bytesRead, "UTF-16BE");

                    try {
                        short response = Short.parseShort(sentence);
                        // Send response to client
                        outToClient.writeShort(response);
                        System.out.println("Sent response: " + response);
                    } catch (NumberFormatException e) {
                        // Handle non-numeric message
                        String errorMessage = "Invalid input. Only decimal numbers are supported.";
                        byte[] errorBytes = errorMessage.getBytes("UTF-16BE");
                        outToClient.write(errorBytes);
                        System.out.println("Sent error response: " + errorMessage);
                    }
                }

                clientSocket.close();
                System.out.println("Client disconnected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
