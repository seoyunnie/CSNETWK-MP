package seoyunnie.pokeprotocol.gui.chat;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;

public class MessageInputPanel extends JPanel {
    private final JTextField inputField = new JTextField();
    private final JButton sendButton = new JButton("Send");

    public MessageInputPanel() {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Message Chat"));
        setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(ChatFrame.MARGIN, ChatFrame.MARGIN, ChatFrame.MARGIN, ChatFrame.MARGIN / 2);
        constraints.weightx = 1.0;

        add(inputField, constraints);

        sendButton.setBackground(Color.BLUE);
        sendButton.setForeground(Color.WHITE);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx++;
        constraints.insets.left = ChatFrame.MARGIN / 2;
        constraints.insets.right = ChatFrame.MARGIN;
        constraints.weightx = 0.0;

        add(sendButton, constraints);
    }

    public void setDefaultButton(JRootPane rootPane) {
        rootPane.setDefaultButton(sendButton);
    }

    public void setSendButtonListener(Consumer<JTextField> cb) {
        sendButton.addActionListener((e) -> cb.accept(inputField));
    }
}
