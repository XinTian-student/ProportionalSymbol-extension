package com.tianxin.classification;



/**
 * description：Factory of the classification methods
 * spring TODO
 *
 * @Author X_T
 * @Date 14/08/2020 15:42
 * @Version V1.0
 **/
public class ClassificationFactory {
    private ClassificationFactory(){}
    public static ClassificationFactory newInstance(){
        return new ClassificationFactory();
    }
    public Classification create(String method){
        if("natural".equals(method)){
            return new JenksFisherNaturalBreaksClassification();
        }else if("quantile".equals(method)){
            return new QuantileClassification();
        }else if("heads".equals(method)){
            return new HeadsClassification();
        }else if("equal".equals(method)){
            return new EqualIntervalClassification();
        }else{
            return null;
        }
    }
}
