package seoyunnie.pokeprotocol;

import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

import seoyunnie.pokeprotocol.game.GameManager;
import seoyunnie.pokeprotocol.util.NetworkUtils;

public class PokeProtocol {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("OptionPane.border", BorderFactory.createEmptyBorder(10, 10, 10, 10));

        InetAddress broadcastAddr = NetworkUtils.getBroadcastAddress().orElse(null);

        if (broadcastAddr == null) {
            JOptionPane.showMessageDialog(null, "The broadcast address could not be found.",
                    "Unknown Broadcast Address", JOptionPane.ERROR_MESSAGE);

            return;
        }

        new GameManager().run();
    }
}
