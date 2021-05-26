package com.libus.cycleposters.Events;

import com.libus.cycleposters.CyclePosters;
import com.libus.cycleposters.Models.Poster;
import com.libus.cycleposters.Models.PosterRenderer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Event implements Listener {
    private CyclePosters plugin;

    public Event(CyclePosters pl) {
        plugin = pl;
    }

    @EventHandler
    public void onPlayerPlacePoster(PlayerInteractEvent event) throws IOException {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            for (List<Object> list : plugin.playerList) {
                if (list.contains(player)) {
                    Poster poster = (Poster) list.get(1);
                    plugin.playerList.remove(list);

                    /**
                     * https://www.spigotmc.org/threads/getting-the-blockface-of-a-targeted-block.319181/#post-3002432
                     * Gets the BlockFace of the block the player is currently targeting.
                     *
                     * @param player the player's whos targeted blocks BlockFace is to be checked.
                     * @return the BlockFace of the targeted block, or null if the targeted block is non-occluding.
                     */
                    List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
                    if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return;
                    Block targetBlock = lastTwoTargetBlocks.get(1);
                    Block adjacentBlock = lastTwoTargetBlocks.get(0);
                    String faceDirection = targetBlock.getFace(adjacentBlock).toString();

                    Block clickedBlock = event.getClickedBlock();

                    String horizontalPlacementDirection = player.getFacing().toString();

                    Location location = new Location(player.getWorld(), clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ() + 1);
                    poster.setStartingLocation(location);
                    poster.setFacingDirection(faceDirection);
                    poster.setHorizontalPlacementDirection(horizontalPlacementDirection);

                    PosterRenderer renderer = new PosterRenderer(plugin);
                    renderer.render(poster);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerClickPoster(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType().name().equals("ITEM_FRAME")) {
            ItemFrame itemFrame = (ItemFrame) entity;
            ItemStack itemStack = itemFrame.getItem();
            int mapId;
            try {
                MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                mapId = mapMeta.getMapView().getId();
            } catch (ClassCastException e) {
                return;
            }

            File dataFile = new File(plugin.getDataFolder() + "/data.yml");
            YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);

            if (mapData.contains("posters")) {
                for (String poster : mapData.getConfigurationSection("posters").getKeys(false)) {
                    if (mapData.getIntegerList("posters." + poster + ".maps").contains(mapId)) {
                        int currentSlideIndex = mapData.getInt("posters." + poster + ".current_slide_index");

                        /**
                         * URL message on poster click
                         * url and hover message are optional
                         */
                        if (mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_message") != null) {

                            String clickMessage = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_message");
                            String clickHover = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_hover");
                            String clickURL = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_url");

                            TextComponent message = new TextComponent(clickMessage);
                            if (clickHover != null) {
                                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(clickHover)));
                            }
                            if (clickURL != null) {
                                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, clickURL));
                            }
                            player.spigot().sendMessage(message);
                        }

                        /**
                         * Teleport on poster click
                         * if teleport_safely is true in slide, player will be placed on ground
                         */
                        if (mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".teleport") != null) {
                            List<String> teleportData = mapData.getStringList("posters." + poster + ".slides.slide_" + currentSlideIndex + ".teleport");
                            World teleportWorld = Bukkit.getWorld(teleportData.get(0));
                            int teleportX = Integer.parseInt(teleportData.get(1));
                            int teleportY = Integer.parseInt(teleportData.get(2));
                            int teleportZ = Integer.parseInt(teleportData.get(3));

                            boolean teleport_safely = mapData.getBoolean("posters." + poster + ".slides.slide_" + currentSlideIndex + ".teleport_safely");
                            if (teleport_safely) {
                                teleportY = teleportWorld.getHighestBlockYAt(teleportX, teleportZ) + 1;
                            }

                            Location teleportLocation = new Location(teleportWorld, teleportX, teleportY, teleportZ);
                            player.teleport(teleportLocation);
                        }

                        /**
                         * Give item on click
                         * Very primitive for now
                         * Potentially getting a little out of scope
                         */
                        if(mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_item") != null){
                            String itemType = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_item");
                            ItemStack item = new ItemStack(Material.matchMaterial(itemType), 1);
                            ItemMeta meta = item.getItemMeta();

                            String itemName = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_item_name");
                            if(itemName != null) {
                                meta.setDisplayName(itemName);
                            }

                            List<String> itemLore = mapData.getStringList("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_item_lore");
                            if(itemLore != null) {
                                List<String> lore = new ArrayList<>();
                                for(String loreline : itemLore) lore.add(loreline);
                                meta.setLore(lore);
                            }

                            item.setItemMeta(meta);
                            player.getInventory().addItem(item);
                        }

                        /**
                         * Run command as player on click
                         */
                        if(mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".run_command") != null){
                            String command = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".run_command");
                            command = command.replace("{player}", player.getName());
                            if(mapData.getBoolean("posters." + poster + ".slides.slide_" + currentSlideIndex + ".run_command_as_console")){
                                ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                                Bukkit.dispatchCommand(console, command);
                            }
                            else {
                                player.performCommand(command);
                            }
                        }

                        event.setCancelled(true);
                    }
                }
            }


        }


    }
}
