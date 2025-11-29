package seoyunnie.pokeprotocol.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import java.util.function.Predicate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import seoyunnie.pokeprotocol.validator.IntegerDocumentFilter;

public class HostNetworkInputPanel extends JPanel {
    private final JTextField addrInField = new JTextField(HostNetworkInfoDialog.TEXT_FIELD_LENGTH);
    private final JTextField portInField = new JTextField(HostNetworkInfoDialog.TEXT_FIELD_LENGTH);

    public HostNetworkInputPanel() {
        ((PlainDocument) portInField.getDocument()).setDocumentFilter(new IntegerDocumentFilter());

        setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 5, 2);

        add(new JLabel("IP Address"), constraints);

        constraints.gridy++;
        constraints.insets.top = 5;
        constraints.insets.bottom = 0;

        add(new JLabel("Port"), constraints);

        constraints.gridx++;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 2, 5, 0);

        add(addrInField, constraints);

        constraints.gridy++;
        constraints.insets.top = 5;
        constraints.insets.bottom = 0;

        add(portInField, constraints);
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(addrInField.getText()).filter(Predicate.not(String::isEmpty));
    }

    public Optional<Integer> getPort() {
        String portStr = portInField.getText();

        if (portStr == null || portStr.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(Integer.parseInt(portStr));
    }
}
