package net.minestom.server.network;

import net.kyori.text.TextComponent;
import net.minestom.server.entity.Player;
import net.minestom.server.listener.manager.PacketConsumer;
import net.minestom.server.network.player.PlayerConnection;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConnectionManager {

    private Set<Player> players = new CopyOnWriteArraySet<>();
    private Map<PlayerConnection, Player> connectionPlayerMap = Collections.synchronizedMap(new HashMap<>());

    private List<PacketConsumer> packetConsumers = new CopyOnWriteArrayList<>();
    private UuidProvider uuidProvider;
    private List<Consumer<Player>> playerInitializations = new CopyOnWriteArrayList<>();

    /**
     * @param connection the player connection
     * @return the {@link Player} linked to the conneciton
     */
    public Player getPlayer(PlayerConnection connection) {
        return connectionPlayerMap.get(connection);
    }

    /**
     * @return an unmodifiable collection containing all the online players
     */
    public Collection<Player> getOnlinePlayers() {
        return Collections.unmodifiableCollection(players);
    }

    /**
     * @param username the player username (ignoreCase)
     * @return the first player who validate the username condition
     */
    public Player getPlayer(String username) {
        for (Player player : getOnlinePlayers()) {
            if (player.getUsername().equalsIgnoreCase(username))
                return player;
        }
        return null;
    }

    /**
     * Send a message to all online players who validate the condition {@code condition}
     *
     * @param textComponent the message to send
     * @param condition     the condition to receive the message
     */
    public void broadcastMessage(TextComponent textComponent, Function<Player, Boolean> condition) {
        if (condition == null) {
            getOnlinePlayers().forEach(player -> player.sendMessage(textComponent));
        } else {
            getOnlinePlayers().forEach(player -> {
                boolean result = condition.apply(player);
                if (result)
                    player.sendMessage(textComponent);
            });
        }
    }

    /**
     * Send a message to all online players without exception
     *
     * @param textComponent the message to send
     */
    public void broadcastMessage(TextComponent textComponent) {
        broadcastMessage(textComponent, null);
    }

    /**
     * Those are all the listeners which are called for each packet received
     *
     * @return an unmodifiable list of packet's consumers
     */
    public List<PacketConsumer> getPacketConsumers() {
        return Collections.unmodifiableList(packetConsumers);
    }

    /**
     * Add a new packet listener
     *
     * @param packetConsumer the packet consumer
     */
    public void addPacketConsumer(PacketConsumer packetConsumer) {
        this.packetConsumers.add(packetConsumer);
    }

    /**
     * Shouldn't be override if already defined
     *
     * @param uuidProvider the new player connection uuid provider
     */
    public void setUuidProvider(UuidProvider uuidProvider) {
        this.uuidProvider = uuidProvider;
    }

    /**
     * Compute the UUID of the specified connection
     * Used in {@link net.minestom.server.network.packet.client.login.LoginStartPacket} in order
     * to give the player the right UUID
     *
     * @param playerConnection the player connection
     * @return the uuid based on {@code playerConnection}
     * return a random UUID if no UUID provider is defined see {@link #setUuidProvider(UuidProvider)}
     */
    public UUID getPlayerConnectionUuid(PlayerConnection playerConnection) {
        if (uuidProvider == null)
            return UUID.randomUUID();
        return uuidProvider.provide(playerConnection);
    }

    /**
     * Those are all the consumers called when a new player join
     *
     * @return an unmodifiable list containing all the player initialization consumer
     */
    public List<Consumer<Player>> getPlayerInitializations() {
        return Collections.unmodifiableList(playerInitializations);
    }

    /**
     * Add a new player initialization consumer. Those are called when a player join,
     * mainly to add event callbacks to the player
     *
     * @param playerInitialization the player initialization consumer
     */
    public void addPlayerInitialization(Consumer<Player> playerInitialization) {
        this.playerInitializations.add(playerInitialization);
    }

    /**
     * Add a new player in the players list
     * Is currently used at
     * {@link net.minestom.server.network.packet.client.login.LoginStartPacket#process(PlayerConnection, ConnectionManager)}
     * and in {@link net.minestom.server.entity.fakeplayer.FakePlayer#FakePlayer(UUID, String, boolean)}
     *
     * @param player the player to add
     */
    public void createPlayer(Player player) {
        this.players.add(player);
        this.connectionPlayerMap.put(player.getPlayerConnection(), player);
    }

    /**
     * Create a player object and register it
     *
     * @param uuid       the new player uuid
     * @param username   the new player username
     * @param connection the new player connection
     */
    public void createPlayer(UUID uuid, String username, PlayerConnection connection) {
        Player player = new Player(uuid, username, connection);
        createPlayer(player);
    }

    /**
     * Remove a player from the players list
     * used at player disconnection
     *
     * @param connection the player connection
     */
    public void removePlayer(PlayerConnection connection) {
        Player player = this.connectionPlayerMap.get(connection);
        if (player == null)
            return;

        this.players.remove(player);
        this.connectionPlayerMap.remove(player);
    }
}
