package net.minestom.server.event.player;

import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.utils.Position;

/**
 * Called when {@link Player#respawn()} is executed (for custom respawn or as a result of
 * {@link net.minestom.server.network.packet.client.play.ClientStatusPacket}
 */
public class PlayerRespawnEvent extends Event {

    private Player player;
    private Position respawnPosition;

    public PlayerRespawnEvent(Player player, Position respawnPosition) {
        this.player = player;
        this.respawnPosition = respawnPosition;
    }

    /**
     * Get the player who is respawning
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the respawn position
     *
     * @return the respawn position
     */
    public Position getRespawnPosition() {
        return respawnPosition;
    }

    /**
     * Change the respawn position
     *
     * @param respawnPosition the new respawn position
     */
    public void setRespawnPosition(Position respawnPosition) {
        this.respawnPosition = respawnPosition;
    }
}
