/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.tianxin.function;

import com.tianxin.classification.Classification;
import com.tianxin.classification.ClassificationFactory;
import com.tianxin.model.PivotClassifier;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FilterAttributeExtractor;
import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.filter.function.RangedClassifier;
import org.geotools.renderer.CacheUtil;
import org.geotools.util.Converters;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of "Classify" as a normal function.
 *
 * <p>This implementation is compatible with the Function interface; the parameter list can be used
 * to set the proporty name, classification method, the colours of each class etc...
 *
 * <p>This function expects:
 *
 * <ol>
 *   <li>PropertyName: the name of the attribute need to be visualised
 *   <li>Literal: clasNum
 *   <li>Literal: clasMethod
 *   <li>Literal: colorschema
 * </ol>
 *
 * In reality any expression will do.
 *
 * @author X_T
 * 
 */
public class ClassifyFunction implements Function {

//    /** Use as a literal value to indicate behaviour of threshold boundary */
//    public static final String SUCCEEDING = "succeeding";
//
//    /** Use as a literal value to indicate behaviour of threshold boundary */
//    public static final String PRECEDING = "preceding";
//
//    /**
//     * Use as a PropertyName when defining a color map. The "Raterdata" is expected to apply to only
//     * a single band; if multiple bands are provided it is probably a mistake; but we will use the
//     * maximum value (since we are working against a threshold).
//     */
//    public static final String RASTER_DATA = "Rasterdata";

    private final List<Expression> parameters;
    private final Literal fallback;
    /** True if all expressions in the param set are static values */
    private boolean staticTable = true;

    double[] thresholds;
    Expression[] values;
    volatile Object[] convertedValues;
    private Class convertedValuesContext;
    private String belongsTo;
    private RangedClassifier rangedClassifier;

    /** Make the instance of FunctionName available in a consistent spot. */
    public static final FunctionName NAME =
            new FunctionNameImpl(
                    "Classify");

    public ClassifyFunction() {
        this(new ArrayList<Expression>(), null);
    }

    public ClassifyFunction(List<Expression> parameters, Literal fallback) {
        this.parameters = parameters;
        this.fallback = fallback;

        values = new Expression[parameters.size() - 3];
        for(int i = 3; i< parameters.size(); i++){
            values[i-3] = parameters.get(i);
        }

    }

    @Override
    public String getName() {
        return NAME.getName();
    }

    @Override
    public FunctionName getFunctionName() {
        return NAME;
    }

    @Override
    public List<Expression> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public Object evaluate(Object object) {
        return evaluate(object, Object.class);
    }

    private static AtomicBoolean isHappened = new AtomicBoolean(false);

    /**
     * @param method: classification methods
     * @param clazz: the number of classes
     */
    public void classify(String method, Integer clazz){
        //Ensure that the code is executed only once
        if(!isHappened.compareAndSet(false, true)){
            return;
        }
        List<Double> values = getValues();
        //Create the specified construction method through the ClassificationFactory
        Classification classification = ClassificationFactory.newInstance().create(method);
        //Thresholds and colors obtained through the specific classification method.
        RangedClassifier rangedClassifier = classification.calculate(values, clazz);
        //Cache classification results
        this.rangedClassifier = rangedClassifier;
    }

    
    @Override
    public <T> T evaluate(Object object, Class<T> context) {
        final Expression lookupExp = parameters.get(0);

        //Starting classify features
        classify(parameters.get(2).toString(), Integer.valueOf(parameters.get(1).toString()));

        // check the value we're looking for
        Double value = lookupExp.evaluate(object, Double.class);
        if (value == null) {
            value = Converters.convert(object, Double.class);
        }

        //Determine the interval of the current attribute value and the corresponding color.
        for (int i = 0 ; i < this.rangedClassifier.getSize(); i++){
            double left = toDouble(this.rangedClassifier.getMin(i));
            double right = toDouble(this.rangedClassifier.getMax(i));
            if(value >= left && value <= right){
                return values[i].evaluate(object, context);
            }
        }
        return values[0].evaluate(object, context);
    }
    public static double toDouble(Object value){
        return Double.valueOf(String.valueOf(value));
    }

    @Override
    public Literal getFallbackValue() {
        return fallback;
    }

    /**
     * Creates a String representation of this Function with the function name and the arguments.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append("(");
        List<Expression> params = getParameters();
        if (params != null) {
            Expression exp;
            for (Iterator<Expression> it = params.iterator();
                 it.hasNext(); ) {
                exp = it.next();
                sb.append("[");
                sb.append(exp);
                sb.append("]");
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassifyFunction that = (ClassifyFunction) o;
        return staticTable == that.staticTable
                && Objects.equals(parameters, that.parameters)
                && Objects.equals(fallback, that.fallback)
                && Arrays.equals(thresholds, that.thresholds)
                && Arrays.equals(values, that.values)
                && Arrays.equals(convertedValues, that.convertedValues)
                && Objects.equals(convertedValuesContext, that.convertedValuesContext)
                && Objects.equals(belongsTo, that.belongsTo);
    }

    @Override
    public int hashCode() {
        int result =
                Objects.hash(parameters, fallback, staticTable, convertedValuesContext, belongsTo);
        result = 31 * result + Arrays.hashCode(thresholds);
        result = 31 * result + Arrays.hashCode(values);
        result = 31 * result + Arrays.hashCode(convertedValues);
        return result;
    }

    public static List<Double> getValues(){
        List<Double> res = new ArrayList<>();
        List<Object> values = CacheUtil.values;
        for(Object val : values){
            res.add(Double.valueOf(val.toString()));
        }
        return res;
    }
}
