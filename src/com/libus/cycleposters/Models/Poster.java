package com.libus.cycleposters.Models;

import com.libus.cycleposters.CyclePosters;
import org.bukkit.Location;
import java.util.List;

public class Poster {

    private CyclePosters plugin;

    private String name;
    private int width;
    private int height;
    private Location startingLocation;
    private List<String> images;

    private String faceDirection;
    private String horizontalPlacementDirection;

    public Poster(String name, int width, int height, CyclePosters plugin, List<String> images) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.images = images;
        this.plugin = plugin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public void setStartingLocation(Location startingLocation) {
        this.startingLocation = startingLocation;
    }

    public Location getStartingLocation() {
        return this.startingLocation;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return this.images;
    }

    public void setFacingDirection(String direction) {
        this.faceDirection = direction;
    }

    public String getFacingDirection() {
        return this.faceDirection;
    }

    public void setHorizontalPlacementDirection(String horizontalPlacementDirection) {
        this.horizontalPlacementDirection = horizontalPlacementDirection;
    }

    public String getHorizontalPlacementDirection() {
        return this.horizontalPlacementDirection;
    }
}
