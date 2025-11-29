package seoyunnie.pokeprotocol.gui.battle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import seoyunnie.pokeprotocol.move.Move;
import seoyunnie.pokeprotocol.type.TypeEffectiveness;

public class HUDTurnPanel extends JPanel {
    private final HUDMessagePanel messagePanel = new HUDMessagePanel();
    private MoveButton[] moveButtons = new MoveButton[4];

    public HUDTurnPanel(String pokemonName, Map<Move, TypeEffectiveness> moveEffectivenessMap) {
        setBackground(Color.BLACK);

        setBorder(new EmptyBorder(HUDPanel.MARGIN, HUDPanel.MARGIN, HUDPanel.MARGIN, HUDPanel.MARGIN));
        setLayout(new GridLayout(1, 2, HUDPanel.MARGIN, HUDPanel.MARGIN));

        messagePanel.setMessage((graphics2d) -> {
            graphics2d.drawString("What will", 25, 40);
            graphics2d.drawString(pokemonName + " do?", 25, 80);
        });

        add(messagePanel);

        var moveSelPanel = new JPanel();
        moveSelPanel.setBackground(getBackground());
        moveSelPanel.setLayout(new GridLayout(2, 2, HUDPanel.MARGIN, HUDPanel.MARGIN));

        int moveIdx = 0;

        for (Map.Entry<Move, TypeEffectiveness> entry : moveEffectivenessMap.entrySet()) {
            moveButtons[moveIdx] = new MoveButton(entry.getKey(), entry.getValue());

            moveSelPanel.add(moveButtons[moveIdx++]);
        }

        add(moveSelPanel);

        setPreferredSize(new Dimension(HUDPanel.WIDTH, HUDPanel.HEIGHT));
    }

    public void setMoveButtonListener(BiConsumer<Move, TypeEffectiveness> cb) {
        for (MoveButton btn : moveButtons) {
            btn.addActionListener((e) -> cb.accept(btn.getMove(), btn.getMoveEffectiveness()));
        }
    }
}
