package com.libus.cycleposters.Commands;

import com.libus.cycleposters.CyclePosters;
import com.libus.cycleposters.Models.Poster;
import com.libus.cycleposters.Models.PosterRenderer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PosterCommand implements CommandExecutor {

    private final CyclePosters plugin;

    public PosterCommand(CyclePosters pl) {
        plugin = pl;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        Player player = (Player) sender;
        if(player.hasPermission("cycleposters.manage")) {
            if (args.length > 0) {
                String command = args[0];

                // CREATE POSTER INSTANCE
                if (command.equals("place")) {
                    if (args.length > 1) {
                        String posterName = args[1];

                        List<String> images = new ArrayList<>();
                        for (int i = 4; i < args.length; i++) {
                            images.add(plugin.getDataFolder() + "/" + args[i]);
                        }

                        int posterWidth = 3;
                        int posterHeight = 2;
                        if (args.length > 2) posterWidth = Integer.parseInt(args[2]);
                        if (args.length > 3) posterHeight = Integer.parseInt(args[3]);

                        Poster poster = new Poster(posterName, posterWidth, posterHeight, plugin, images);

                        List<Object> list = new ArrayList<>();
                        list.add(player);
                        list.add(poster);

                        plugin.playerList.add(list);

                    } else {
                        player.sendMessage("please specify poster name");
                    }
                } else if (command.equals("next")) {
                    if (args.length > 1) {
                        String posterName = args[1];
                        PosterRenderer renderer = new PosterRenderer(plugin);
                        try {
                            renderer.loadNextImage(posterName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        else{
            player.sendMessage("You do not have permission: cycleposters.manage");
        }
        return true;
    }
}
