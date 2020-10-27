package com.tianxin.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tianxin.function.ProportionalSymbolFunction;
import org.geotools.feature.NameImpl;
import org.geotools.filter.FunctionFactory;
import org.opengis.feature.type.Name;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Description：
 *
 * @Author X_T
 * @Date 04/07/2020 21:08
 * @Version V1.0
 **/
public class MyFunctionFactory implements FunctionFactory {

    @Override
    public List<FunctionName> getFunctionNames() {
        List<FunctionName> functionList = new ArrayList<>();
        functionList.add(ProportionalSymbolFunction.NAME);
        return Collections.unmodifiableList(functionList);
    }

    @Override
    public Function function(String name, List<Expression> args, Literal fallback) {
        return function(new NameImpl(name), args, fallback);
    }

    @Override
    public Function function(Name name, List<Expression> args, Literal fallback) {
        if (ProportionalSymbolFunction.NAME.getFunctionName().equals(name)) {
            return new ProportionalSymbolFunction(args, fallback);
        }
        return null; // we do not implement that function
    }
}

