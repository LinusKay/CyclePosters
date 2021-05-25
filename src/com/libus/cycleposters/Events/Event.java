package com.libus.cycleposters.Events;

import com.libus.cycleposters.CyclePosters;
import com.libus.cycleposters.models.Poster;
import com.libus.cycleposters.models.PosterRenderer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Event implements Listener {
    private CyclePosters plugin;
    public Event(CyclePosters pl){ plugin = pl;}

    @EventHandler
    public void onPlayerPlacePoster(PlayerInteractEvent event) throws IOException {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            for(List<Object> list : plugin.playerList){
                if(list.contains(player)) {
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

                    Location location = new Location(player.getWorld(), clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ()+1);
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
    public void onPlayerClickPoster(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if(entity.getType().name().equals("ITEM_FRAME")){
            ItemFrame itemFrame = (ItemFrame) entity;
            ItemStack itemStack = itemFrame.getItem();
            MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
            int mapId = mapMeta.getMapView().getId();

            File dataFile = new File(plugin.getDataFolder() + "/data.yml");
            YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);

            if(mapData.contains("posters")){
                for(String poster : mapData.getConfigurationSection("posters").getKeys(false)){
                    if(mapData.getIntegerList("posters." + poster + ".maps").contains(mapId)){
                        int currentSlideIndex = mapData.getInt("posters." + poster + ".current_slide_index");
                        if(mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_message") != null) {
                            String clickMessage = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_message");
                            String clickHover = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_hover");
                            String clickURL = mapData.getString("posters." + poster + ".slides.slide_" + currentSlideIndex + ".click_url");

                            TextComponent message = new TextComponent(clickMessage);
                            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, clickURL));
                            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(clickHover)));
                            player.spigot().sendMessage(message);

                            event.setCancelled(true);
                        }
                    }
                }
            }



        }


    }
}
