package seoyunnie.pokeprotocol.gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import seoyunnie.pokeprotocol.game.BattlePokemon;
import seoyunnie.pokeprotocol.gui.battle.BattlePanel;

public class GamePanel extends JPanel {
    private final BattlePanel battlePanel;
    private JPanel hudPanel;

    public GamePanel(BattlePokemon ownPokemon, BattlePokemon enemyPokemon, JPanel initialHUDPanel) {
        this.battlePanel = new BattlePanel(ownPokemon, enemyPokemon);
        this.hudPanel = initialHUDPanel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(battlePanel, BorderLayout.PAGE_START);
        add(hudPanel, BorderLayout.PAGE_END);
    }

    public void setHUDPanel(JPanel hudPanel) {
        remove(this.hudPanel);

        this.hudPanel = hudPanel;

        add(hudPanel, BorderLayout.PAGE_END);

        revalidate();
        repaint();
    }
}
