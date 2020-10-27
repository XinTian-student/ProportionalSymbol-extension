package com.tianxin;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.util.factory.FactoryRegistry;
import org.geotools.xml.styling.SLDParser;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Prompts the user for a shapefile and displays the contents on the screen in a map frame.
 *
 * <p>This is the GeoTools Quickstart application used in documentationa and tutorials. *
 */
public class Start2 {
    //shp数据所在文件夹
    private final static String SHP_FILE_PATH = "C:\\Users\\mydream\\Desktop\\临时文件\\geo\\data\\";
    //sld文件夹
    private final static String STYLE_FILE_PATH = "C:\\Users\\mydream\\Desktop\\临时文件\\geo\\";

    /**
     * GeoTools Quickstart demo application. Prompts the user for a shapefile and displays its
     * contents on the screen in a map frame
     */
    public static void main(String[] args) throws Exception {
        //1 找shp文件
        SimpleFeatureSource polygonfeature = getFeature(SHP_FILE_PATH + "Overijssel_province.shp");
        SimpleFeatureSource pointfeature = getFeature(SHP_FILE_PATH + "point_Overijssel.shp");
        // Create a map content and add our shapefile to it
        //2找样式文件
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        File sldFile = new File(STYLE_FILE_PATH + File.separator + "proportional_symbol_map.sld");
        SLDParser stylereader = new SLDParser(styleFactory, sldFile.toURI().toURL());
        Style[] stylearray = stylereader.readXML();
        Style style = stylearray[0];
        //3，创建map（地图）
        MapContent map = new MapContent();
        map.setTitle("11111");
        //Overijssel_province
        Layer layer1 = new FeatureLayer(polygonfeature, style);


        //point_Overijssel
        Layer layer2 = new FeatureLayer(pointfeature, style);
        //将面shp加载到地图（加载 == 绘制）
        map.addLayer(layer2);
        map.addLayer(layer1);
        //将点shp加载到地图（加载 == 绘制）

        // Now display the map 绘制地图
        JMapFrame.showMap(map);
    }

    public static SimpleFeatureSource getFeature(String filePath) throws IOException {
        File pFile = new File(filePath);//);
        FileDataStore store = FileDataStoreFinder.getDataStore(pFile);
        SimpleFeatureSource featureSource = store.getFeatureSource();
        return featureSource;
    }



}

