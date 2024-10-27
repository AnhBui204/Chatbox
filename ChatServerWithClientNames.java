package rr;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class ChatServerWithClientNames {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static JTextArea textArea;

    public static void main(String[] args) {
        int port = 12345;

        JFrame frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        frame.setVisible(true);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            textArea.append("Chat Server is listening on port " + port + "\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                textArea.append("New client connected\n");

                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName; // Store the client's name

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Request and store the client's name
                out.println("Enter your name:");
                clientName = in.readLine();
                textArea.append(clientName + " joined the chat\n");

                String message;
                while ((message = in.readLine()) != null) {
                    textArea.append(clientName + ": " + message + "\n");
                    broadcast(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                    clients.remove(this);
                    textArea.append(clientName + " disconnected\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.sendMessage(message);
                }
            }
        }

        private void sendMessage(String message) {
            out.println(message);
        }
    }
}
