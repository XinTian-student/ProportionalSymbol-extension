package com.tianxin.function;

import org.geotools.filter.capability.FunctionNameImpl;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

import java.util.*;

/**
 * description：
 *
 * @Author X_T
 * @Date 04/07/2020 20:40
 * @Version V1.0
 **/
public class ProportionalSymbolFunction implements Function {
    private final List<Expression> parameters;
    private final Literal fallback;
    /** True if all expressions in the param set are static values */
    private boolean staticTable = true;
    /** Make the instance of FunctionName available in a consistent spot. */
    public static final FunctionName NAME =
            new FunctionNameImpl(
                    "ProportionalSymbol"
                    );

    public ProportionalSymbolFunction() {
        this(new ArrayList<Expression>(), null);
    }

    public ProportionalSymbolFunction(List<Expression> parameters, Literal fallback) {
        this.parameters = parameters;
        this.fallback = fallback;
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

    @Override
    public <T> T evaluate(Object object, Class<T> context) {
        final Expression lookupExp = parameters.get(0);

        // check the value we're looking for
        Double value = lookupExp.evaluate(object, Double.class);
        Object maxData = parameters.get(2);
        Object maxSize = parameters.get(1);
        Double res = Double.valueOf(String.valueOf(maxSize)) * Math.sqrt(value/Double.valueOf(String.valueOf(maxData)));
        System.out.println("------"+ res +"------");
        return (T)String.valueOf(res);
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
        List<org.opengis.filter.expression.Expression> params = getParameters();
        if (params != null) {
            org.opengis.filter.expression.Expression exp;
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
        ProportionalSymbolFunction that = (ProportionalSymbolFunction) o;
        return staticTable == that.staticTable
                && Objects.equals(parameters, that.parameters)
                && Objects.equals(fallback, that.fallback);
    }

    @Override
    public int hashCode() {
        int result =
                Objects.hash(parameters, fallback, staticTable);
        return result;
    }
}
