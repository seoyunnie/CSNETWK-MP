package seoyunnie.pokeprotocol.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.InetAddress;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class HostNetworkInfoDialog extends JDialog {
    public static final int TEXT_FIELD_LENGTH = 15;

    public HostNetworkInfoDialog(InetAddress addr, int port) {
        super((JFrame) null, "Network Info");

        setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 5, 2);

        add(new JLabel("IP Address"), constraints);

        constraints.gridy++;
        constraints.insets.top = 5;
        constraints.insets.bottom = 10;

        add(new JLabel("Port"), constraints);

        var addrField = new JTextField(addr.getHostAddress(), TEXT_FIELD_LENGTH);
        addrField.setEditable(false);

        constraints.gridx++;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 2, 5, 10);

        add(addrField, constraints);

        var portField = new JTextField(Integer.toString(port), TEXT_FIELD_LENGTH);
        portField.setEditable(false);

        constraints.gridy++;
        constraints.insets.top = 5;
        constraints.insets.bottom = 10;

        add(portField, constraints);

        pack();
        setResizable(false);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
