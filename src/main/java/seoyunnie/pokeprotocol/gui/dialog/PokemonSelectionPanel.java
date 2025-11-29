package seoyunnie.pokeprotocol.gui.dialog;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import seoyunnie.pokeprotocol.pokemon.GamePokemon;
import seoyunnie.pokeprotocol.pokemon.Pokemon;

public class PokemonSelectionPanel extends JPanel {
    private final JComboBox<String> selection = new JComboBox<>(GamePokemon.names());

    public PokemonSelectionPanel() {
        setLayout(new GridBagLayout());

        var cardPanel = new JPanel(new CardLayout());

        for (Pokemon pokemon : GamePokemon.values()) {
            cardPanel.add(new PokemonDetailsPanel(pokemon), pokemon.name());
        }

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 5, 0);

        add(cardPanel, constraints);

        selection.setEditable(false);

        selection.addItemListener((evt) -> {
            CardLayout layout = (CardLayout) cardPanel.getLayout();

            layout.show(cardPanel, (String) evt.getItem());
        });

        constraints.gridy++;
        constraints.insets.top = 5;
        constraints.insets.bottom = 0;

        add(selection, constraints);
    }

    public Pokemon getPokemon() {
        return GamePokemon.getByName((String) selection.getSelectedItem()).get();
    }
}
