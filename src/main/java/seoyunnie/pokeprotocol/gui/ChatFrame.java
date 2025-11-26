package seoyunnie.pokeprotocol.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import seoyunnie.pokeprotocol.network.ChatClient;

public class ChatFrame extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 426;

    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");

    private final JTextArea historyArea = new JTextArea(10, 1);
    private final JScrollPane historyPane = new JScrollPane(historyArea);

    private final ChatClient client;

    public ChatFrame(String username, InetAddress broadcastAddr) throws SocketException {
        super("Global Chat");

        this.client = new ChatClient(broadcastAddr, username);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        initComponents();
        addListeners();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        setResizable(false);

        client.startChatMessageListener((m) -> SwingUtilities.invokeLater(
                () -> historyArea.append(m.senderName() + ": " + m.messageText() + "\n")));
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 4, 10);
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;

        add(new JLabel("Message Global Chat"), constraints);

        constraints.gridy++;
        constraints.insets.top = 3;
        constraints.insets.right = 5;
        constraints.insets.bottom = 10;
        constraints.gridwidth = 1;

        add(inputField, constraints);

        sendButton.setBackground(Color.BLUE);
        sendButton.setForeground(Color.WHITE);

        constraints.gridx++;
        constraints.insets.left = 5;
        constraints.insets.right = 10;
        constraints.weightx = 0.1;

        add(sendButton, constraints);
        getRootPane().setDefaultButton(sendButton);

        constraints.gridx--;
        constraints.gridy++;
        constraints.insets.left = 10;
        constraints.insets.bottom = 3;
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;

        add(new JLabel("Chat History"), constraints);

        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setEditable(false);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy++;
        constraints.insets.top = 3;
        constraints.insets.bottom = 10;
        constraints.weighty = 1.0;

        add(historyPane, constraints);
    }

    private void addListeners() {
        sendButton.addActionListener((evt) -> {
            String msg = inputField.getText();

            if (msg.isEmpty()) {
                return;
            }

            try {
                client.sendChatMessage(msg);

                SwingUtilities.invokeLater(() -> inputField.setText(""));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "The message could not be sent.", "Failed to Send",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                client.stopChatMessageListener();

                ChatFrame.this.dispose();
            };
        });
    }
}
