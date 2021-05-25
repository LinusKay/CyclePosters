package com.libus.cycleposters.Commands;

import com.libus.cycleposters.CyclePosters;
import com.libus.cycleposters.models.Poster;
import com.libus.cycleposters.models.PosterRenderer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PosterCommand implements CommandExecutor {

    private CyclePosters plugin;

    public PosterCommand(CyclePosters pl) {
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if(player.hasPermission("cycleposters.manage")) {
            if (args.length >= 0) {
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
                        renderer.loadNextImage(posterName);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gets image dimensions for given file
     *
     * @param imgFile image file
     * @return dimensions of image
     * @throws IOException if the file is not a known image
     */
    public static Dimension getImageDimension(File imgFile) throws IOException {
        int pos = imgFile.getName().lastIndexOf(".");
        if (pos == -1)
            throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
        String suffix = imgFile.getName().substring(pos + 1);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        while (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                ImageInputStream stream = new FileImageInputStream(imgFile);
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                return new Dimension(width, height);
            } catch (IOException e) {
                System.out.println("Error reading: " + imgFile.getAbsolutePath() + e);
            } finally {
                reader.dispose();
            }
        }

        throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
    }
}
