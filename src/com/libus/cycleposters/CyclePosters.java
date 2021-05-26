package com.libus.cycleposters;

import com.libus.cycleposters.Commands.PosterCommand;
import com.libus.cycleposters.Events.Event;
import com.libus.cycleposters.Models.CustomMapView;
import com.libus.cycleposters.Models.PosterRenderer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CyclePosters extends JavaPlugin {

    public List<List<Object>> playerList = new ArrayList<>();
    public Map<String, Map<String, Map<Integer, BufferedImage>>> postermap;

    @Override
    public void onEnable(){

        CustomMapView customMapView = CustomMapView.getInstance(this);
        customMapView.init();





        getCommand("poster").setExecutor(new PosterCommand(this));
        getServer().getPluginManager().registerEvents(new Event(this), this);
        System.out.println("[CyclePosters] enabled");
    }

    @Override
    public void onDisable(){
        System.out.println("[CyclePosters] disabled");

    }

}
