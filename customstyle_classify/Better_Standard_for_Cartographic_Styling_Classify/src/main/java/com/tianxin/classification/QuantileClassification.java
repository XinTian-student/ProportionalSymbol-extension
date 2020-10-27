package com.tianxin.classification;

import org.geotools.filter.function.ExplicitClassifier;
import org.geotools.filter.function.RangedClassifier;

import java.util.*;

/**
 * Quantile
 * 
 *
 * @author X_T
 *
 * @version
 *
 * @since 25/08/2020
 */
public class QuantileClassification extends Classification {

	@Override
	@SuppressWarnings("all")
	public RangedClassifier calculate(List<? extends Comparable> datas, int classNum) {
		List[] bin = (List[]) getResult(datas, classNum);

		// generate the min and max values, and round off if applicable/necessary
		Comparable globalMin = (Comparable) bin[0].toArray()[0];
		Object lastBin[] = bin[bin.length - 1].toArray();
		if (lastBin.length == 0) {
			return null;
		}
		Comparable globalMax = (Comparable) lastBin[lastBin.length - 1];

		if ((globalMin instanceof Number) && (globalMax instanceof Number)) {
			return (RangedClassifier) calculateNumerical(bin, globalMin, globalMax);
		} else {
			return (RangedClassifier) calculateNonNumerical(bin, globalMin, globalMax);
		}
	}

	@SuppressWarnings("all")
	public ArrayList[] getResult(List<? extends Comparable> items, int bins) {
		ArrayList[] bin = new ArrayList[bins];
		int count = items.size();
		if (bins == 0 || count == 0) {
			return new ArrayList[0];
		}

		// sort the list
		Collections.sort(items);

		if (bins > count) { // resize
			bins = count;
			bin = new ArrayList[bins];
		}

		// calculate number of items to put into each of the larger bins
		int binPop = Double.valueOf(Math.ceil((double) count / bins)).intValue();
		// determine index of bin where the next bin has one less item
		int lastBigBin = count % bins;
		if (lastBigBin == 0)
			lastBigBin = bins;
		else
			lastBigBin--;

		// put the items into their respective bins
		int item = 0;
		for (int binIndex = 0; binIndex < bins; binIndex++) {
			bin[binIndex] = new ArrayList();
			for (int binMember = 0; binMember < binPop; binMember++) {
				bin[binIndex].add(items.get(item++));
			}
			if (lastBigBin == binIndex)
				binPop--; // decrease the number of items in a bin for the next item
		}
		return bin;
	}

	@SuppressWarnings("all")
	private Object calculateNumerical(List[] bin, Comparable globalMin, Comparable globalMax) {
		if (globalMax.equals(globalMin)) {
			return new RangedClassifier(new Comparable[] { globalMin }, new Comparable[] { globalMax });
		}

		int classNum = bin.length;
		// size arrays
		Comparable[] localMin = new Comparable[classNum];
		Comparable[] localMax = new Comparable[classNum];
		// globally consistent
		// double slotWidth = (((Number) globalMax).doubleValue() - ((Number)
		// globalMin).doubleValue()) / classNum;
		for (int i = 0; i < classNum; i++) {
			// copy the min + max values
			List thisBin = bin[i];
			localMin[i] = (Comparable) thisBin.get(0);
			localMax[i] = (Comparable) thisBin.get(thisBin.size() - 1);
			// locally accurate
			double slotWidth = ((Number) localMax[i]).doubleValue() - ((Number) localMin[i]).doubleValue();
			if (slotWidth == 0.0) { // use global value, as there is only 1 value in this set
				slotWidth = (((Number) globalMax).doubleValue() - ((Number) globalMin).doubleValue()) / classNum;
			}
			// determine number of decimal places to allow
			int decPlaces = decimalPlaces(slotWidth);
			decPlaces = Math.max(decPlaces, decimalPlaces(((Number) localMin[i]).doubleValue()));
			decPlaces = Math.max(decPlaces, decimalPlaces(((Number) localMax[i]).doubleValue()));
			// clean up truncation error
			if (decPlaces > -1) {
				localMin[i] = Double.valueOf(round(((Number) localMin[i]).doubleValue(), decPlaces));
				localMax[i] = Double.valueOf(round(((Number) localMax[i]).doubleValue(), decPlaces));
			}

			if (i == 0) {
				// ensure first min is less than or equal to globalMin
				if (localMin[i].compareTo(Double.valueOf(((Number) globalMin).doubleValue())) > 0)
					localMin[i] = Double.valueOf(fixRound(((Number) localMin[i]).doubleValue(), decPlaces, false));
			} else if (i == classNum - 1) {
				// ensure last max is greater than or equal to globalMax
				if (localMax[i].compareTo(Double.valueOf(((Number) globalMax).doubleValue())) < 0)
					localMax[i] = Double.valueOf(fixRound(((Number) localMax[i]).doubleValue(), decPlaces, true));
			}

			// synchronize previous max with current min; the ranged classifier is min <= x
			// < y;
			if (i != 0) {
				localMax[i - 1] = localMin[i];
			}
		}
		// TODO: disallow having 2 identical bins (ie 0..0, 0..0, 0..0, 0..100)
		return new RangedClassifier(localMin, localMax);
	}

	@SuppressWarnings("all")
	private Object calculateNonNumerical(List[] bin, Comparable globalMin, Comparable globalMax) {
		if (globalMax.equals(globalMin)) {
			return new ExplicitClassifier(new Set[] { Collections.singleton(globalMin) });
		}

		int classNum = bin.length;
		// it's a string.. leave it be (just copy the values)
		Set[] values = new Set[classNum];
		for (int i = 0; i < classNum; i++) {
			values[i] = new HashSet();
			Iterator iterator = bin[i].iterator();
			while (iterator.hasNext()) {
				values[i].add(iterator.next());
			}
		}
		return new ExplicitClassifier(values);
		// alternative for ranged classifier
		// localMin[i] = (Comparable) thisBin.get(0);
		// localMax[i] = (Comparable) thisBin.get(thisBin.size()-1);
	}

}
