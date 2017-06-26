/**
 * 
 */
package net.finmath.rootfinder;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import net.finmath.montecarlo.automaticdifferentiation.RandomVariableDifferentiableInterface;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * @author Stefan Sedlmair
 *
 */
public abstract class LevenbergMarquardtSolver implements RandomVariableRootFinderUsingDerivative {

	private final RandomVariableDifferentiableInterface initialValue;
	private final RandomVariableInterface targetValue;
	private final RandomVariableInterface uncertainties;
	
	private RandomVariableInterface	nextPoint;											// Stores the next point to be returned by getPoint()
	private RandomVariableInterface	bestPoint;	
	
	private int	numberOfIterations				= 0;           							// Number of numberOfIterations
	private boolean	isDone						= false;             					// Will be true if machine accuracy has been reached
	private double accuracy 					= Double.MAX_VALUE;		
	
	public LevenbergMarquardtSolver(RandomVariableDifferentiableInterface initialValue, RandomVariableInterface targetValue, RandomVariableInterface uncertainties) {
		super();		
		this.initialValue 	= initialValue;
		this.targetValue 	= targetValue;
		this.uncertainties 	= uncertainties;
		
		bestPoint 			= this.initialValue;
		nextPoint 			= this.initialValue;
	}

	public abstract RandomVariableDifferentiableInterface functionToMinimize(RandomVariableInterface[] arguments);
	
	/* (non-Javadoc)
	 * @see net.finmath.rootfinder.RandomVariableRootFinderUsingDerivative#getNextPoint()
	 */
	@Override
	public RandomVariableInterface getNextPoint() {
		return null;

	}
	
	private RandomVariableInterface estimateDelta(Map<Long, RandomVariableInterface> gradient, RandomVariableInterface lambda){
			
		int numberOfVariables = gradient.size();
		int numberOfRealizations = gradient.values().toArray().length; /* all gradients should have the same size! */
		
		double[][] A = new double[numberOfVariables][numberOfRealizations];
		double[] b = new double[numberOfRealizations];
		
		
		for(int realizationIndex = 0; realizationIndex < numberOfRealizations; realizationIndex++){
			for(int variableIndex = 0; variableIndex < numberOfVariables; variableIndex++){
				
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.finmath.rootfinder.RandomVariableRootFinderUsingDerivative#setValueAndDerivative(net.finmath.stochastic.RandomVariableInterface, net.finmath.stochastic.RandomVariableInterface)
	 */
	@Override
	public void setValueAndDerivative(RandomVariableInterface value, RandomVariableInterface derivative) {
		
		double currentAccuracy = targetValue.sub(value).div(uncertainties).squared().getAverage() * targetValue.size();
		
		if(currentAccuracy < accuracy)
		{
			accuracy	= currentAccuracy;
			bestPoint	= nextPoint;
		}

		// Calculate next point
        nextPoint = nextPoint.addRatio(value,derivative);

		numberOfIterations++;

	}

	/* (non-Javadoc)
	 * @see net.finmath.rootfinder.RandomVariableRootFinderUsingDerivative#getNumberOfIterations()
	 */
	@Override
	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	/* (non-Javadoc)
	 * @see net.finmath.rootfinder.RandomVariableRootFinderUsingDerivative#getAccuracy()
	 */
	@Override
	public double getAccuracy() {
		return accuracy;
	}

	/* (non-Javadoc)
	 * @see net.finmath.rootfinder.RandomVariableRootFinderUsingDerivative#isDone()
	 */
	@Override
	public boolean isDone() {
		return isDone;
	}

	/* (non-Javadoc)
	 * @see net.finmath.rootfinder.RandomVariableRootFinderUsingDerivative#getBestPoint()
	 */
	@Override
	public RandomVariableInterface getBestPoint() {
		return bestPoint;
	}

}
