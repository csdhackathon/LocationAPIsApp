package com.pb.locationapis.tilesource;

import android.content.Context;
import android.util.Log;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.ManifestUtil;

/**
 *
 * Creates a PitneyBowes GeoMap API TileSource. The PitneyBowes GeoMap API Tiles are the Standard Raster Tile.
 * For details @link https://locate.pitneybowes.com/geomap
 * You won't be able to use it until you set the access token and map id.
*/
public class PBGeoMapTileSource  extends OnlineTileSourceBase{
    /** the meta data key in the manifest */

    private static final String PBGEO_GEOMAP_THEME = "PBGEO_GEOMAP_THEME";

    private static final String ACCESS_TOKEN = "API_KEY";

    private static final String[] geoMapBaseURL = new String[]{
            "https://api.pitneybowes.com/location-intelligence/geomap/v1/tile/osm/"};

    private String accessToken;
    private String geoMapThemeType ="bronze";

    public PBGeoMapTileSource()
    {
        super("PBGeoMapTiles", 1, 21, 256, ".png", geoMapBaseURL, "@Carto @OpenStreetMap");
    }

    /**
     * creates a new PitneyBowes tile source, loading the access token and mapid from the manifest
     * @param ctx
     * @since 5.1
     */
    public PBGeoMapTileSource(final Context ctx)
    {
        super("PBGeoMapTiles", 1, 21, 256, ".png", geoMapBaseURL, "@Carto @OpenStreetMap");
        retrieveAccessToken(ctx);
        retrieveGeoMapThemeType(ctx);

    }

    /**
     * creates a new PitneyBowes tile source, using the specified access token and Pitney Bowes GeoMap Theme Type
     * @param geoMapTheme
     * @param accesstoken
     * @since 5.1
     */
    public PBGeoMapTileSource(final String geoMapTheme, final String accesstoken)
    {
        super("PBGeoMapTiles", 1, 21, 256, ".png", PBGeoMapTileSource.geoMapBaseURL, "@Carto @OpenStreetMap");
        this.accessToken=accesstoken;
        this.geoMapThemeType =geoMapTheme;

    }

    /**
     * TileSource allowing majority of options (sans url) to be user selected.
     * <br> <b>Warning, the static method {@link #retrieveGeoMapThemeType(android.content.Context)} should have been invoked once before constructor invocation</b>
     * @param name Name
     * @param zoomMinLevel Minimum Zoom Level
     * @param zoomMaxLevel Maximum Zoom Level
     * @param tileSizePixels Size of Tile Pixels
     * @param imageFilenameEnding Image File Extension
     */
    public PBGeoMapTileSource(String name, int zoomMinLevel, int zoomMaxLevel, int tileSizePixels, String imageFilenameEnding)
    {
        super(name, zoomMinLevel, zoomMaxLevel, tileSizePixels, imageFilenameEnding, geoMapBaseURL);
    }

    /**
     * TileSource allowing all options to be user selected.
     * <br> <b>Warning, the static method {@link #retrieveGeoMapThemeType(android.content.Context)} should have been invoked once before constructor invocation</b>
     * @param name Name
     * @param zoomMinLevel Minimum Zoom Level
     * @param zoomMaxLevel Maximum Zoom Level
     * @param tileSizePixels Size of Tile Pixels
     * @param imageFilenameEnding Image File Extension
     * @param geoMapBaseVersionURL Pitney Bowes Version Base Url @see https://api.pitneybowes.com/location-intelligence/geomap/v1/tile#Versions
     */
    public PBGeoMapTileSource(String name, int zoomMinLevel, int zoomMaxLevel, int tileSizePixels, String imageFilenameEnding,
                              String geoMapBaseVersionURL)
    {
        super(name, zoomMinLevel, zoomMaxLevel, tileSizePixels, imageFilenameEnding,
                new String[] { geoMapBaseVersionURL });
    }

    /**
     * Reads the Pitney Bowes map id from the manifest.<br>
     */


    public final void retrieveGeoMapThemeType(final Context acontext)
    {
        geoMapThemeType = ManifestUtil.retrieveKey(acontext,PBGEO_GEOMAP_THEME);
    }

    /**
     * Reads the access token from the manifest.
     */
    public final void retrieveAccessToken(final Context aContext)
    {
        // Retrieve the MapId from the Manifest
        accessToken = ManifestUtil.retrieveKey(aContext, ACCESS_TOKEN);
    }





    @Override
    public String getTileURLString(final MapTile aMapTile)
    {
        StringBuilder url = new StringBuilder(geoMapBaseURL[0]);
        url.append(aMapTile.getZoomLevel());
        url.append("/");
        url.append(aMapTile.getX());
        url.append("/");
        url.append(aMapTile.getY());
        url.append(".png");
        url.append("?api_key=").append(this.accessToken);
        url.append("&theme=").append(this.geoMapThemeType);
        String res = url.toString();
        Log.i("GeoMapTileSource ", res);

        return res;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessTokeninput) {
        accessToken = accessTokeninput;
    }


    public static String[] geoMapBaseURL() {
        return geoMapBaseURL;
    }

    public static String getPbgeoGeomapTheme() {
        return PBGEO_GEOMAP_THEME;
    }

    public String getGeoMapThemeType() {
        return geoMapThemeType;
    }

    public void setGeoMapThemeType(String geoMapThemeType) {
        this.geoMapThemeType = geoMapThemeType;
    }

}
