package net.minestom.server.listener;

import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.serializer.plain.PlainComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.chat.Chat;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.network.packet.client.play.ClientChatMessagePacket;

import java.util.Collection;
import java.util.function.Function;

public class ChatMessageListener {

    public static void listener(ClientChatMessagePacket packet, Player player) {
        String message = PlainComponentSerializer.INSTANCE.serialize(Chat.fromLegacyText(packet.message));

        CommandManager commandManager = MinecraftServer.getCommandManager();
        String cmdPrefix = commandManager.getCommandPrefix();
        if (message.startsWith(cmdPrefix)) {
            // The message is a command
            message = message.replaceFirst(cmdPrefix, "");

            PlayerCommandEvent playerCommandEvent = new PlayerCommandEvent(player, message);
            player.callCancellableEvent(PlayerCommandEvent.class, playerCommandEvent, () -> {
                commandManager.execute(player, playerCommandEvent.getCommand());
            });

            // Do not call chat event
            return;
        }


        Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
        PlayerChatEvent playerChatEvent = new PlayerChatEvent(player, players, message);

        // Call the event
        player.callCancellableEvent(PlayerChatEvent.class, playerChatEvent, () -> {

            Function<PlayerChatEvent, TextComponent> formatFunction = playerChatEvent.getChatFormatFunction();

            TextComponent textObject;

            if (formatFunction != null) {
                // Custom format
                textObject = formatFunction.apply(playerChatEvent);
            } else {
                // Default format
                textObject = buildDefaultChatMessage(playerChatEvent);
            }

            for (Player recipient : playerChatEvent.getRecipients()) {
                recipient.sendMessage(textObject);
            }

        });

    }

    private static TextComponent buildDefaultChatMessage(PlayerChatEvent chatEvent) {
        String username = chatEvent.getSender().getUsername();

        TextComponent usernameText = TextComponent.of(String.format("<%s>", username))
                .color(TextColor.WHITE)
                .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Its " + username).color(TextColor.GRAY)))
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + username + " "))
                .append(TextComponent.of(" " + chatEvent.getMessage()));

        return usernameText;
    }

}
