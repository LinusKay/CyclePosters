package com.libus.cycleposters;

import com.libus.cycleposters.Commands.PosterCommand;
import com.libus.cycleposters.Events.Event;
import com.libus.cycleposters.models.CustomMapView;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CyclePosters extends JavaPlugin {

    public List<List<Object>> playerList = new ArrayList<>();

    @Override
    public void onEnable(){


        getCommand("poster").setExecutor(new PosterCommand(this));
        getServer().getPluginManager().registerEvents(new Event(this), this);
        System.out.println("[CyclePosters] enabled");
    }

    @Override
    public void onDisable(){
        System.out.println("[CyclePosters] disabled");

    }

}
