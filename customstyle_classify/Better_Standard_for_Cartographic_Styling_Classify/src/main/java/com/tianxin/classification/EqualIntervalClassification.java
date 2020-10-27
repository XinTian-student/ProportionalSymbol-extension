package com.tianxin.classification;

import org.geotools.filter.function.RangedClassifier;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * Equal Interval
 *
 * @author GMR，X_T
 *
 * @version
 *
 * @since 13/08/2020
 */
public class EqualIntervalClassification extends Classification {

	@Override
	@SuppressWarnings("all")
	public RangedClassifier calculate(List<? extends Comparable> datas, int classNum) {
		Collections.sort(datas);
		Comparable globalMin = datas.get(0);
		Comparable globalMax = datas.get(datas.size() - 1);

		if ((globalMin instanceof Number) && (globalMax instanceof Number)) {
			return calculateNumerical(classNum, globalMin, globalMax);
		} else {
			return calculateNonNumerical(classNum, datas);
		}

	}

	@SuppressWarnings("all")
	public RangedClassifier calculateNumerical(int classNum, Comparable globalMin, Comparable globalMax) {
		// handle constant value case
		if (globalMax.equals(globalMin)) {
			return new RangedClassifier(new Comparable[] { globalMin }, new Comparable[] { globalMax });
		}

		double slotWidth = (((Number) globalMax).doubleValue() - ((Number) globalMin).doubleValue()) / classNum;
		// size arrays
		Comparable[] localMin = new Comparable[classNum];
		Comparable[] localMax = new Comparable[classNum];
		for (int i = 0; i < classNum; i++) {
			// calculate the min + max values
			localMin[i] = Double.valueOf(((Number) globalMin).doubleValue() + (i * slotWidth));
			localMax[i] = Double.valueOf(((Number) globalMax).doubleValue() - ((classNum - i - 1) * slotWidth));
			// determine number of decimal places to allow
			int decPlaces = decimalPlaces(slotWidth);
			// clean up truncation error
			if (decPlaces > -1) {
				localMin[i] = Double.valueOf(round(((Number) localMin[i]).doubleValue(), decPlaces));
				localMax[i] = Double.valueOf(round(((Number) localMax[i]).doubleValue(), decPlaces));
			}

			if (i == 0) {
				// ensure first min is less than or equal to globalMin
				if (localMin[i].compareTo(Double.valueOf(((Number) globalMin).doubleValue())) < 0)
					localMin[i] = Double.valueOf(fixRound(((Number) localMin[i]).doubleValue(), decPlaces, false));
			} else if (i == classNum - 1) {
				// ensure last max is greater than or equal to globalMax
				if (localMax[i].compareTo(Double.valueOf(((Number) globalMax).doubleValue())) > 0)
					localMax[i] = Double.valueOf(fixRound(((Number) localMax[i]).doubleValue(), decPlaces, true));
			}
			// synchronize min with previous max
			if ((i != 0) && (!localMin[i].equals(localMax[i - 1]))) {
				localMin[i] = localMax[i - 1];
			}
		}
		return new RangedClassifier(localMin, localMax);
	}

	@SuppressWarnings("all")
	private RangedClassifier calculateNonNumerical(int classNum, List<? extends Comparable> datas) {
		List result = datas.stream().distinct().collect(Collectors.toList());
		// sort the results and put them in an array
		Collections.sort(result);

		Comparable[] values = (Comparable[]) result.toArray(new Comparable[result.size()]);

		// size arrays
		classNum = Math.min(classNum, values.length);
		Comparable[] localMin = new Comparable[classNum];
		Comparable[] localMax = new Comparable[classNum];

		// we have 2 options here:
		// 1. break apart by numeric value: (aaa, aab, aac, bbb) --> [aaa, aab, aac],
		// [bbb]
		// 2. break apart by item count: --> [aaa, aab], [aac, bbb]

		// this code currently implements option #2 (this is a quantile, why don't we
		// use their code
		// instead)

		// calculate number of items to put in each of the larger bins
		int binPop = Double.valueOf(Math.ceil((double) values.length / classNum)).intValue();
		// determine index of bin where the next bin has one less item
		int lastBigBin = values.length % classNum;
		if (lastBigBin == 0)
			lastBigBin = classNum;
		else
			lastBigBin--;

		int itemIndex = 0;
		// for each bin
		for (int binIndex = 0; binIndex < classNum; binIndex++) {
			// store min
			if (binIndex < localMin.length)
				localMin[binIndex] = (itemIndex < values.length ? values[itemIndex] : values[values.length - 1]);
			else
				localMin[localMin.length - 1] = (itemIndex < values.length ? values[itemIndex]
						: values[values.length - 1]);
			itemIndex += binPop;
			// store max
			if (binIndex == classNum - 1) {
				if (binIndex < localMax.length)
					localMax[binIndex] = (itemIndex < values.length ? values[itemIndex] : values[values.length - 1]);
				else
					localMax[localMax.length - 1] = (itemIndex < values.length ? values[itemIndex]
							: values[values.length - 1]);
			} else {
				if (binIndex < localMax.length)
					localMax[binIndex] = (itemIndex + 1 < values.length ? values[itemIndex + 1]
							: values[values.length - 1]);
				else
					localMax[localMax.length - 1] = (itemIndex + 1 < values.length ? values[itemIndex + 1]
							: values[values.length - 1]);
			}
			if (lastBigBin == binIndex)
				binPop--; // decrease the number of items in a bin for the
			// next iteration
		}
		return new RangedClassifier(localMin, localMax);
	}

}
