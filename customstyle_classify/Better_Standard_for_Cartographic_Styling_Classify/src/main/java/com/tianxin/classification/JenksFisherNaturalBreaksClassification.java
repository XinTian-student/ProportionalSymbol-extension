package com.tianxin.classification;

import org.geotools.filter.function.RangedClassifier;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * Natural break
 *
 * @author X_T
 *
 * @version
 *
 * @since 14/08/2020
 */
public class JenksFisherNaturalBreaksClassification extends Classification {

	@Override
	@SuppressWarnings("all")
	public RangedClassifier calculate(List<? extends Comparable> datas, int classNum) {
		List<Double> input = toDouble(datas);
		List<Double> breaks = JenksFisher.createJenksFisherBreaksArray(input, classNum+1);
		
		
		
		////////////// todo 
		Double[] min = new Double[breaks.size()-1];
		Double[] max = new Double[breaks.size()-1];
		if(breaks.size() >1)
		{
			for (int i = 0; i < breaks.size()-1; i++) {
				if(i==0)
				{
					min[i] = Collections.min(input);
					max[i] = breaks.get(i+1);
				}
				else if(i<breaks.size()-2)
				{
					min[i] = breaks.get(i);
					max[i] = breaks.get(i+1);
				}
				else
				{
					min[i] = breaks.get(i);
					max[i] = Collections.max(input);
				}
					
			}
			return new RangedClassifier(min, max);
		}
		else if(breaks.size() == 1)
		{
			min = new Double[1];
			max = new Double[1];
			min[0] = breaks.get(0);
			max[0] = breaks.get(0);
		}
//		Double[] min = new Double[breaks.size() - 1];
//		Double[] max = new Double[breaks.size() - 1];
//		if(breaks.size() >1)
//		{
//			for (int i = 0; i < breaks.size() - 1; i++) {
//				min[i] = breaks.get(i);
//				max[i] = breaks.get(i + 1);
//			}
//			return new RangedClassifier(min, max);
//		}
//		else if(breaks.size() == 1)
//		{
//			min = new Double[1];
//			max = new Double[1];
//			min[0] = breaks.get(0);
//			max[0] = breaks.get(0);
//		}
		
		return new RangedClassifier(min, max);
	}

	@SuppressWarnings("all")
	private List<Double> toDouble(List<? extends Comparable> datas) {
		if (datas != null) {
			return datas.stream().filter(a -> a != null).mapToDouble(a -> Double.parseDouble(a.toString())).boxed()
					.collect(Collectors.toList());
		}
		return null;
	}

}
