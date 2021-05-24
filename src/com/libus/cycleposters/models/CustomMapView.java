package com.libus.cycleposters.models;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.libus.cycleposters.CyclePosters;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
        if(mapData.contains("ids")) {
            for (String id : mapData.getConfigurationSection("ids").getKeys(false)) {
                mapCount++;
                savedImages.put(Integer.parseInt(id), mapData.getString("ids." + id));
            }
            System.out.println("[CyclePosters] loaded " + mapCount + " poster tiles");
        }
    }

    @EventHandler
    public void onMapInitEvent(MapInitializeEvent event) throws IOException {
        if (savedImages.containsKey(event.getMap().getId())) {
            MapView view = event.getMap();
            for (MapRenderer renderer : view.getRenderers()) view.removeRenderer(renderer);
            PosterRenderer renderer = new PosterRenderer();
            renderer.load(ImageIO.read(new File(plugin.getDataFolder() + "/events/pieces/" + view.getId() + ".jpg")));
            view.addRenderer(renderer);
            view.setScale(MapView.Scale.FARTHEST);
            view.setTrackingPosition(false);
        }
    }

    public void saveImage(int id) throws IOException {
        File dataFile = new File(plugin.getDataFolder() + "/data.yml");
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);
        mapData.set("ids." + id, "");
        mapData.save(dataFile);
    }
}
