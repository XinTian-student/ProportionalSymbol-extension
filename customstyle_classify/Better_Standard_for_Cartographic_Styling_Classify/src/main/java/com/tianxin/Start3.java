package com.tianxin;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.CacheUtil;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.geotools.xml.styling.SLDParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Prompts the user for a shapefile and displays the contents on the screen in a map frame.
 *
 * <p>This is the GeoTools Quickstart application used in documentationa and tutorials. *
 */
public class Start3 {
    //the folder having the shapefile.
    private final static String SHP_FILE_PATH = "C:\\tianxin\\study\\MSc Thesis\\Thesis\\New Method test\\说明\\data\\";
    //the folder having the SLD file.
    private final static String STYLE_FILE_PATH = "C:\\tianxin\\study\\MSc Thesis\\Thesis\\New Method test\\";

    /**
     * GeoTools Quickstart demo application. Prompts the user for a shapefile and displays its
     * contents on the screen in a map frame
     */
    public static void main(String[] args) throws Exception {
        //1 find .shp file
        SimpleFeatureSource polygonfeature = getFeature(SHP_FILE_PATH + "Beijing.shp");
        // Create a map content and add our shapefile to it
        //2，Create a widow to displace the map
        MapContent map = new MapContent();
        map.setTitle("The classified choropleth map of the population density of each Municipality in Beijing");
        //3 find the style file (SLD file)
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        File sldFile = new File(STYLE_FILE_PATH + File.separator + "classify_extension_beijing.sld");
        SLDParser stylereader = new SLDParser(styleFactory, sldFile.toURI().toURL());
        Style[] stylearray = stylereader.readXML();
        Style style = stylearray[0];

        Layer layer1 = new FeatureLayer(polygonfeature, style);
        map.addLayer(layer1);

        // Now display the map
        JMapFrame.showMap(map);
        List<Object> values = CacheUtil.values;

    }

    public static SimpleFeatureSource getFeature(String filePath) throws IOException {
        File pFile = new File(filePath);
        FileDataStore store = FileDataStoreFinder.getDataStore(pFile);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        return featureSource;
    }



}

