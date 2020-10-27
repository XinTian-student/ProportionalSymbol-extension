package com.tianxin.classification;

import org.geotools.filter.function.RangedClassifier;

import java.util.List;

/**
 * 
 * Classification methods
 *
 * @author X_T
 *
 * @version
 *
 * @since 14/08/2020
 */
public abstract class Classification {

	/**
	 * 
	 * Description： classification methods
	 *
	 * @param datas
	 * @param classNum
	 * @return
	 * 
	 * @author X_T
	 *
	 * @since 14/08/2020
	 *
	 */
	public abstract RangedClassifier calculate(@SuppressWarnings("rawtypes") List<? extends Comparable> datas,
                                               int classNum);

	@SuppressWarnings("all")
	protected int decimalPlaces(double slotWidth) {
		if (slotWidth == 0) {
			return 5;
		}
		String str = Double.toString(slotWidth);
		if (str.indexOf(".") > -1) {
			while (str.endsWith("0")) {
				str = str.substring(0, str.length() - 1);
			}
		}
		int intPart = Double.valueOf(Math.floor(slotWidth)).intValue();
		double decPart = slotWidth - intPart;
		int intPoints = Integer.toString(intPart).length();
		int decPoints = str.length() - intPoints;
		if (str.indexOf(".") > -1) {
			decPoints--;
		}
		if (decPart == 0) {
			decPoints = 0;
		}
		// if there are dec points, show at least one
		if (intPart == 0) { // if int part is 0, be very specific
			if (decPoints > 6) {
				return 5;
			} else if (decPoints > 0) {
				return decPoints;
			} else {
				return 1;
			}
		} else if (decPoints == 0) { // if there are no dec points, don't show any
			return 0;
		} else { // aim for a number of digits (not including '.') up to a reasonable limit (5)
			int chars = intPoints + decPoints;
			if (chars < 6) {
				return decPoints;
			} else if (intPoints > 4) {
				return 1;
			} else {
				return 5 - intPoints;
			}
		}
	}

	/**
	 * Truncates a double to a certain number of decimals places. Note: truncation
	 * at zero decimal places will still show up as x.0, since we're using the
	 * double type.
	 *
	 * @param value         number to round-off
	 * @param decimalPlaces number of decimal places to leave
	 * @return the rounded value
	 */
	@SuppressWarnings("all")
	protected double round(double value, int decimalPlaces) {
		double divisor = Math.pow(10, decimalPlaces);
		double newVal = value * divisor;
		newVal = Math.round(newVal) / divisor;
		return newVal;
	}

	/**
	 * Corrects a round off operation by incrementing or decrementing the decimal
	 * place (preferably the smallest one). This should usually be used to adjust
	 * the bounds to include a value. Example: 0.31-->0.44 where 0.44 is the maximum
	 * value and end of the range. We could just make the , round(0.31, 1)=0.3;
	 * round(0.44 max value） = 0.49
	 *
	 * @param value
	 * @param decimalPlaces
	 * @param up
	 */
	protected double fixRound(double value, int decimalPlaces, boolean up) {
		double divisor = Math.pow(10, decimalPlaces);
		double newVal = value * divisor;
		if (up)
			newVal++; // +0.001 (for 3 dec places)
		else
			newVal--; // -0.001
		newVal = newVal / divisor;
		return newVal;
	}

}
