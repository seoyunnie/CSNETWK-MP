package seoyunnie.pokeprotocol.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import seoyunnie.pokeprotocol.game.BattlePokemon;
import seoyunnie.pokeprotocol.gui.battle.BattlePanel;

public class BattleFrame extends JFrame {
    private final BattlePanel battlePanel;
    private JPanel hudPanel;

    public BattleFrame(BattlePokemon ownPokemon, BattlePokemon enemyPokemon, JPanel initialHUDPanel) {
        super("Pokémon Battle");

        this.battlePanel = new BattlePanel(ownPokemon, enemyPokemon);
        this.hudPanel = initialHUDPanel;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        add(battlePanel, BorderLayout.PAGE_START);
        add(initialHUDPanel, BorderLayout.PAGE_END);

        pack();
        setResizable(false);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setHUDPanel(JPanel hudPanel) {
        if (this.hudPanel != null) {
            remove(this.hudPanel);
        }

        this.hudPanel = hudPanel;

        add(hudPanel, BorderLayout.PAGE_END);

        revalidate();
        repaint();
    }

    // @Override
    // public void repaint() {
    // super.repaint();

    // battlePanel.repaint();
    // }
}
