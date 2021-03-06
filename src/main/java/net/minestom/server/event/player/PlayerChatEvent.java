package net.minestom.server.event.player;

import net.kyori.text.TextComponent;
import net.minestom.server.entity.Player;
import net.minestom.server.event.CancellableEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * Called every time a player write and send something in the chat.
 * The event can be cancelled to do not send anything, and the format can be changed
 */
public class PlayerChatEvent extends CancellableEvent {

    private Player sender;
    private Collection<Player> recipients;
    private String message;
    private Function<PlayerChatEvent, TextComponent> chatFormat;

    public PlayerChatEvent(Player sender, Collection<Player> recipients, String message) {
        this.sender = sender;
        this.recipients = new ArrayList<>(recipients);
        this.message = message;
    }

    /**
     * @param chatFormat the custom chat format
     */
    public void setChatFormat(Function<PlayerChatEvent, TextComponent> chatFormat) {
        this.chatFormat = chatFormat;
    }

    /**
     * @return the sender of the message
     */
    public Player getSender() {
        return sender;
    }

    /**
     * This is all the players who will receive the message
     * It can be modified to add or remove recipient
     *
     * @return a modifiable list of message targets
     */
    public Collection<Player> getRecipients() {
        return recipients;
    }

    /**
     * @return the sender's message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Used to change the message
     *
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Used to retrieve the chat format for this message.
     * If null, the default format will be used
     *
     * @return the chat format which will be used
     */
    public Function<PlayerChatEvent, TextComponent> getChatFormatFunction() {
        return chatFormat;
    }
}
