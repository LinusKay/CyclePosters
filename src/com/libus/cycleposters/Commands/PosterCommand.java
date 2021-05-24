package com.libus.cycleposters.Commands;

import com.libus.cycleposters.CyclePosters;
import com.libus.cycleposters.models.Poster;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PosterCommand implements CommandExecutor {

    private CyclePosters plugin;
    private String posterDirectory;

    public PosterCommand(CyclePosters pl) {
        plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length >= 0) {
            String command = args[0];
            // SETUP POSTER
            if (command.equals("create")) {
                if (args.length > 1) {
                    String posterName = args[1];
                    posterDirectory = plugin.getDataFolder() + "/" + posterName;
                    player.sendMessage("creating poster " + posterName);
                    try {
                        if (Files.exists(Paths.get(posterDirectory))) {
                            player.sendMessage("Poster already exists!");
                            return false;
                        }
                        Path directory = Files.createDirectories(Paths.get(posterDirectory));
                        File posterconfigfile = new File(posterDirectory + "/" + posterName + ".yml");
                        YamlConfiguration posterconfig = YamlConfiguration.loadConfiguration(posterconfigfile);
                        if (args.length > 2) {
                            String interval = args[2];
                            posterconfig.set("interval", interval);
                        } else {
                            posterconfig.set("interval", 10);
                        }
                        posterconfig.save(posterconfigfile);
                        player.sendMessage("created directory " + directory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    player.sendMessage("please specify poster name");
                }
            }
            // SET CYCLE INTERVAL
            else if (command.equals("setinterval")) {
                if (args.length > 1) {
                    String posterName = args[1];
                    posterDirectory = plugin.getDataFolder() + "/" + posterName;
                    if (args.length > 2) {
                        String interval = args[2];
                        File posterconfigfile = new File(posterDirectory + "/" + posterName + ".yml");
                        YamlConfiguration posterconfig = YamlConfiguration.loadConfiguration(posterconfigfile);
                        posterconfig.set("interval", interval);
                        try {
                            posterconfig.save(posterconfigfile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage("Please specify interval");
                    }
                } else {
                    player.sendMessage("Please specify poster name");
                }
            }
            // CREATE POSTER INSTANCE
            else if (command.equals("place")) {
                if (args.length > 1) {
                    String posterName = args[1];
//                    posterDirectory = plugin.getDataFolder() + "/" + posterName;
                    posterDirectory = plugin.getDataFolder() + "/events";
//                    if (Files.exists(Paths.get(plugin.getDataFolder() + "/" + posterName))) {

//                        UUID uuid = UUID.randomUUID();
//                        Location location = player.getLocation();
//                        File posterconfigfile = new File(posterDirectory + "/" + posterName + ".yml");
//                        YamlConfiguration posterconfig = YamlConfiguration.loadConfiguration(posterconfigfile);
//                        posterconfig.set("instances." + uuid + ".location.x", location.getX());
//                        posterconfig.set("instances." + uuid + ".location.y", location.getY());
//                        posterconfig.set("instances." + uuid + ".location.z", location.getZ());

                    // set block dimensions of poster display
                    int posterWidth = 3;
                    int posterHeight = 2;
                    if (args.length > 2) posterWidth = Integer.parseInt(args[2]);
                    if (args.length > 3) posterHeight = Integer.parseInt(args[3]);

                    // create image object, using placeholder image
                    BufferedImage image = null;
                    try {
                        image = ImageIO.read(new File(posterDirectory + "/" + posterName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Poster poster = new Poster(posterWidth, posterHeight, image);

                    List<Object> list = new ArrayList<>();
                    list.add(player);
                    list.add(poster);

                    plugin.playerList.add(list);


//                        try {
//                            posterconfig.save(posterconfigfile);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        player.sendMessage("No existing poster");
//                    }
                } else {
                    player.sendMessage("please specify poster name");
                }
            }
            // GET POSTER INSTANCES
            else if (command.equals("getinstances")) {
                if (args.length > 1) {
                    String posterName = args[1];
                    posterDirectory = plugin.getDataFolder() + "/" + posterName;
                    File posterconfigfile = new File(posterDirectory + "/" + posterName + ".yml");
                    YamlConfiguration posterconfig = YamlConfiguration.loadConfiguration(posterconfigfile);
                    try {
                        for (String key : posterconfig.getConfigurationSection("instances").getKeys(false)) {
                            String x = posterconfig.getString("instances." + key + ".location.x");
                            String y = posterconfig.getString("instances." + key + ".location.y");
                            String z = posterconfig.getString("instances." + key + ".location.z");
                            String scale = posterconfig.getString(key + ".scale");
                            player.sendMessage("uuid: " + key + ", location: [x: " + x + ", y: " + y + ", z: " + z + "], scale: " + scale);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
            // REMOVE POSTER INSTANCES
            else if (command.equals("removeinstances")) {
                if (args.length > 1) {
                    String posterName = args[1];
                    posterDirectory = plugin.getDataFolder() + "/" + posterName;
                    File posterconfigfile = new File(posterDirectory + "/" + posterName + ".yml");
                    YamlConfiguration posterconfig = YamlConfiguration.loadConfiguration(posterconfigfile);
                    posterconfig.set("instances", null);
                    try {
                        posterconfig.save(posterconfigfile);
                    } catch (IOException e) {
                        e.printStackTrace();
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
