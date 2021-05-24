package com.libus.cycleposters.models;

import com.libus.cycleposters.CyclePosters;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Poster {

    private int width;
    private int height;
    private Location startingLocation;
    private BufferedImage image;

    public Poster(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Poster(int width, int height, BufferedImage image) {
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public Poster(int width, int height, Location startingLocation, BufferedImage image) {
        this.width = width;
        this.height = height;
        this.startingLocation = startingLocation;
        this.image = image;
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
        return this.getStartingLocation();
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public boolean render() throws IOException {
        // scale image to correct dimensions
        int scaleWidth = this.width * 128;
        int scaleHeight = this.height * 128;
        BufferedImage scaledImage = new BufferedImage(scaleWidth, scaleHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = scaledImage.createGraphics();
        g.drawImage(this.image, 0, 0, scaleWidth, scaleHeight, null);
        g.dispose();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {

                MapView view = Bukkit.createMap(this.startingLocation.getWorld());
                for (MapRenderer renderer : view.getRenderers()) view.removeRenderer(renderer);

                PosterRenderer renderer = new PosterRenderer();

                BufferedImage imagePart = scaledImage.getSubimage(x * 128, y * 128, 128, 128);
                renderer.load(imagePart);
                view.addRenderer(renderer);

                ItemStack map = new ItemStack(Material.FILLED_MAP);
                MapMeta meta = (MapMeta) map.getItemMeta();

                meta.setMapView(view);

                map.setItemMeta(meta);
                Location startingLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() + x, this.startingLocation.getY() - y, this.startingLocation.getZ());
                ItemFrame frame = this.startingLocation.getWorld().spawn(startingLocation, ItemFrame.class);
                frame.setItem(map);

//                startingLocation.getBlock().setType(Material.RED_CONCRETE);

            }
        }
        return true;
    }
}
