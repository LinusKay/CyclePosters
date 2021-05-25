package com.libus.cycleposters.Events;

import com.libus.cycleposters.CyclePosters;
import com.libus.cycleposters.models.Poster;
import com.libus.cycleposters.models.PosterRenderer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.List;

public class Event implements Listener {
    private CyclePosters plugin;
    public Event(CyclePosters pl){ plugin = pl;}

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) throws IOException {
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
}
