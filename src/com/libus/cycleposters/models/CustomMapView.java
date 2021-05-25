package com.libus.cycleposters.models;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    public CustomMapView(CyclePosters pl){
        plugin = pl;
    }

    public static CustomMapView getInstance(CyclePosters plugin) {
        if (instance == null)
            instance = new CustomMapView(plugin);
        return instance;
    }

    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, CyclePosters.getPlugin(CyclePosters.class));
        File dataFile = new File(plugin.getDataFolder() + "/data.yml");
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);
        int mapCount = 0;
        int tileCount = 0;
        if(mapData.contains("posters")) {
            for (String poster : mapData.getConfigurationSection("posters").getKeys(false)) {
                mapCount++;
                System.out.println(poster);
                for(String map : mapData.getStringList("posters." + poster + ".maps")){
                    tileCount++;
                    savedImages.put(Integer.parseInt(map), mapData.getString("posters." + poster + ".images"));
                }

            }
            System.out.println("[CyclePosters] loaded " + mapCount + " posters with " + tileCount + " tiles");
        }
    }

    @EventHandler
    public void onMapInitEvent(MapInitializeEvent event) throws IOException {
        if (savedImages.containsKey(event.getMap().getId())) {
            MapView view = event.getMap();
            for (MapRenderer renderer : view.getRenderers()) view.removeRenderer(renderer);
            PosterRenderer renderer = new PosterRenderer();
            renderer.load(ImageIO.read(new File(plugin.getDataFolder() + "/" + savedImages.get(event.getMap().getId()))));
            view.addRenderer(renderer);
            view.setScale(MapView.Scale.FARTHEST);
            view.setTrackingPosition(false);
        }
    }

    public void saveImage(String name, List<Integer> maps, String imageLocation, int width, int height) throws IOException {
        File dataFile = new File(plugin.getDataFolder() + "/data.yml");
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);
        mapData.set("posters." + name + ".width", width);
        mapData.set("posters." + name + ".height", height);
        mapData.set("posters." + name + ".maps", maps);
        mapData.set("posters." + name + ".images", imageLocation);
        mapData.save(dataFile);
    }
}
