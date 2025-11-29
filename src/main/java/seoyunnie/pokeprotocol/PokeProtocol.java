package seoyunnie.pokeprotocol;

import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

import seoyunnie.pokeprotocol.game.GameManager;
import seoyunnie.pokeprotocol.gui.chat.ChatFrame;
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

        String username = JOptionPane.showInputDialog(null, "Input your preferred username:", "Login",
                JOptionPane.PLAIN_MESSAGE);

        if (username == null) {
            return;
        } else if (username.isBlank()) {
            username = "Anonymous";
        }

        ChatFrame chatFrame;

        try {
            chatFrame = new ChatFrame(username, broadcastAddr);
        } catch (SocketException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "A network-related issue was encountered.", "Network Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        new GameManager().accept(chatFrame);
    }
}
