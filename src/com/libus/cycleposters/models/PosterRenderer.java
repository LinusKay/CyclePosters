package com.libus.cycleposters.models;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class PosterRenderer extends MapRenderer {

    private BufferedImage image;
    private boolean done;

    public PosterRenderer() {
        done = false;
    }

    public PosterRenderer(String url) {
        done = false;
    }

    public boolean load(BufferedImage image) {
        image = MapPalette.resizeImage(image);
        this.image = image;
        return false;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (done) return;
        mapCanvas.drawImage(0, 0, image);
        mapView.setTrackingPosition(false);
        done = true;
    }
}
