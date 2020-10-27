package com.tianxin.model;

import org.geotools.filter.function.RangedClassifier;

import java.util.List;

/**
 * 描述：
 *
 * @Author X_T
 * @Date 20/09/2020 14:35
 * @Version V1.0
 **/
public final class PivotClassifier {
    /**
     * the minimum value of a interval
     */
    double[] min;
    /**
     * the maximum value of a interval
     */
    double[] max;
    /**
     * the color of a interval
     */
    String[] colors;
    private PivotClassifier(){}

    /**
     * create through RangedClassifier
     * @param classifier
     */
    public PivotClassifier(RangedClassifier classifier){
        int len = Math.min(colors.length, classifier.getSize());
        this.min = new double[len];
        this.max = new double[len];
        for(int i = 0; i< len; i++){
            Double min = Double.valueOf(String.valueOf(classifier.getMin(i)));
            Double max = Double.valueOf(String.valueOf(classifier.getMax(i)));
            this.min[i] = min;
            this.max[i] = max;
        }
    }
    public int getSize(){
        return this.min.length;
    }
    public double getMin(int index){
        if(index > min.length){
            throw new IndexOutOfBoundsException("Index: "+index+", Size: "+ min.length);
        }
        return this.min[index];
    }
    public double getMax(int index){
        if(index > max.length){
            throw new IndexOutOfBoundsException("Index: "+index+", Size: "+ max.length);
        }
        return this.max[index];
    }
}
