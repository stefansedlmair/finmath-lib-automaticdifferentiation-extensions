/**
 * 
 */
package net.finmath.montecarlo.automaticdifferentiation.forward;

import net.finmath.montecarlo.AbstractRandomVariableFactory;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * @author mdm33ee
 *
 */
public class RandomVariableADFactory extends AbstractRandomVariableFactory {

	/**
	 * 
	 */
	public RandomVariableADFactory() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.finmath.montecarlo.AbstractRandomVariableFactory#createRandomVariable(double, double)
	 */
	@Override
	public RandomVariableInterface createRandomVariable(double time, double value) {
		return new RandomVaribaleAD(time, value);
	}

	/* (non-Javadoc)
	 * @see net.finmath.montecarlo.AbstractRandomVariableFactory#createRandomVariable(double, double[])
	 */
	@Override
	public RandomVariableInterface createRandomVariable(double time, double[] values) {
		return new RandomVaribaleAD(time, values);
	}

}
