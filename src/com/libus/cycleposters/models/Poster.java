package com.libus.cycleposters.models;

import com.libus.cycleposters.CyclePosters;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.bukkit.Rotation.NONE;

public class Poster {

    private CyclePosters plugin;

    private int width;
    private int height;
    private Location startingLocation;
    private BufferedImage image;
    private String faceDirection;
    private String horizontalPlacementDirection;

    public Poster(int width, int height, CyclePosters plugin) {
        this.width = width;
        this.height = height;
        this.plugin = plugin;
    }

    public Poster(int width, int height, CyclePosters plugin, BufferedImage image) {
        this.width = width;
        this.height = height;
        this.image = image;
        this.plugin = plugin;
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

    public void setFacingDirection(String direction) { this.faceDirection = direction; }

    public String getFacingDirection(){ return this.faceDirection; }

    public void setHorizontalPlacementDirection(String horizontalPlacementDirection) { this.horizontalPlacementDirection = horizontalPlacementDirection; }

    public String getHorizontalPlacementDirection(){ return this.horizontalPlacementDirection; }

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


                //save image to file
                ImageIO.write(imagePart, "jpg", new File(plugin.getDataFolder() + "/events/pieces/" + view.getId() + ".jpg") );

                /**
                 * THERE IS PROBABLY DEFINITELY A BETTER WAY OF DOING THIS BUT IT IS SO LATE AND I HAVE BEEN AWAKE STUDYING UNTIL 4:30 AM FOR THE PAST FEW NIGHTS AND I SIMPLY AM BEYOND CARING
                 * SORRY XO BUT ALSO HELP
                 * */
                Location frameLocation = null;
                Rotation rotation = NONE;
                switch(this.faceDirection){
                    case "NORTH":
                        frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() - x, this.startingLocation.getY() - y, this.startingLocation.getZ()-2);
                        break;
                    case "EAST":
                        frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() + 1, this.startingLocation.getY() - y, this.startingLocation.getZ()-x-1);
                        break;
                    case "SOUTH":
                        frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() + x, this.startingLocation.getY() - y, this.startingLocation.getZ());
                        break;
                    case "WEST":
                        frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() - 1, this.startingLocation.getY() - y, this.startingLocation.getZ()+x-1);
                        break;
                    case "UP":
                        switch(this.horizontalPlacementDirection) {
                            case "NORTH":
                                frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() + x, this.startingLocation.getY() + 1, this.startingLocation.getZ() - 1 + y);
                                rotation = NONE;
                                break;
                            case "EAST":
                                frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() - y, this.startingLocation.getY() + 1, this.startingLocation.getZ() - 1 + x);
                                rotation = Rotation.CLOCKWISE_45;
                                break;
                            case "SOUTH":
                                frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() - x , this.startingLocation.getY() + 1, this.startingLocation.getZ() - 1 - y);
                                rotation = Rotation.CLOCKWISE;
                                break;
                            case "WEST":
                                frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() + y, this.startingLocation.getY() + 1, this.startingLocation.getZ() - 1 - x);
                                rotation = Rotation.COUNTER_CLOCKWISE_45;
                                break;
                        }
                        break;
                    case "DOWN":
                        switch(this.horizontalPlacementDirection) {
                            case "NORTH":
                                frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() + x, this.startingLocation.getY() - 1, this.startingLocation.getZ() - 1 + y);
                                rotation = NONE;
                                break;
                            case "EAST":
                                frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() - y, this.startingLocation.getY() - 1, this.startingLocation.getZ() - 1 + x);
                                rotation = Rotation.COUNTER_CLOCKWISE_45;
                                break;
                            case "SOUTH":
                                frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() - x , this.startingLocation.getY() - 1, this.startingLocation.getZ() - 1 - y);
                                rotation = Rotation.CLOCKWISE;
                                break;
                            case "WEST":
                                frameLocation = new Location(this.startingLocation.getWorld(), this.startingLocation.getX() + y, this.startingLocation.getY() - 1, this.startingLocation.getZ() - 1 - x);
                                rotation = Rotation.CLOCKWISE_45;
                                break;
                        }
                        break;
                }


                ItemFrame frame = this.startingLocation.getWorld().spawn(frameLocation, ItemFrame.class);
                frame.setItem(map);
                frame.setRotation(rotation);

                CustomMapView customMapView = CustomMapView.getInstance(this.plugin);
                customMapView.saveImage(view.getId());
//                frameLocation.getBlock().setType(Material.RED_CONCRETE);
            }
        }
        return true;
    }
}
