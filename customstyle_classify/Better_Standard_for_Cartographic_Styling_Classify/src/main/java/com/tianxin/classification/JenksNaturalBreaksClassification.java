package com.tianxin.classification;

import org.geotools.filter.function.RangedClassifier;

import java.util.Collections;
import java.util.List;

/**
 * 
 * Natural break
 *
 * @author X_T
 *
 * @version
 *
 * @since 15/08/2020
 */
public class JenksNaturalBreaksClassification extends Classification {

	@Override
	@SuppressWarnings("all")
	public RangedClassifier calculate(List<? extends Comparable> datas, int classNum) {
		Collections.sort(datas);

		if (datas.size() == 1 || datas.get(0).equals(datas.get(datas.size() - 1))) {
			return new RangedClassifier(new Comparable[] { datas.get(0) }, new Comparable[] { datas.get(0) });
		}
		final int k = classNum;
		final int m = datas.size();
		if (k == m) {
			Comparable[] localMin = new Comparable[k];
			Comparable[] localMax = new Comparable[k];

			for (int id = 0; id < k - 1; id++) {

				localMax[id] = datas.get(id + 1);
				localMin[id] = datas.get(id);
			}
			localMax[k - 1] = datas.get(k - 1);
			localMin[k - 1] = datas.get(k - 1);
			return new RangedClassifier(localMin, localMax);
		}
		int[][] iwork = new int[m + 1][k + 1];
		double[][] work = new double[m + 1][k + 1];

		for (int j = 1; j <= k; j++) {
			// the first item is always in the first class!
			iwork[0][j] = 1;
			iwork[1][j] = 1;
			// initialize work matirix
			work[1][j] = 0;
			for (int i = 2; i <= m; i++) {
				work[i][j] = Double.MAX_VALUE;
			}
		}

		// calculate the class for each tempalte item
		for (int i = 1; i <= m; i++) {
			// sum of tempalte values
			double s1 = 0;
			// sum of squares of tempalte values
			double s2 = 0;

			double var = 0.0;
			// consider all the previous values
			for (int ii = 1; ii <= i; ii++) {
				// index in to sorted tempalte array
				int i3 = i - ii + 1;
				// remember to allow for 0 index
				double val = getValue(datas, i3);
				// update running totals
				s2 = s2 + (val * val);
				s1 += val;
				double s0 = (double) ii;
				// calculate (square of) the variance
				// (http://secure.wikimedia.org/wikipedia/en/wiki/Standard_deviation#Rapid_calculation_methods)
				var = s2 - ((s1 * s1) / s0);
				// System.out.println(s0+" "+s1+" "+s2);
				// System.out.println(i+","+ii+" var "+var);
				int ik = i3 - 1;
				if (ik != 0) {
					// not the last value
					for (int j = 2; j <= k; j++) {
						// for each class compare current value to var + previous value
						// System.out.println("\tis "+work[i][j]+" >= "+(var + work[ik][j - 1]));
						if (work[i][j] >= (var + work[ik][j - 1])) {
							// if it is greater or equal update classification
							iwork[i][j] = i3 - 1;
							// System.out.println("\t\tiwork["+i+"]["+j+"] = "+i3);
							work[i][j] = var + work[ik][j - 1];
						}
					}
				}
			}
			// store the latest variance!
			iwork[i][1] = 1;
			work[i][1] = var;
		}
		// go through matrix and extract class breaks
		int ik = m - 1;

		Comparable[] localMin = new Comparable[k];
		Comparable[] localMax = new Comparable[k];
		localMax[k - 1] = datas.get(ik);
		for (int j = k; j >= 2; j--) {
			int id = (int) iwork[ik][j] - 1; // subtract one as we want inclusive breaks on the
			// left?

			localMax[j - 2] = datas.get(id);
			localMin[j - 1] = datas.get(id);
			ik = (int) iwork[ik][j] - 1;
		}
		localMin[0] = datas.get(0);
		/*
		 * for(int k1=0;k1<k;k1++) { //
		 * System.out.println(k1+" "+localMin[k1]+" - "+localMax[k1]); }
		 */
		return new RangedClassifier(localMin, localMax);

	}

	private Double getValue(@SuppressWarnings("rawtypes") List<? extends Comparable> datas, int i3) {
		return Double.parseDouble(datas.get(i3 - 1).toString());
	}

}
