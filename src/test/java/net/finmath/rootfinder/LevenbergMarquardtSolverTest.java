/**
 * 
 */
package net.finmath.rootfinder;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.finmath.montecarlo.AbstractRandomVariableFactory;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.montecarlo.automaticdifferentiation.RandomVariableDifferentiableInterface;
import net.finmath.montecarlo.automaticdifferentiation.backward.RandomVariableDifferentiableAADFactory;
import net.finmath.montecarlo.automaticdifferentiation.backward.alternative.RandomVariableAADv2Factory;
import net.finmath.montecarlo.automaticdifferentiation.backward.alternative.RandomVariableAADv3Factory;
import net.finmath.randomnumbers.MersenneTwister;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * @author Stefan Sedlmair
 *
 */
@RunWith(Parameterized.class)
public class LevenbergMarquardtSolverTest {
	
	/* parameters specify the factories one wants to test against each other */
	@Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][] {
        	{new RandomVariableDifferentiableAADFactory()},
        	{new RandomVariableAADv3Factory()},
        	{new RandomVariableAADv2Factory()}
        });
    }

    private final AbstractRandomVariableFactory randomVariableFactory;
    
    public LevenbergMarquardtSolverTest(AbstractRandomVariableFactory factory) {
    	this.randomVariableFactory = factory;
    	System.out.println(randomVariableFactory.getClass().getSimpleName());
    }
    
	@Test
	public void PolynomialFittingTest() {
	
		/* --------------------- problem set up -------------------------*/
		
		long seed  = 1234;
		MersenneTwister RNG = new MersenneTwister(seed);
		
		int numberOfRealization = (int)Math.pow(10, 5);
		int numberOfArguments 	= 10;
		
		// create random vectors for arguments
		TreeMap<Long, RandomVariableInterface> initialArguments = new TreeMap<>();
		
		double[] realizations = new double[numberOfRealization];
		
		// generate x argument
		for(int j = 0; j < numberOfRealization; j++)
			realizations[j] = RNG.nextDouble();
		RandomVariableInterface x = new RandomVariable(0.0, realizations);
		
		// generate target function value
		for(int j = 0; j < numberOfRealization; j++)
			realizations[j] = RNG.nextDouble();
		RandomVariableInterface targetFunctionValue = new RandomVariable(0.0, realizations);
		
		// generate arguments that shall be fitted
		for(int i = 0; i < numberOfArguments; i++){
			for(int j = 0; j < numberOfRealization; j++)
				realizations[j] = RNG.nextDouble();
			
			RandomVariableDifferentiableInterface argument = (RandomVariableDifferentiableInterface) randomVariableFactory.createRandomVariable(0.0, realizations);
			
			initialArguments.put(argument.getID(), argument);
		}
		
		/* --------------------- solver set up -------------------------*/
		
		
		int maxNumberOfIterations = 100;
		double targetAccuracy = Math.pow(10, -6);
		
		
		LevenbergMarquardtSolver LMSolver = new LevenbergMarquardtSolver(initialArguments, targetFunctionValue);

		RandomVariableDifferentiableInterface[] nextParametersArray = new RandomVariableDifferentiableInterface[numberOfArguments];

		long totalTime = 0;
		long totalMem = 0;
		
		while(LMSolver.getAccuracy() > targetAccuracy && LMSolver.getNumberOfIterations() < maxNumberOfIterations && Double.isFinite(LMSolver.getLambda())){
			
			long startMem = getAllocatedMemory();
			long startTime = System.currentTimeMillis();
			
			TreeMap<Long, RandomVariableInterface> nextParameters = (TreeMap<Long, RandomVariableInterface>) LMSolver.getNextParameters();
			
			int argumentIndex = 0;
			for(long key : nextParameters.keySet())
				nextParametersArray[argumentIndex++] = (RandomVariableDifferentiableInterface) nextParameters.get(key);
			
			RandomVariableDifferentiableInterface currentFunctionValue = polynomialFuction(x, nextParametersArray);
			
			LMSolver.setValueAndDerivative(currentFunctionValue, currentFunctionValue.getGradient());
			
			long endTime = System.currentTimeMillis();
			long endMem = getAllocatedMemory();
			
//			System.out.println("Step#:..............." + LMSolver.getNumberOfIterations());
//			System.out.println("Accuracy:............" + LMSolver.getAccuracy());
//			System.out.println("Lambda:.............." + LMSolver.getLambda());
//			System.out.println("duration:............" + (double)(endTime - startTime)/1000.0 + "s");
//			System.out.println("memory consumption:.." + (double)(endMem - startMem)/Math.pow(10, 6) + "Mbyte");

			totalTime += endTime - startTime;
			totalMem += endMem - startMem;
		}
		System.out.println("number of Iterations........................." + LMSolver.getNumberOfIterations());
		System.out.println("average duration per Iteration..............." + (double)totalTime/ ((double)LMSolver.getNumberOfIterations() *1000.0) +"s ");
		System.out.println("average memory consumption per Iteration....." + (double)totalMem/ ((double)LMSolver.getNumberOfIterations() * Math.pow(10, 6)) +"Mbyte");
		System.out.println();

		Assert.assertEquals(0.0, Math.max(LMSolver.getAccuracy() - targetAccuracy,0.0), 0.0);
			
	}
	
	private RandomVariableDifferentiableInterface polynomialFuction(RandomVariableInterface x, RandomVariableDifferentiableInterface[] a){
		RandomVariableInterface polynom = a[0];
		for(int i = 1; i < a.length; i++)
			polynom = polynom.addProduct(a[i], x.pow(i));
			
		return (RandomVariableDifferentiableInterface) polynom;
	}
	
	static long getAllocatedMemory() {
		System.gc();
		long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
		return allocatedMemory;
	}

}
