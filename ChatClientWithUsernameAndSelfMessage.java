package gg;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClientWithUsernameAndSelfMessage {
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static JTextArea textArea;
    private static JTextField messageField;
    private static String username;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);

        frame.setVisible(true);

        try {
            clientSocket = new Socket("localhost", 12345);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        username = JOptionPane.showInputDialog("Enter your username:");
        out.println(username);

        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });

        while (true) {
            try {
                String message = in.readLine();
                textArea.append(message + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendMessage(String message) {
        textArea.append("You: " + message + "\n"); // Display the message locally
        out.println(username + ": " + message); // Send the message to the server
    }
}
