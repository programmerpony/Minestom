package net.minestom.server.scoreboard;

import net.minestom.server.chat.Chat;
import net.minestom.server.network.packet.server.play.TeamsPacket;

public class SidebarTeam {

    private String teamName;
    private String prefix, suffix;
    private String entityName;

    private String teamDisplayName = "displaynametest";
    private byte friendlyFlags = 0x00;
    private TeamsPacket.NameTagVisibility nameTagVisibility = TeamsPacket.NameTagVisibility.NEVER;
    private TeamsPacket.CollisionRule collisionRule = TeamsPacket.CollisionRule.NEVER;
    private int teamColor = 2;


    protected SidebarTeam(String teamName, String prefix, String suffix, String entityName) {
        this.teamName = teamName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.entityName = entityName;
    }

    protected TeamsPacket getCreationPacket() {
        TeamsPacket teamsPacket = new TeamsPacket();
        teamsPacket.teamName = teamName;
        teamsPacket.action = TeamsPacket.Action.CREATE_TEAM;
        teamsPacket.teamDisplayName = Chat.fromLegacyText(teamDisplayName);
        teamsPacket.friendlyFlags = friendlyFlags;
        teamsPacket.nameTagVisibility = nameTagVisibility;
        teamsPacket.collisionRule = collisionRule;
        teamsPacket.teamColor = teamColor;
        teamsPacket.teamPrefix = Chat.fromLegacyText(prefix);
        teamsPacket.teamSuffix = Chat.fromLegacyText(suffix);
        teamsPacket.entities = new String[]{entityName};
        return teamsPacket;
    }

    protected TeamsPacket getDestructionPacket() {
        TeamsPacket teamsPacket = new TeamsPacket();
        teamsPacket.teamName = teamName;
        teamsPacket.action = TeamsPacket.Action.REMOVE_TEAM;
        return teamsPacket;
    }

    protected TeamsPacket updatePrefix(String prefix) {
        TeamsPacket teamsPacket = new TeamsPacket();
        teamsPacket.teamName = teamName;
        teamsPacket.action = TeamsPacket.Action.UPDATE_TEAM_INFO;
        teamsPacket.teamDisplayName = Chat.fromLegacyText(teamDisplayName);
        teamsPacket.friendlyFlags = friendlyFlags;
        teamsPacket.nameTagVisibility = nameTagVisibility;
        teamsPacket.collisionRule = collisionRule;
        teamsPacket.teamColor = teamColor;
        teamsPacket.teamPrefix = Chat.fromLegacyText(prefix);
        teamsPacket.teamSuffix = Chat.fromLegacyText(suffix);
        return teamsPacket;
    }

    protected String getEntityName() {
        return entityName;
    }

    protected String getPrefix() {
        return prefix;
    }

    protected void refreshPrefix(String prefix) {
        this.prefix = prefix;
    }
}
