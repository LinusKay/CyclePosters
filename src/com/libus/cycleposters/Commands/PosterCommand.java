package com.libus.cycleposters.Commands;

import com.libus.cycleposters.CyclePosters;
import com.libus.cycleposters.models.CustomMapView;
import com.libus.cycleposters.models.Poster;
import com.libus.cycleposters.models.PosterRenderer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

            // CREATE POSTER INSTANCE
            if (command.equals("place")) {
                if (args.length > 1) {
                    String posterName = args[1];
                    String imageLocation = args[2];
                    posterDirectory = plugin.getDataFolder().toString();

                    int posterWidth = 3;
                    int posterHeight = 2;
                    if (args.length > 3) posterWidth = Integer.parseInt(args[3]);
                    if (args.length > 4) posterHeight = Integer.parseInt(args[4]);


                    Poster poster = new Poster(posterName, posterWidth, posterHeight, plugin, posterDirectory + "/" + imageLocation);

                    List<Object> list = new ArrayList<>();
                    list.add(player);
                    list.add(poster);

                    plugin.playerList.add(list);

                } else {
                    player.sendMessage("please specify poster name");
                }
            } else if (command.equals("update")) {
                if (args.length > 1) {
                    if (args.length > 2) {
                        try {
                            String posterName = args[1];
                            String imageFile = args[2];

                            File dataFile = new File(plugin.getDataFolder() + "/data.yml");
                            YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);
                            if (mapData.contains("posters." + posterName)) {
                                List<Integer> maps = mapData.getIntegerList("posters." + posterName + ".maps");
                                int width = Integer.parseInt(mapData.getString("posters." + posterName + ".width"));
                                int height = Integer.parseInt(mapData.getString("posters." + posterName + ".height"));
                                BufferedImage image = ImageIO.read(new File(plugin.getDataFolder() + "/" + imageFile));

                                int mapCount = 0;
                                for (int y = 0; y < height; y++) {
                                    for (int x = 0; x < width; x++) {
                                        MapView view = Bukkit.getServer().getMap(maps.get(mapCount));
                                        for (MapRenderer renderer : view.getRenderers()) view.removeRenderer(renderer);
                                        PosterRenderer renderer = new PosterRenderer();

                                        BufferedImage scaledImage = new BufferedImage(width * 128, height * 128, BufferedImage.TYPE_INT_RGB);
                                        Graphics g = scaledImage.createGraphics();
                                        g.drawImage(image, 0, 0, width * 128, height * 128, null);
                                        g.dispose();

                                        BufferedImage imagePart = scaledImage.getSubimage(x * 128, y * 128, 128, 128);

                                        renderer.load(imagePart);


                                        view.addRenderer(renderer);
                                        view.setScale(MapView.Scale.FARTHEST);
                                        view.setTrackingPosition(false);
                                        mapCount++;
                                    }
                                }

                                CustomMapView customMapView = CustomMapView.getInstance(plugin);
                                customMapView.saveImage(posterName, maps, imageFile, width, height);
                                }

//                            customMapView.saveImage(mapId, this.imageLocation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage("Please specify a new image");
                    }
                } else {
                    player.sendMessage("Please specify map id");
                }
            }
//            else if(command.equals("delete")){
//                if(args.length > 1){
//                    String posterName = args[1];
//                    File dataFile = new File(plugin.getDataFolder() + "/data.yml");
//                    YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);
//
//                    for(String map : mapData.getStringList("posters." + posterName + ".maps")){
//                        int mapId = Integer.parseInt(map);
//                        MapView mapView = Bukkit.getServer().getMap(mapId);
//                        Bukkit.getServer().getMap(mapId).
//                    }
//
//                    mapData.set("posters." + posterName, null);
//                    try {
//                        mapData.save(dataFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
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
