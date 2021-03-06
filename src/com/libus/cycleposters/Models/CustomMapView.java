package com.libus.cycleposters.Models;

import com.libus.cycleposters.CyclePosters;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomMapView implements Listener {

    private final CyclePosters plugin;

    private static CustomMapView instance = null;

    // load up all images into map upon init
    // saved splitting and loading images on every cycle


    public CustomMapView(CyclePosters pl) {
        plugin = pl;
    }

    public static CustomMapView getInstance(CyclePosters plugin) {
        if (instance == null)
            instance = new CustomMapView(plugin);
        return instance;
    }

    /**
     * load up maps on server start
     * allows persistence between restarts
     */
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, CyclePosters.getPlugin(CyclePosters.class));

        File dataFile = new File(plugin.getDataFolder() + "/data.yml");
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);
        if (mapData.contains("posters")) {
            plugin.postermap = new HashMap<>();
            for (String poster : mapData.getConfigurationSection("posters").getKeys(false)) {
                List<Integer> maplist = mapData.getIntegerList("posters." + poster + ".maps");

                //for every slide in every poster
                Map<String, Map<Integer, BufferedImage>> slidemap = new HashMap<>();
                for(String slide : mapData.getConfigurationSection("posters." + poster + ".slides").getKeys(false)){
                    String imageLocation = mapData.getString("posters." + poster + ".slides." + slide + ".image");

                    BufferedImage image = null;
                    try {
                        image = ImageIO.read(new File(imageLocation));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int width = Integer.parseInt(mapData.getString("posters." + poster + ".width"));
                    int height = Integer.parseInt(mapData.getString("posters." + poster + ".height"));

                    BufferedImage scaledImage = new BufferedImage(width * 128, height * 128, BufferedImage.TYPE_INT_RGB);
                    Graphics g = scaledImage.createGraphics();
                    g.drawImage(image, 0, 0, width * 128, height * 128, null);
                    g.dispose();

                    Map<Integer, BufferedImage> tilemap = new HashMap<>();
                    int i = 0;
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            MapView view = Bukkit.getServer().getMap(maplist.get(i));
                            for (MapRenderer renderer : view.getRenderers()) view.removeRenderer(renderer);
                            PosterRenderer renderer = new PosterRenderer();
                            BufferedImage imagePart = scaledImage.getSubimage(x * 128, y * 128, 128, 128);
                            tilemap.put(maplist.get(i), imagePart);

                            renderer.load(imagePart);
                            view.addRenderer(renderer);
                            view.setScale(MapView.Scale.FARTHEST);
                            view.setTrackingPosition(false);
                            i++;
                        }
                    }
                    slidemap.put(slide, tilemap);
                }
                plugin.postermap.put(poster, slidemap);
//
//
                int cycleInterval = mapData.getInt("posters." + poster + ".interval") * 20;
                if (cycleInterval > 0) {
                    Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
                        PosterRenderer renderer = new PosterRenderer(plugin);
                        try {
                            renderer.loadNextImage(poster);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, cycleInterval, cycleInterval);
                }
            }
        }
    }


    public void saveImage(String name, List<Integer> maps, List<String> images, int width, int height, int interval) throws IOException {
        File dataFile = new File(plugin.getDataFolder() + "/data.yml");
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);
        mapData.set("posters." + name + ".width", width);
        mapData.set("posters." + name + ".height", height);
        mapData.set("posters." + name + ".maps", maps);
        for (int i = 0; i < images.size(); i++) {
            mapData.set("posters." + name + ".slides.slide_" + i + ".image", images.get(i));
        }
        mapData.set("posters." + name + ".current_slide_index", 0);
        mapData.set("posters." + name + ".interval", interval);
        mapData.save(dataFile);
    }
}
