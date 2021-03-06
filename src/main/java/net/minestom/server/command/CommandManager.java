package net.minestom.server.command;

import fr.themode.command.Command;
import fr.themode.command.CommandDispatcher;
import fr.themode.command.condition.CommandCondition;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import net.minestom.server.utils.ArrayUtils;
import net.minestom.server.utils.validate.Check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    private String commandPrefix = "/";

    private CommandDispatcher<Player> dispatcher = new CommandDispatcher<>();
    private Map<String, CommandProcessor> commandProcessorMap = new HashMap<>();

    public void register(Command<Player> command) {
        this.dispatcher.register(command);
    }

    public void register(CommandProcessor commandProcessor) {
        this.commandProcessorMap.put(commandProcessor.getCommandName().toLowerCase(), commandProcessor);
    }

    public boolean execute(Player source, String command) {
        Check.notNull(source, "Source cannot be null");
        Check.notNull(command, "Command string cannot be null");

        PlayerCommandEvent playerCommandEvent = new PlayerCommandEvent(source, command);
        source.callEvent(PlayerCommandEvent.class, playerCommandEvent);

        if (playerCommandEvent.isCancelled())
            return false;

        command = playerCommandEvent.getCommand();

        try {
            this.dispatcher.execute(source, command);
            return true;
        } catch (NullPointerException e) {
            String[] splitted = command.split(" ");
            String commandName = splitted[0];
            CommandProcessor commandProcessor = commandProcessorMap.get(commandName.toLowerCase());
            if (commandProcessor == null)
                return false;

            String[] args = command.substring(command.indexOf(" ") + 1).split(" ");

            return commandProcessor.process(source, commandName, args);

        }
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public DeclareCommandsPacket createDeclareCommandsPacket(Player player) {
        return buildPacket(player);
    }

    private DeclareCommandsPacket buildPacket(Player player) {
        DeclareCommandsPacket declareCommandsPacket = new DeclareCommandsPacket();

        List<String> commands = new ArrayList<>();
        for (Command<Player> command : dispatcher.getCommands()) {
            CommandCondition<Player> commandCondition = command.getCondition();
            if (commandCondition != null) {
                // Do not show command if return false
                if (!commandCondition.apply(player)) {
                    continue;
                }
            }
            commands.add(command.getName());
            for (String alias : command.getAliases()) {
                commands.add(alias);
            }
        }

        for (CommandProcessor commandProcessor : commandProcessorMap.values()) {
            // Do not show command if return false
            if (!commandProcessor.hasAccess(player))
                continue;

            commands.add(commandProcessor.getCommandName());
            String[] aliases = commandProcessor.getAliases();
            if (aliases == null || aliases.length == 0)
                continue;
            for (String alias : aliases) {
                commands.add(alias);
            }
        }


        List<DeclareCommandsPacket.Node> nodes = new ArrayList<>();
        ArrayList<Integer> rootChildren = new ArrayList<>();

        DeclareCommandsPacket.Node argNode = new DeclareCommandsPacket.Node();
        argNode.flags = 0b10;
        argNode.name = "arg";
        argNode.parser = "brigadier:string";
        argNode.properties = packetWriter -> {
            packetWriter.writeVarInt(0);
        };
        int argOffset = nodes.size();
        nodes.add(argNode);
        argNode.children = new int[]{argOffset};

        for (String commandName : commands) {

            DeclareCommandsPacket.Node literalNode = new DeclareCommandsPacket.Node();
            literalNode.flags = 0b1;
            literalNode.name = commandName;
            literalNode.children = new int[]{argOffset};

            rootChildren.add(nodes.size());
            nodes.add(literalNode);

        }

        DeclareCommandsPacket.Node rootNode = new DeclareCommandsPacket.Node();
        rootNode.flags = 0;
        rootNode.children = ArrayUtils.toArray(rootChildren);

        nodes.add(rootNode);

        declareCommandsPacket.nodes = nodes.toArray(new DeclareCommandsPacket.Node[0]);
        declareCommandsPacket.rootIndex = nodes.size() - 1;

        return declareCommandsPacket;
    }

    /*private void refreshPacket2() {

        List<DeclareCommandsPacket.Node> nodes = new ArrayList<>();
        ArrayList<Integer> rootChildren = new ArrayList<>();

        for (Command<Player> command : dispatcher.getCommands()) {
            ArrayList<Integer> cmdChildren = new ArrayList<>();

            String name = command.getName();

            DeclareCommandsPacket.Node literalNode = new DeclareCommandsPacket.Node();
            literalNode.flags = 0b1;
            literalNode.name = name;

            rootChildren.add(nodes.size());
            nodes.add(literalNode);

            for (CommandSyntax syntax : command.getSyntaxes()) {
                ArrayList<Integer> argChildren = cmdChildren;

                for (Argument argument : syntax.getArguments()) {

                    DeclareCommandsPacket.Node argumentNode = toNode(argument);

                    argChildren.add(nodes.size());
                    nodes.add(argumentNode);
                    System.out.println("size: " + argChildren.size());
                    argumentNode.children = ArrayUtils.toArray(argChildren);
                    argChildren = new ArrayList<>();
                }

            }
            System.out.println("test " + cmdChildren.size() + " : " + cmdChildren.get(0));
            literalNode.children = ArrayUtils.toArray(cmdChildren);

        }


        DeclareCommandsPacket.Node rootNode = new DeclareCommandsPacket.Node();
        rootNode.flags = 0;
        rootNode.children = ArrayUtils.toArray(rootChildren);

        nodes.add(rootNode);

        declareCommandsPacket.nodes = nodes.toArray(new DeclareCommandsPacket.Node[0]);
        declareCommandsPacket.rootIndex = nodes.size() - 1;
    }

    private DeclareCommandsPacket.Node toNode(Argument argument) {
        DeclareCommandsPacket.Node argumentNode = new DeclareCommandsPacket.Node();
        argumentNode.flags = 0b1010;
        argumentNode.name = argument.getId();

        if (argument instanceof ArgumentBoolean) {
            argumentNode.parser = "brigadier:bool";
            argumentNode.properties = packetWriter -> packetWriter.writeByte((byte) 0);
        } else if (argument instanceof ArgumentDouble) {
            ArgumentDouble argumentDouble = (ArgumentDouble) argument;
            argumentNode.parser = "brigadier:double";
            argumentNode.properties = packetWriter -> {
                packetWriter.writeByte((byte) 0b11);
                packetWriter.writeDouble(argumentDouble.min);
                packetWriter.writeDouble(argumentDouble.max);
            };
        } else if (argument instanceof ArgumentFloat) {
            ArgumentFloat argumentFloat = (ArgumentFloat) argument;
            argumentNode.parser = "brigadier:float";
            argumentNode.properties = packetWriter -> {
                packetWriter.writeByte((byte) 0b11);
                packetWriter.writeFloat(argumentFloat.min);
                packetWriter.writeFloat(argumentFloat.max);
            };
        } else if (argument instanceof ArgumentInteger) {
            ArgumentInteger argumentInteger = (ArgumentInteger) argument;
            argumentNode.parser = "brigadier:integer";
            argumentNode.properties = packetWriter -> {
                packetWriter.writeByte((byte) 0b11);
                packetWriter.writeInt(argumentInteger.min);
                packetWriter.writeInt(argumentInteger.max);
            };
        } else if (argument instanceof ArgumentWord) {
            argumentNode.parser = "brigadier:string";
            argumentNode.properties = packetWriter -> {
                packetWriter.writeVarInt(0); // Single word
            };
        } else if (argument instanceof ArgumentString) {
            argumentNode.parser = "brigadier:string";
            argumentNode.properties = packetWriter -> {
                packetWriter.writeVarInt(1); // Quotable phrase
            };
        } else if (argument instanceof ArgumentStringArray) {
            argumentNode.parser = "brigadier:string";
            argumentNode.properties = packetWriter -> {
                packetWriter.writeVarInt(2); // Greedy phrase
            };
        }

        return argumentNode;
    }*/
}
