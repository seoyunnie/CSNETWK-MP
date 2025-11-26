package seoyunnie.pokeprotocol.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NetworkUtils {
    private NetworkUtils() {
    }

    public static Optional<InetAddress> getAddress() {
        try (var s = new Socket()) {
            s.connect(new InetSocketAddress("google.com", 80));

            return Optional.of(s.getLocalAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<InetAddress> getBroadcastAddress() {
        try {
            Enumeration<NetworkInterface> netIfaces = NetworkInterface.getNetworkInterfaces();

            while (netIfaces.hasMoreElements()) {
                NetworkInterface netIface = netIfaces.nextElement();

                if (netIface.isLoopback() || !netIface.isUp()) {
                    continue;
                }

                for (InterfaceAddress addr : netIface.getInterfaceAddresses()) {
                    InetAddress broadcastAddr = addr.getBroadcast();

                    if (broadcastAddr != null) {
                        return Optional.of(broadcastAddr);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Map<String, String> getMessageEntries(DatagramPacket packet) {
        var msgEntries = new HashMap<String, String>();

        for (String line : new String(packet.getData(), packet.getOffset(), packet.getLength()).split("\n")) {
            int sepIdx = line.indexOf(": ");

            msgEntries.put(line.substring(0, sepIdx).trim(), line.substring(sepIdx + 2).trim());

        }

        return msgEntries;
    }
}
