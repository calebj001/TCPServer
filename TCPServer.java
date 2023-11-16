import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

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
            
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            while (true) {
                DataInputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

                // Create a byte array to receive the client's packet
                byte[] receiveData = inFromClient.readNBytes(2);

                if (receiveData.length != 2) {
                    String error = "****";
                    outToClient.writeUTF(error);
                }
                // Valid packet received
                else {
                    // Print data in hexadecimal format
                    System.out.print("Byte stream received: ");
                    for (int i = 0; i < receiveData.length; i++) {
                    String st = String.format("%02X", receiveData[i]);
                    System.out.print("0x" + st + " ");
                    }

                    // Convert data to integer value
                    ByteBuffer bb = ByteBuffer.allocate(2);
                    bb.order(ByteOrder.BIG_ENDIAN);
                    bb.put(receiveData[0]);
                    bb.put(receiveData[1]);
                    short shortVal = bb.getShort(0);

                    // Display the received integer and client information
                    System.out.println("\nShort Received: " + shortVal + " from: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    // Convert the short integer request back to bytes
                    String responseString = String.valueOf(shortVal);

                    // Encode the response data with UTF-16 standard encoding
                    byte[] responseData = responseString.toString().getBytes(StandardCharsets.UTF_16);

                    // Print encoded response after UTF-16 standard encoding
                    System.out.println("Echo response being sent: ");
                    for (byte b : responseData) {
                        String st = String.format("%02X", b);
                        System.out.print("0x" + st + " ");
                    }
                    
                    try {
                        short response = Short.parseShort(responseString);
                        // Send response to client
                        outToClient.write(responseData);
                        System.out.println("\nSent response: " + response);
                    } catch (NumberFormatException e) {
                        // Handle non-numeric message
                        String errorMessage = "Invalid input. Only decimal numbers are supported.";
                        byte[] errorBytes = errorMessage.getBytes("UTF-16BE");
                        outToClient.write(errorBytes);
                        System.out.println("\nSent error response: " + errorMessage);
                        break;
                    }        
                }
            }
            
            clientSocket.close();
            System.out.println("Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
