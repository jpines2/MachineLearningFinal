package cs475;

import java.io.Serializable;
import java.util.List;

public class AccuracyEvaluator extends Evaluator implements Serializable {

	@Override
	public double evaluate(List<Instance> instances, Predictor predictor) {
		// TODO Auto-generated method stub
		double matches = 0;
		for (Instance instance : instances)
		{
			if (instance.getLabel() == null)
			{
				return 0;
			}
			if (instance.getLabel().toString().equals(predictor.predict(instance).toString()))
			{
				matches += 1;
			}
		}
		return matches / instances.size();
	}

}
