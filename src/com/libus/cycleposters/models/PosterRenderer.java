package com.libus.cycleposters.models;

import com.libus.cycleposters.CyclePosters;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Rotation.NONE;

public class PosterRenderer extends MapRenderer {

    private BufferedImage image;
    private boolean done;

    private CyclePosters plugin;

    private List<Integer> maps = new ArrayList<Integer>();

    public PosterRenderer() {
        done = false;
    }
    public PosterRenderer(CyclePosters pl) {
        plugin = pl;
        done = false;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (done) return;
        mapCanvas.drawImage(0, 0, image);
        mapView.setTrackingPosition(false);
        done = true;
    }

    public PosterRenderer(String url) {
        done = false;
    }

    public boolean load(BufferedImage image) {
        image = MapPalette.resizeImage(image);
        this.image = image;
        return false;
    }

    /**
     * Render poster tiles
     * @param poster
     * @throws IOException
     */
    public void render(Poster poster) throws IOException {
        if (done) return;
        int scaleWidth = poster.getWidth() * 128;
        int scaleHeight = poster.getHeight() * 128;

        BufferedImage originalImage = ImageIO.read(new File(poster.getImages().get(0)));
        BufferedImage scaledImage = new BufferedImage(scaleWidth, scaleHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = scaledImage.createGraphics();
        g.drawImage(originalImage, 0, 0, scaleWidth, scaleHeight, null);
        g.dispose();

        for (int y = 0; y < poster.getHeight(); y++) {
            for (int x = 0; x < poster.getWidth(); x++) {

                MapView view = Bukkit.createMap(poster.getStartingLocation().getWorld());
                for (MapRenderer renderer : view.getRenderers()) view.removeRenderer(renderer);

                PosterRenderer renderer = new PosterRenderer();

                BufferedImage imagePart = scaledImage.getSubimage(x * 128, y * 128, 128, 128);

                renderer.load(imagePart);
                view.addRenderer(renderer);

                ItemStack map = new ItemStack(Material.FILLED_MAP);
                MapMeta meta = (MapMeta) map.getItemMeta();

                meta.setMapView(view);

                map.setItemMeta(meta);

                /**
                 * THERE IS PROBABLY DEFINITELY A BETTER WAY OF DOING poster BUT IT IS SO LATE AND I HAVE BEEN AWAKE STUDYING UNTIL 4:30 AM FOR THE PAST FEW NIGHTS AND I SIMPLY AM BEYOND CARING
                 * SORRY XO BUT ALSO HELP
                 * */
                Location frameLocation = null;
                Rotation rotation = NONE;
                switch (poster.getFacingDirection()) {
                    case "NORTH":
                        frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() - x, poster.getStartingLocation().getY() - y, poster.getStartingLocation().getZ() - 2);
                        break;
                    case "EAST":
                        frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() + 1, poster.getStartingLocation().getY() - y, poster.getStartingLocation().getZ() - x - 1);
                        break;
                    case "SOUTH":
                        frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() + x, poster.getStartingLocation().getY() - y, poster.getStartingLocation().getZ());
                        break;
                    case "WEST":
                        frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() - 1, poster.getStartingLocation().getY() - y, poster.getStartingLocation().getZ() + x - 1);
                        break;
                    case "UP":
                        switch (poster.getHorizontalPlacementDirection()) {
                            case "NORTH":
                                frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() + x, poster.getStartingLocation().getY() + 1, poster.getStartingLocation().getZ() - 1 + y);
                                rotation = NONE;
                                break;
                            case "EAST":
                                frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() - y, poster.getStartingLocation().getY() + 1, poster.getStartingLocation().getZ() - 1 + x);
                                rotation = Rotation.CLOCKWISE_45;
                                break;
                            case "SOUTH":
                                frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() - x, poster.getStartingLocation().getY() + 1, poster.getStartingLocation().getZ() - 1 - y);
                                rotation = Rotation.CLOCKWISE;
                                break;
                            case "WEST":
                                frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() + y, poster.getStartingLocation().getY() + 1, poster.getStartingLocation().getZ() - 1 - x);
                                rotation = Rotation.COUNTER_CLOCKWISE_45;
                                break;
                        }
                        break;
                    case "DOWN":
                        switch (poster.getHorizontalPlacementDirection()) {
                            case "NORTH":
                                frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() + x, poster.getStartingLocation().getY() - 1, poster.getStartingLocation().getZ() - 1 + y);
                                rotation = NONE;
                                break;
                            case "EAST":
                                frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() - y, poster.getStartingLocation().getY() - 1, poster.getStartingLocation().getZ() - 1 + x);
                                rotation = Rotation.COUNTER_CLOCKWISE_45;
                                break;
                            case "SOUTH":
                                frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() - x, poster.getStartingLocation().getY() - 1, poster.getStartingLocation().getZ() - 1 - y);
                                rotation = Rotation.CLOCKWISE;
                                break;
                            case "WEST":
                                frameLocation = new Location(poster.getStartingLocation().getWorld(), poster.getStartingLocation().getX() + y, poster.getStartingLocation().getY() - 1, poster.getStartingLocation().getZ() - 1 - x);
                                rotation = Rotation.CLOCKWISE_45;
                                break;
                        }
                        break;
                }

                ItemFrame frame = poster.getStartingLocation().getWorld().spawn(frameLocation, ItemFrame.class);
                frame.setItem(map);
                frame.setRotation(rotation);

                maps.add(view.getId());
            }
        }
        CustomMapView customMapView = CustomMapView.getInstance(this.plugin);
        customMapView.saveImage(poster.getName(), maps, poster.getImages(), poster.getWidth(), poster.getHeight());
        done = true;
    }

    public void loadNextImage(String posterName){

        File dataFile = new File(plugin.getDataFolder() + "/data.yml");
        YamlConfiguration mapData = YamlConfiguration.loadConfiguration(dataFile);

        if (mapData.contains("posters." + posterName)) {
            List<Integer> maps = mapData.getIntegerList("posters." + posterName + ".maps");
            int width = Integer.parseInt(mapData.getString("posters." + posterName + ".width"));
            int height = Integer.parseInt(mapData.getString("posters." + posterName + ".height"));

            List<String> images = mapData.getStringList("posters." + posterName + ".images");
            int currentImageCount = mapData.getInt("posters." + posterName + ".current_image_index");
            if(currentImageCount + 1 < images.size()){
                currentImageCount++;
            }
            else{
                currentImageCount = 0;
            }
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(images.get(currentImageCount)));
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            mapData.set("posters." + posterName + ".current_image_index", currentImageCount);
            try {
                mapData.save(dataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
