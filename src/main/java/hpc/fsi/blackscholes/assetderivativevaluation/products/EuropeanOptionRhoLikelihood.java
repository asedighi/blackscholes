/*
 * part of this code was taken from: Christian P. Fries, Germany. All rights reserved. Contact: email@christian-fries.de.
 *
 * Created on 19.01.2004, 21.12.2012, 01.02.2012
 */


package hpc.fsi.blackscholes.assetderivativevaluation.products;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.stochastic.RandomVariable;
import net.finmath.stochastic.RandomVariableAccumulator;

/**
 * Implements calculation of the delta of a European option.
 *
 * @author Christian Fries
 * @version 1.0
 * @author asedighi
 */
public class EuropeanOptionRhoLikelihood extends AbstractAssetMonteCarloProduct {

	private final double	maturity;
	private final double	strike;

	private final boolean	isLikelihoodByFiniteDifference = true;

	/**
	 * Construct a product representing an European option on an asset S (where S the asset with index 0 from the model - single asset case).
	 *
	 * @param strike The strike K in the option payoff max(S(T)-K,0).
	 * @param maturity The maturity T in the option payoff max(S(T)-K,0)
	 */
	public EuropeanOptionRhoLikelihood(double maturity, double strike) {
		super();
		this.maturity = maturity;
		this.strike = strike;
	}

	/**
	 * Calculates the value of the option under a given model.
	 *
	 * @param model A reference to a model
	 * @return the value
	 * @throws CalculationException
	 */
	public double getValue(AssetModelMonteCarloSimulationModel model) throws CalculationException
	{
		MonteCarloBlackScholesModel blackScholesModel = null;
		try {
			blackScholesModel = (MonteCarloBlackScholesModel)model;
		}
		catch(final Exception e) {
			throw new ClassCastException("This method requires a Black-Scholes type model (MonteCarloBlackScholesModel).");
		}

		// Get underlying and numeraire
		final RandomVariable underlyingAtMaturity	= model.getAssetValue(maturity,0);
		final RandomVariable numeraireAtMaturity		= model.getNumeraire(maturity);
		final RandomVariable underlyingAtToday		= model.getAssetValue(0.0,0);
		final RandomVariable numeraireAtToday		= model.getNumeraire(0);
		final RandomVariable monteCarloWeights		= model.getMonteCarloWeights(maturity);

		/*
		 *  The following way of calculating the expected value (average) is discouraged since it makes too strong
		 *  assumptions on the internals of the <code>RandomVariableAccumulator</code>. Instead you should use
		 *  the mutators sub, div, mult and the getter getAverage. This code is provided for illustrative purposes.
		 */
		double average = 0.0;
		for(int path=0; path<model.getNumberOfPaths(); path++)
		{
			if(underlyingAtMaturity.get(path) > strike)
			{
				// Get some model parameters
				final double T		= maturity;
				final double S0		= underlyingAtToday.get(path);
				final double r		= blackScholesModel.getModel().getRiskFreeRate().doubleValue();
				final double sigma	= blackScholesModel.getModel().getVolatility().doubleValue();

				final double ST		= underlyingAtMaturity.get(path);

				final double x		= 1.0 / (sigma * Math.sqrt(T)) * (Math.log(ST) - (r * T - 0.5 * sigma*sigma * T + Math.log(S0)));

				double lr;
				if(isLikelihoodByFiniteDifference) {
					final double h		= 1E-6;

					final double x1		= 1.0 / (sigma * Math.sqrt(T)) * (Math.log(ST) - (r     * T - 0.5 * sigma*sigma * T + Math.log(S0)));
					final double logPhi1	= Math.log(1.0/Math.sqrt(2 * Math.PI) * Math.exp(-x1*x1/2.0) / (ST * sigma * Math.sqrt(T)) );

					final double x2		= 1.0 / (sigma * Math.sqrt(T)) * (Math.log(ST) - ((r+h) * T - 0.5 * sigma*sigma * T + Math.log(S0)));
					final double logPhi2	= Math.log(1.0/Math.sqrt(2 * Math.PI) * Math.exp(-x2*x2/2.0) / (ST * sigma * Math.sqrt(T)) );

					lr		= (logPhi2 - logPhi1) / h;
				}
				else {
					final double dxdr = -1.0 / (sigma * Math.sqrt(T));

					lr		= - x * dxdr;
				}

				final double payOff			= (underlyingAtMaturity.get(path) - strike);
				final double modifiedPayoff	= payOff * (lr-T);

				average += modifiedPayoff / numeraireAtMaturity.get(path) * monteCarloWeights.get(path) * numeraireAtToday.get(path);
			}
		}

		return average;
	}

	@Override
	public RandomVariableAccumulator getValue(double evaluationTime, AssetModelMonteCarloSimulationModel model) {
		throw new RuntimeException("Method not supported.");
	}
}
