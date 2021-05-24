package com.libus.cycleposters.Events;

import com.libus.cycleposters.CyclePosters;
import com.libus.cycleposters.models.Poster;
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

                    Block block = event.getClickedBlock();
                    Location location = new Location(player.getWorld(), block.getX(), block.getY(), block.getZ()+1);
                    poster.setStartingLocation(location);

                    poster.render();
                    return;
                }
            }
        }
    }
}
