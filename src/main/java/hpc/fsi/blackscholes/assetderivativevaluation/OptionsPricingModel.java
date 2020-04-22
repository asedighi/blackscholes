package hpc.fsi.blackscholes.assetderivativevaluation;

import java.io.IOException;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotionLazyInit;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.montecarlo.process.EulerSchemeFromProcessModel;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class OptionsPricingModel {

	// Model properties

	private final double initialValue;
	private final double strike;
	
	private final double startTime;
	private final double lastTime;;

	
	private final int numberOfPaths;

	
	private final double riskFreeRate = 0.02;
	private final double volatility = 0.40;
	private final double maturity = 3.0;

	// Process discretization properties
	
	private final double dt = 0.001;
	private final int seed = 53567562;

	// private AssetModelMonteCarloSimulationModel model = null;
	private EulerSchemeFromProcessModel process = null;

	public static void main(String[] args) throws IOException, CalculationException, InterruptedException {
		
		if(args.length < 5)
		{
			System.out.println("usage: OptionsPricingModel <initial price> <strike price> <start time> <final time> <num of paths>");
		}
		double initialPrice = Double.valueOf(args[0]);
		double strikePrice = Double.valueOf(args[1]);
		double initTime = Double.valueOf(args[2]);
		double finalTime = Double.valueOf(args[3]);
		int numPaths = Integer.valueOf(args[4]);
		
		
		
	
		
		
		
		
		
		final OptionsPricingModel pricingTest = new OptionsPricingModel(initialPrice, strikePrice, initTime, finalTime, numPaths);

		
		
		
		
		final long start = System.currentTimeMillis();

		pricingTest.priceOptions();
		final long end = System.currentTimeMillis();

		System.out.println("\nCalculation time required: " + (end - start) / 1000.0 + " seconds.");
	}

	public OptionsPricingModel(double initialPrice, double strikePrice, double initTime, double finalTime, int numPaths) {
		super();

		this.initialValue = initialPrice;
		this.strike = strikePrice;
		this.startTime = initTime;
		this.lastTime = finalTime;
		this.numberOfPaths = numPaths;
		
		
		// Create a Model (see method getModel)
		process = getModel();
	}

	// public AssetModelMonteCarloSimulationModel getModel()
	public EulerSchemeFromProcessModel getModel()

	{

		// Create the time discretization
		final TimeDiscretization timeDiscretization = new TimeDiscretizationFromArray(startTime, (int) (lastTime / dt), dt);

		// Test the quality of the Brownian motion
		final BrownianMotionLazyInit brownian = new BrownianMotionLazyInit(timeDiscretization, 2, numberOfPaths, seed);

		// Create an instance of a black scholes monte carlo model
		// final AssetModelMonteCarloSimulationModel model = new
		// MonteCarloBlackScholesModel(
		// timeDiscretization,
		// numberOfPaths,
		// initialValue,
		// riskFreeRate,
		// volatility);

		BlackScholesModel model = new BlackScholesModel(initialValue, riskFreeRate, volatility);

		EulerSchemeFromProcessModel process = new EulerSchemeFromProcessModel(model, brownian);

		return process;
	}

	public void priceOptions() throws CalculationException {


		/*
		 * European Option
		 */
		final EuropeanOption myEuropeanOption = new EuropeanOption(maturity, strike);

		MonteCarloAssetModel simulation = new MonteCarloAssetModel(process);


		RandomVariable valueOfEuropeanOption = myEuropeanOption.getValue(0.0, simulation);
		Double value = valueOfEuropeanOption.average().doubleValue();

		double error = valueOfEuropeanOption.getStandardError();

		/*
		 * Output
		 */
		System.out.println("Value of European Option is \t" + value);
		System.out.println("Standard error of European Option is \t" + error);

	}

}
