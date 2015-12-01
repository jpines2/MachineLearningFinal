package cs475;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Network extends Predictor implements Serializable
{
	private Random rand;
	private int numLayers;
	private int[] sizes;
	private double[][] biases;
	private double[][][] weights;
	private int epochs;
	private int miniBatchSize;
	private double eta;

	public Network(int[] sizes, int epochs, int miniBatchSize, double eta)
	{
		this.rand = new Random();
		this.sizes = sizes;
		this.epochs = epochs;
		this.miniBatchSize = miniBatchSize;
		this.eta = eta;
		this.numLayers = sizes.length;
		this.inializeWeights();
	}
	
	private double[] feedforward(double[] a)
	{
		for (int i = 0; i < this.numLayers - 1; i++)
		{
			a = sigmoid(Matrix.add1(Matrix.multiply(this.weights[i], a), this.biases[i]));
		}
		return a;
	}
	
	public void SGD(List<Instance> instances)
	{
		int n = instances.size();
		for (int j = 0; j < this.epochs; j++)
		{
			Collections.shuffle(instances); // TODO: 
			List<List<Instance>> miniBatches = new ArrayList<List<Instance>>();
			for (int k = 0; k < n; k+=this.miniBatchSize)
			{
				miniBatches.add(instances.subList(k, Math.min(k+this.miniBatchSize, n-1)));
			}
			for (List<Instance> miniBatch: miniBatches)
			{
				this.updateMiniBatch(miniBatch, this.eta);
			}
			//System.out.println("Epoch " + j + " complete");
		}
	}
	
	private void updateMiniBatch(List<Instance> miniBatch, double eta)
	{
		double[][] gradientsB = newBiasesMatrix(true);
		double[][][] gradientsW = newWeightsMatrix(true);
		Gradients gradients;
		for (Instance instance: miniBatch)
		{
			gradients = this.backprop(instance);
			gradientsB = Matrix.add2(gradientsB, gradients.getGradientsB());
			gradientsW = Matrix.add3(gradientsW, gradients.getGradientsW());
		}
		this.biases = Matrix.weightedAdd2(this.biases, eta / miniBatch.size(), gradientsB);
		this.weights = Matrix.weightedAdd3(this.weights, eta / miniBatch.size(), gradientsW);
	}
	
	private Gradients backprop(Instance instance)
	{
		double[][] gradientsB = newBiasesMatrix(true);
		double[][][] gradientsW = newWeightsMatrix(true);
		
		double y = Double.parseDouble(instance.getLabel().toString()); //TODO:
		double[] activation = instance.getFeatureVector().toArrary();
		double[][] activations = new double[this.numLayers - 1][];
		activations[0] = activation;
		double[][] zs = new double[this.numLayers - 1][];

		int n = this.numLayers - 1;
		for (int i = 0; i < n; i++)
		{
			double[] z = Matrix.add1(Matrix.multiply(activation, this.weights[i]), this.biases[i]);
			zs[i] = z;
			activation = sigmoid(z);
			activations[i+1] = activation;
		}
		double[] delta = Matrix.elementwiseMultiply(this.costDerivative(activations[activations.length - 1], y), //TODO:
				 						 			sigmoidPrime(zs[zs.length - 1]));
		gradientsB[gradientsB.length - 1] = delta;
		gradientsW[gradientsW.length - 1] = Matrix.vectorTransposeMultiply(delta, activations[activations.length - 2]);
		for (int l = 2; l < this.numLayers; l++)
		{
			double[] z = zs[zs.length - l];
			double sp[] = sigmoidPrime(z);
			delta = Matrix.elementwiseMultiply(Matrix.multiply(Matrix.transpose(this.weights[this.weights.length-l+1]), delta),
											   sp);
			gradientsB[gradientsB.length - l] = delta;
			gradientsW[gradientsW.length - l] = Matrix.vectorTransposeMultiply(delta, activations[activations.length - l - 1]);
		}
		Gradients gradients = new Gradients();
		gradients.setGradients(gradientsB, gradientsW);
		return gradients;
	}
	
	private void inializeWeights()
	{
		this.biases = newBiasesMatrix(false);
		this.weights = newWeightsMatrix(false);
	}
	
	private double[][] newBiasesMatrix(Boolean zeros)
	{
		double[][] matrix = new double[this.numLayers-1][];
		for (int i = 0; i < this.numLayers - 1; i++)
		{
			matrix[i] = new double[this.sizes[i]];
			for (int j = 0; j < this.sizes[i]; j++)
			{
				if (zeros)
				{
					matrix[i][j] = 0;
				}
				else
				{
					matrix[i][j] = this.rand.nextGaussian();
				}
			}
		}
		return matrix;
	}
	
	private double[][][] newWeightsMatrix(Boolean zeros)
	{
		double[][][] matrix = new double[this.numLayers-1][][];
		for (int i = 0; i < this.numLayers - 1; i++)
		{
			matrix[i] = new double[this.sizes[i]][this.sizes[i+1]];
			for (int j = 0; j < this.sizes[i]; j++)
			{
				for (int k = 0; k < this.sizes[i+1]; k++)
				{
					if (zeros)
					{
						matrix[i][j][k] = 0;
					}
					else
					{
						matrix[i][j][k] = this.rand.nextGaussian();
					}
				}
			}
		}
		return matrix;
	}
	
	private int evaluate(List<Instance> testData)
	{
		int correct = 0;
		for (int i = 0; i < testData.size(); i++)
		{
			double[] result = feedforward(testData.get(i).getFeatureVector().toArrary());
			if (result[0] == Double.parseDouble(testData.get(i).getLabel().toString())) //TODO:
			{
				correct += 1;
			}
		}
		return correct;
	}
	
	private double[] costDerivative(double[] outputActivations, double y)
	{
		double[] ret = new double[outputActivations.length];
		for (int i= 0; i < outputActivations.length; i++)
		{
			ret[i] = outputActivations[i] + y;
		}
		return ret;
	}
	
	private double[] sigmoid(double[] z)
	{
		double[] sig = new double[z.length];
		for (int i = 0; i < z.length; i++)
		{
			sig[i] = this.sigmoid(z[i]);
		}
		return sig;
	}
	
	private double sigmoid(double z)
	{
		return (1.0 / 1.0 + Math.exp(-1.0 * z));
	}
	
	private double[] sigmoidPrime(double[] z)
	{
		double[] sig = new double[z.length];
		for (int i = 0; i < z.length; i++)
		{
			sig[i] = this.sigmoidPrime(z[i]);
		}
		return sig;
	}
	
	private double sigmoidPrime(double z)
	{
		return this.sigmoid(z) * (1 - this.sigmoid(z));
	}

	@Override
	public void train(List<Instance> instances) {
		SGD(instances);
	}

	@Override
	public Label predict(Instance instance) {
		double[] a = instance.getFeatureVector().toArrary();
		double[] result = feedforward(a);
		return new RegressionLabel(result[0]); //TODO:
	}
}
