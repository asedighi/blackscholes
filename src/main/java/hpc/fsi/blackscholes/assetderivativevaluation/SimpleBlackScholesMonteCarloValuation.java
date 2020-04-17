/*
 * part of this code was taken from: Christian P. Fries, Germany. All rights reserved. Contact: email@christian-fries.de.
 *
 * Created on 19.01.2004, 21.12.2012, 01.02.2012
 */


package hpc.fsi.blackscholes.assetderivativevaluation;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.time.TimeDiscretizationFromArray;

/**
 * A simple demo on how to value a financial product with a given model.
 *
 * This relates to the "separation of model and product". The only assumptions
 * made by the valuation code in the product is that the model implements
 * the interface {@code AssetModelMonteCarloSimulationModel}.
 *
 * @author Christian Fries
 * @author asedighi
 */
public class SimpleBlackScholesMonteCarloValuation {

	/**
	 * Demo program.
	 *
	 * @param args Arguments. Not used.
	 * @throws CalculationException
	 */
	public static void main(String[] args) throws CalculationException {

		final AssetModelMonteCarloSimulationModel model = new MonteCarloBlackScholesModel(new TimeDiscretizationFromArray(0, 10, 0.5), 10000, 100, 0.05, 0.20);

		final AbstractAssetMonteCarloProduct product = new EuropeanOption(2.0, 110);

		final double value = product.getValue(model);

		System.out.println("The value is " + value);
	}

}
