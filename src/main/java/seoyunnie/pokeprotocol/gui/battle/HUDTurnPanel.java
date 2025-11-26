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
    protected final HUDMessagePanel messagePanel = new HUDMessagePanel();
    private MoveButton[] moveButtons = new MoveButton[4];

    private final String pokemonName;
    private final Map<Move, TypeEffectiveness> moveEffectivenessMap;

    public HUDTurnPanel(String pokemonName, Map<Move, TypeEffectiveness> moveEffectivenessMap) {
        this.pokemonName = pokemonName;
        this.moveEffectivenessMap = moveEffectivenessMap;

        setBackground(Color.BLACK);

        initComponents();

        setPreferredSize(new Dimension(HUDPanel.WIDTH, HUDPanel.HEIGHT));
    }

    public void initComponents() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new GridLayout(1, 2, 5, 5));

        messagePanel.setMessage((graphics2d) -> {
            graphics2d.drawString("What will", 25, 40);
            graphics2d.drawString(pokemonName + " do?", 25, 80);
        });

        add(messagePanel);

        var moveSelectionPanel = new JPanel();

        moveSelectionPanel.setBackground(getBackground());
        moveSelectionPanel.setLayout(new GridLayout(2, 2, 5, 5));

        int moveIdx = 0;

        for (Map.Entry<Move, TypeEffectiveness> entry : moveEffectivenessMap.entrySet()) {
            var moveBtn = new MoveButton(entry.getKey(), entry.getValue());

            moveButtons[moveIdx++] = moveBtn;
            moveSelectionPanel.add(moveBtn);
        }

        add(moveSelectionPanel);
    }

    public void addMoveButtonListener(BiConsumer<Move, TypeEffectiveness> cb) {
        for (MoveButton btn : moveButtons) {
            btn.addActionListener((e) -> cb.accept(btn.getMove(), btn.getMoveEffectiveness()));
        }
    }
}
