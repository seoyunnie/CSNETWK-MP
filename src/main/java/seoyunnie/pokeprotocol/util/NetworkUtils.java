package seoyunnie.pokeprotocol.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NetworkUtils {
    private static InetAddress address;
    private static InetAddress broadcastAddress;

    private NetworkUtils() {}

    public static Optional<InetAddress> getAddress() {
        if (address != null) {
            return Optional.of(address);
        }

        try {
            Enumeration<NetworkInterface> netIfaces = NetworkInterface.getNetworkInterfaces();

            while (netIfaces.hasMoreElements()) {
                NetworkInterface netIface = netIfaces.nextElement();

                if (netIface.isLoopback() || !netIface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = netIface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();

                    if (!address.isLoopbackAddress() && address instanceof Inet4Address
                            && address.isSiteLocalAddress()) {
                        return Optional.of(address);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static Optional<InetAddress> getBroadcastAddress() {
        if (broadcastAddress != null) {
            return Optional.of(broadcastAddress);
        }

        try {
            Enumeration<NetworkInterface> netIfaces = NetworkInterface.getNetworkInterfaces();

            while (netIfaces.hasMoreElements()) {
                NetworkInterface netIface = netIfaces.nextElement();

                if (netIface.isLoopback() || !netIface.isUp()) {
                    continue;
                }

                for (InterfaceAddress addr : netIface.getInterfaceAddresses()) {
                    broadcastAddress = addr.getBroadcast();

                    if (broadcastAddress != null) {
                        return Optional.of(broadcastAddress);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static boolean isAvailablePort(int port) {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(port);

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Map<String, String> getMessageEntries(String msg) {
        var msgEntries = new HashMap<String, String>();

        for (String line : msg.split("\n")) {
            int sepIdx = line.indexOf(": ");

            msgEntries.put(line.substring(0, sepIdx).trim(), line.substring(sepIdx + 2).trim());

        }

        return msgEntries;
    }

    public static Map<String, String> getMessageEntries(DatagramPacket packet) {
        return getMessageEntries(new String(packet.getData(), packet.getOffset(), packet.getLength()));
    }

    public static Map<String, String> getMessageEntries(StringBuilder strBuilder) {
        return getMessageEntries(strBuilder.toString());
    }
}
