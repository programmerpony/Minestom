package fr.themode.demo;

import fr.themode.demo.entity.ChickenCreature;
import fr.themode.demo.generator.ChunkGeneratorDemo;
import fr.themode.demo.generator.NoiseTestGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.benchmark.BenchmarkManager;
import net.minestom.server.benchmark.ThreadResult;
import net.minestom.server.entity.*;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.ItemUpdateStateEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.ping.ResponseDataConsumer;
import net.minestom.server.timer.TaskRunnable;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.Vector;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.time.UpdateOption;
import net.minestom.server.world.Dimension;

import java.util.Map;
import java.util.UUID;

public class PlayerInit {

    private static String textures = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDg1NTI3NjIwNCwKICAicHJvZmlsZUlkIiA6ICI0NTY2ZTY5ZmM5MDc0OGVlOGQ3MWQ3YmE1YWEwMGQyMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGlua29mZGVhdGgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRkMWUwOGIwYmI3ZTlmNTkwYWYyNzc1ODEyNWJiZWQxNzc4YWM2Y2VmNzI5YWVkZmNiOTYxM2U5OTExYWU3NSIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBjYzA4ODQwNzAwNDQ3MzIyZDk1M2EwMmI5NjVmMWQ2NWExM2E2MDNiZjY0YjE3YzgwM2MyMTQ0NmZlMTYzNSIKICAgIH0KICB9Cn0=";
    private static String signature = "rCVVwVpLF9ovy+Hm4cOXOLSPOMjNo5WoBfHo9K2OcTUPqcZJ1P/1k4lAnQkChD/Ri11iJ84PejWJzDkHMXM8196Wh+jf12d2GJVhca9/SRLms0cFJjdZZjs72+82AdX0OtO3+qzwKRHzHoEYb+ZVZLfgx37ZKKo4DD3IKmaSnwEjOVJ4BOhnsXLmcNW37kdZUmv2/hlg7ZuWZaayWPhadCYEMnkpVtDIpnpzAeV9EcRfg/ysQoynO2v6WEW0RtnfFEczMN6vXtfiuC8UqyA2SK9aiLnBgpGaehDfFIq/0dpo2uFilVDS/Il6uQ1JSwq7yNT5lNF+i1AlH9SGf1VVy5mT9ShmkVmRxCXX5cSNLXZD0acsNNJb/GAuDHuXpE32GsfgKxWAXMHLw0GnbADbFDfdl5nQyQTDS7FRfUjsFpF8a8Z83muFXaty2WLFy1zxy2JEkI/q+ltLaEG6mQbWI2zhOS7ARvK0OmPz4lDYVInfrwL93AIdMUg2Re817hsapkN6Dm1ND+iirvayR90gqQ9C9J0dMMBlSyTSoKBQeLsi8qETS+7LuhvletPTDFolnTIvP8hj2bWLmQ7LfXJ2arJCUw86YEavVYuF0gYrBuKcEYTC4DA0kO4yLj63gwEgOj9dEigCgyqUcenzmZBffSZ365/QF0cGrG7HC7HmF0w=";

    private static PlayerSkin skin = new PlayerSkin(textures, signature);

    private static volatile InstanceContainer instanceContainer;
    private static volatile InstanceContainer netherTest;

    private static volatile Inventory inventory;

    static {
        //StorageFolder storageFolder = MinecraftServer.getStorageManager().getFolder("chunk_data");
        ChunkGeneratorDemo chunkGeneratorDemo = new ChunkGeneratorDemo();
        NoiseTestGenerator noiseTestGenerator = new NoiseTestGenerator();
        //instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(storageFolder);
        instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(Dimension.OVERWORLD);
        instanceContainer.enableAutoChunkLoad(true);
        instanceContainer.setChunkGenerator(noiseTestGenerator);

        netherTest = MinecraftServer.getInstanceManager().createInstanceContainer(Dimension.NETHER);
        netherTest.enableAutoChunkLoad(true);
        netherTest.setChunkGenerator(noiseTestGenerator);

        // Load some chunks beforehand
        int loopStart = -2;
        int loopEnd = 2;
        for (int x = loopStart; x < loopEnd; x++)
            for (int z = loopStart; z < loopEnd; z++) {
                instanceContainer.loadChunk(x, z);
                netherTest.loadChunk(x, z);
            }


        inventory = new Inventory(InventoryType.CHEST_1_ROW, "Test inventory");
        inventory.addInventoryCondition((p, slot, clickType, inventoryConditionResult) -> {
            p.sendMessage("click type inventory: " + clickType);
            System.out.println("slot inv: " + slot);
            inventoryConditionResult.setCancel(false);
        });
        inventory.setItemStack(0, new ItemStack(Material.DIAMOND, (byte) 34));
    }

