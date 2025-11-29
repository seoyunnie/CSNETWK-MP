package seoyunnie.pokeprotocol.gui.battle;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import seoyunnie.pokeprotocol.game.BattlePokemon;

public class BattleFrame extends JFrame {
    private final BattlePanel battlePanel;
    private JPanel hudPanel;

    public BattleFrame(BattlePokemon ownPokemon, BattlePokemon enemyPokemon, JPanel initialHUDPanel) {
        super("Pokémon Battle");

        this.battlePanel = new BattlePanel(ownPokemon, enemyPokemon);
        this.hudPanel = initialHUDPanel;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        add(battlePanel, BorderLayout.PAGE_START);
        add(hudPanel, BorderLayout.PAGE_END);

        pack();
        setResizable(false);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setHUDPanel(JPanel hudPanel) {
        remove(this.hudPanel);

        this.hudPanel = hudPanel;

        add(hudPanel, BorderLayout.PAGE_END);

        revalidate();
        repaint();
    }
}
