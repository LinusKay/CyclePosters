package com.libus.cycleposters.models;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import com.libus.cycleposters.CyclePosters;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;

public class CustomMapView implements Listener {

    List<MapRenderer> renderers = new ArrayList<>();

    private Map<Integer, String> savedImages = new HashMap<Integer, String>();

    private CyclePosters plugin;

    private static CustomMapView instance = null;

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
            for (String poster : mapData.getConfigurationSection("posters").getKeys(false)) {
                int i = 0;
                List<Integer> maplist = mapData.getIntegerList("posters." + poster + ".maps");
                List<String> images = mapData.getStringList("posters." + poster + ".images");
                int currentImageIndex = mapData.getInt("current_image_index");

                int width = Integer.parseInt(mapData.getString("posters." + poster + ".width"));
                int height = Integer.parseInt(mapData.getString("posters." + poster + ".height"));

                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File(images.get(currentImageIndex)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedImage scaledImage = new BufferedImage(width * 128, height * 128, BufferedImage.TYPE_INT_RGB);
                Graphics g = scaledImage.createGraphics();
                g.drawImage(image, 0, 0, width * 128, height * 128, null);
                g.dispose();

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        MapView view = Bukkit.getServer().getMap(maplist.get(i));
                        for (MapRenderer renderer : view.getRenderers()) view.removeRenderer(renderer);
                        PosterRenderer renderer = new PosterRenderer();
                        BufferedImage imagePart = scaledImage.getSubimage(x * 128, y * 128, 128, 128);

                        renderer.load(imagePart);
                        view.addRenderer(renderer);
                        view.setScale(MapView.Scale.FARTHEST);
                        view.setTrackingPosition(false);
                        i++;
                    }
                }
            }
        }
    }


    public void saveImage(String name, List<Integer> maps, List<String> images, int width, int height) throws IOException {
        File dataFile = new File(plugin.getDataFolder() + "/data.yml");
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);
        mapData.set("posters." + name + ".width", width);
        mapData.set("posters." + name + ".height", height);
        mapData.set("posters." + name + ".maps", maps);
        mapData.set("posters." + name + ".images", images);
        mapData.set("posters." + name + ".current_image_index", 0);
        mapData.save(dataFile);
    }
}
