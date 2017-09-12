package com.pb.locationapis.tilesource;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

/**
 * sample custom tile source
 * Created by alex on 6/20/16.
 */
public class OnlineTileSource extends OnlineTileSourceBase {

    public OnlineTileSource(){
        this("USGS Topo", 0, 18, 256, "",
                new String[] { "http://basemap.nationalmap.gov/ArcGIS/rest/services/USGSTopo/MapServer/tile/"});
    }

    /**
     * Constructor
     *
     * @param aName                a human-friendly name for this tile source
     * @param aZoomMinLevel        the minimum zoom level this tile source can provide
     * @param aZoomMaxLevel        the maximum zoom level this tile source can provide
     * @param aTileSizePixels      the tile size in pixels this tile source provides
     * @param aImageFilenameEnding the file name extension used when constructing the filename
     * @param aBaseUrl             the base url(s) of the tile server used when constructing the url to download the tiles
     */
    public OnlineTileSource(String aName, int aZoomMinLevel, int aZoomMaxLevel, int aTileSizePixels, String aImageFilenameEnding, String[] aBaseUrl) {
        super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels, aImageFilenameEnding, aBaseUrl,"USGS");
    }

    @Override
    public String getTileURLString(MapTile aTile) {
        /*return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getY() + "/" + aTile.getX()
                + mImageFilenameEnding;*/

        try {
            return getBaseUrl().replace("{z}", ""+aTile.getZoomLevel()).replace("{x}",""+aTile.getX()).replace("{y}",""+aTile.getY());
        } catch (Exception fail) {
            return  null;
        }
    }


}
