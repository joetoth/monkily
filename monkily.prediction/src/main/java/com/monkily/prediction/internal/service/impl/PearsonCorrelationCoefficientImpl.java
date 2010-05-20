package com.monkily.prediction.internal.service.impl;

public class PearsonCorrelationCoefficientImpl {

	public static double getDistance(double sum1, double sum2) {

		double sum1sq = Math.pow(sum1, 2.0);

		double sum2sq = Math.pow(sum2, 2.0);

		double pSum = sum1 * sum2;

		double num = pSum - (sum1 * sum2);
		double den = Math.sqrt(((sum1sq - (sum1 * sum1)))
				* (sum2sq - ((sum2 * sum2))));
		if (den == 0) {
			return 0;
		}

		return num / den;
	}

}