    public static void init() {
        ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
        BenchmarkManager benchmarkManager = MinecraftServer.getBenchmarkManager();

        MinecraftServer.getSchedulerManager().addRepeatingTask(new TaskRunnable() {
            @Override
            public void run() {
                long ramUsage = benchmarkManager.getUsedMemory();
                ramUsage /= 1e6; // bytes to MB

                String benchmarkMessage = "";
                for (Map.Entry<String, ThreadResult> resultEntry : benchmarkManager.getResultMap().entrySet()) {
                    String name = resultEntry.getKey();
                    ThreadResult result = resultEntry.getValue();
                    benchmarkMessage += "&7" + name;
                    benchmarkMessage += ": ";
                    benchmarkMessage += "&e" + MathUtils.round(result.getCpuPercentage(), 2) + "% CPU ";
                    benchmarkMessage += "&c" + MathUtils.round(result.getUserPercentage(), 2) + "% USER ";
                    benchmarkMessage += "&d" + MathUtils.round(result.getBlockedPercentage(), 2) + "% BLOCKED ";
                    benchmarkMessage += "&a" + MathUtils.round(result.getWaitedPercentage(), 2) + "% WAITED ";
                    benchmarkMessage += "\n";
                }

                for (Player player : connectionManager.getOnlinePlayers()) {
                    player.sendHeaderFooter("RAM USAGE: " + ramUsage + " MB", benchmarkMessage, '&');
                }
            }
        }, new UpdateOption(10, TimeUnit.TICK));

        connectionManager.addPacketConsumer((player, packetController, packet) -> {
            // Listen to all received packet
            packetController.setCancel(false);
        });

        connectionManager.addPlayerInitialization(player -> {

            player.addEventCallback(EntityAttackEvent.class, event -> {
                Entity entity = event.getTarget();
                if (entity instanceof EntityCreature) {
                    EntityCreature creature = (EntityCreature) entity;
                    creature.damage(DamageType.fromPlayer(player), -1);
                    Vector velocity = player.getPosition().clone().getDirection().multiply(6);
                    velocity.setY(4f);
                    entity.setVelocity(velocity);
                    player.sendMessage("You attacked an entity!");
                } else if (entity instanceof Player) {
                    Player target = (Player) entity;
                    Vector velocity = player.getPosition().clone().getDirection().multiply(4);
                    velocity.setY(3.5f);
                    target.setVelocity(velocity);
                    target.damage(DamageType.fromPlayer(player), 5);
                    player.sendMessage("ATTACK");
                }
            });

            player.addEventCallback(PlayerBlockPlaceEvent.class, event -> {
                if (event.getHand() != Player.Hand.MAIN)
                    return;

                if (event.getBlockId() == Block.STONE.getBlockId()) {
                    event.setCustomBlock((short) 2); // custom stone block
                }
                if (event.getBlockId() == Block.TORCH.getBlockId()) {
                    event.setCustomBlock((short) 3); // custom torch block
                }

                /*for (Player p : player.getInstance().getPlayers()) {
                    if (p != player)
                        p.teleport(player.getPosition());
                }*/

                ChickenCreature chickenCreature = new ChickenCreature(player.getPosition());
                chickenCreature.setInstance(player.getInstance());
                chickenCreature.setAttribute(Attribute.MOVEMENT_SPEED, 0.4f);

                /*FakePlayer fakePlayer = new FakePlayer(UUID.randomUUID(), "test");
                fakePlayer.addEventCallback(EntityDeathEvent.class, e -> {
                    fakePlayer.getController().respawn();
                });
                fakePlayer.setArrowCount(25);
                FakePlayerController controller = fakePlayer.getController();
                controller.sendChatMessage("I am a bot!");*/

                //Hologram hologram = new Hologram(player.getInstance(), player.getPosition(), "Hey guy");

            });

            player.addEventCallback(PlayerBlockInteractEvent.class, event -> {
                if (event.getHand() != Player.Hand.MAIN)
                    return;

                short blockId = player.getInstance().getBlockId(event.getBlockPosition());
                Block block = Block.fromId(blockId);
                player.sendMessage("You clicked at the block " + block);
            });

            player.addEventCallback(PickupItemEvent.class, event -> {
                event.setCancelled(!player.getInventory().addItemStack(event.getItemStack())); // Cancel event if player does not have enough inventory space
            });

            player.addEventCallback(ItemDropEvent.class, event -> {
                ItemStack droppedItem = event.getItemStack();

                Position position = player.getPosition().clone().add(0, 1.5f, 0);
                ItemEntity itemEntity = new ItemEntity(droppedItem, position);
                itemEntity.setPickupDelay(500, TimeUnit.MILLISECOND);
                itemEntity.setInstance(player.getInstance());
                Vector velocity = player.getPosition().clone().getDirection().multiply(6);
                itemEntity.setVelocity(velocity);
            });

            player.addEventCallback(PlayerDisconnectEvent.class, event -> {
                System.out.println("DISCONNECTION " + player.getUsername());
            });

            player.addEventCallback(PlayerLoginEvent.class, event -> {
                event.setSpawningInstance(instanceContainer);

                player.setPermissionLevel(4);

                player.getInventory().addInventoryCondition((p, slot, clickType, inventoryConditionResult) -> {
                    player.sendMessage("CLICK PLAYER INVENTORY");
                    System.out.println("slot player: " + slot);
                });

                /*Sidebar scoreboard = new Sidebar("Scoreboard Title");
                for (int i = 0; i < 15; i++) {
                    scoreboard.createLine(new Sidebar.ScoreboardLine("id" + i, "Hey guys " + i, i));
                }
                scoreboard.addViewer(player);
                scoreboard.updateLineContent("id3", "I HAVE BEEN UPDATED");

                scoreboard.setTitle("test");*/
            });

            player.addEventCallback(PlayerSkinInitEvent.class, event -> {
                event.setSkin(skin);
            });

            player.addEventCallback(PlayerSpawnEvent.class, event -> {
                player.setGameMode(GameMode.CREATIVE);
                player.teleport(new Position(0, 41f, 0));

                player.setGlowing(true);

                ItemStack item = new ItemStack(Material.STONE_SWORD, (byte) 1);
                item.setDisplayName("Item name");
                item.getLore().add("a lore line");
                item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setEnchantment(Enchantment.SHARPNESS, (short) 50);
                player.getInventory().addItemStack(item);

                inventory.addItemStack(item.clone());
                player.openInventory(inventory);

                player.getInventory().addItemStack(new ItemStack(Material.STONE, (byte) 100));

                Instance instance = player.getInstance();
                WorldBorder worldBorder = instance.getWorldBorder();
                worldBorder.setDiameter(30);


                //EntityBoat entityBoat = new EntityBoat(player.getPosition());
                //entityBoat.setInstance(player.getInstance());
                //entityBoat.addPassenger(player);

                //player.getInventory().addItemStack(new ItemStack(Material.DIAMOND_CHESTPLATE, (byte) 1));

            /*TeamManager teamManager = Main.getTeamManager();
            Team team = teamManager.createTeam(getUsername());
            team.setTeamDisplayName("display");
            team.setPrefix("[Test] ");
            team.setTeamColor(ChatColor.RED);
            setTeam(team);

            setAttribute(Attribute.MAX_HEALTH, 10);
            heal();

            BelowNameScoreboard belowNameScoreboard = new BelowNameScoreboard();
            setBelowNameScoreboard(belowNameScoreboard);
            belowNameScoreboard.updateScore(this, 50);*/

            });

            player.addEventCallback(PlayerRespawnEvent.class, event -> {
                event.setRespawnPosition(new Position(0f, 41f, 0f));
            });

            player.addEventCallback(PlayerUseItemEvent.class, useEvent -> {
                player.sendMessage("Using item in air: " + useEvent.getItemStack().getMaterial());
            });

            player.addEventCallback(PlayerUseItemOnBlockEvent.class, useEvent -> {
                player.sendMessage("Main item: " + player.getInventory().getItemInMainHand().getMaterial());
                player.sendMessage("Using item on block: " + useEvent.getItemStack().getMaterial() + " at " + useEvent.getPosition() + " on face " + useEvent.getBlockFace());
            });

            player.addEventCallback(ItemUpdateStateEvent.class, event -> {
                System.out.println("ITEM UPDATE STATE");
            });

            player.addEventCallback(PlayerPreEatEvent.class, event -> {
                ItemStack itemStack = event.getFoodItem();
                Material material = itemStack.getMaterial();
                event.setEatingTime(material == Material.PORKCHOP ? 100 : 1000);
            });

            player.addEventCallback(PlayerEatEvent.class, event -> {
                System.out.println("PLAYER EAT EVENT");
            });

            player.addEventCallback(PlayerChunkUnloadEvent.class, event -> {
                Instance instance = player.getInstance();

                Chunk chunk = instance.getChunk(event.getChunkX(), event.getChunkZ());

                if (chunk == null)
                    return;

                // Unload the chunk (save memory) if it has no remaining viewer
                if (chunk.getViewers().isEmpty()) {
                    player.getInstance().unloadChunk(chunk);
                }
            });

        });
    }

    public static ResponseDataConsumer getResponseDataConsumer() {
        return (playerConnection, responseData) -> {
            responseData.setMaxPlayer(0);
            responseData.setOnline(MinecraftServer.getConnectionManager().getOnlinePlayers().size());
            responseData.addPlayer("A name", UUID.randomUUID());
            responseData.addPlayer("Could be some message", UUID.randomUUID());
            responseData.setDescription("IP test: " + playerConnection.getRemoteAddress());
        };
    }

}
