package com.tianxin.factory;

import com.tianxin.function.ClassifyFunction;
import org.geotools.feature.NameImpl;
import org.geotools.filter.FunctionFactory;
import org.opengis.feature.type.Name;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description：
 *
 * @Author X_T
 * @Date 11/09/2020 21:08
 * @Version V1.0
 **/
public class ClassifyFunctionFactory implements FunctionFactory {

    @Override
    public List<FunctionName> getFunctionNames() {
        List<FunctionName> functionList = new ArrayList<>();
        functionList.add(ClassifyFunction.NAME);
        return Collections.unmodifiableList(functionList);
    }

    @Override
    public Function function(String name, List<Expression> args, Literal fallback) {
        return function(new NameImpl(name), args, fallback);
    }

    @Override
    public Function function(Name name, List<Expression> args, Literal fallback) {
        if (ClassifyFunction.NAME.getFunctionName().equals(name)) {
            return new ClassifyFunction(args, fallback);
        }
        return null; // we do not implement that function
    }
}

