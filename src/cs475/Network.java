package cs475;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

public class Network extends Predictor implements Serializable
{
	private Random rand;
	private int numLayers;
	private int[] sizes;
	private double[][][] biases;
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
	
	private double[][] feedforward(double[][] a)
	{
		for (int i = 0; i < this.numLayers - 1; i++)
		{
			double[][] b = Matrix.matrixMultiply(this.weights[i], a);
//			System.out.println("layer: " + i);
//			System.out.println("a");
//			for (int k = 0; k < a.length; k++)
//			{
//				for (int j = 0; j < a[0].length; j++)
//				{
//					System.out.print(a[k][j] + " ");
//				}
//			}
//			System.out.println();
//			System.out.println("weights");
//			if (i == 1)
//			{
//				for (int k = 0; k < this.weights[i].length; k++)
//				{
//					for (int j = 0; j < this.weights[k].length; j++)
//					{
//						System.out.print(this.weights[i][k][j] + " ");
//					}
//				}
//			}
//
//			System.out.println();
//			System.out.println("without bias added");
//			for (int k = 0; k < b.length; k++)
//			{
//				for (int j = 0; j < b[0].length; j++)
//				{
//					System.out.print(b[k][j] + " ");
//				}
//			}
//			System.out.println();
//			System.out.println("Biases:");
//			for (int k = 0; k < this.biases[i].length; k++)
//			{
//				for (int j = 0; j < this.biases[i][0].length; j++)
//				{
//					System.out.print(this.biases[i][k][j] + " ");
//				}
//			}
//			System.out.println();
			a = sigmoid(Matrix.add2(Matrix.matrixMultiply(this.weights[i], a), this.biases[i]));
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
			for (int k = 0; k < n - this.miniBatchSize; k+=this.miniBatchSize)
			{
				miniBatches.add(instances.subList(k, Math.min(k+this.miniBatchSize, n-1)));
			}
			int count = 0; //TODO remove
			for (List<Instance> miniBatch: miniBatches)
			{
				this.updateMiniBatch(miniBatch, this.eta);
				count++; //TODO remove
			}
		}
	}
	
	private void updateMiniBatch(List<Instance> miniBatch, double eta)
	{
		double[][][] gradientsB = newBiasesMatrix(true);
		double[][][] gradientsW = newWeightsMatrix(true);
		Gradients gradients;
		for (Instance instance: miniBatch)
		{
			gradients = this.backprop(instance);
			//gradientsB = Matrix.add3(gradientsB, gradients.getGradientsB());
			//gradientsW = Matrix.add3(gradientsW, gradients.getGradientsW());
			this.biases = Matrix.weightedAdd3(this.biases, eta, gradients.getGradientsB());
			this.weights = Matrix.weightedAdd3(this.weights, eta, gradients.getGradientsW());
		}
		//this.biases = Matrix.weightedAdd3(this.biases, eta / miniBatch.size(), gradientsB);
		//Matrix.print(gradientsW);
		//this.weights = Matrix.weightedAdd3(this.weights, eta / miniBatch.size(), gradientsW);
	}
	
	private Gradients backprop(Instance instance)
	{
		double[][][] gradientsB = newBiasesMatrix(true);
		double[][][] gradientsW = newWeightsMatrix(true);
		
		double y = Double.parseDouble(instance.getLabel().toString()); //TODO:
		double[][] activation = instance.getFeatureVector().toMatrix(this.sizes[0]);
		double[][][] activations = new double[this.numLayers][][];
		activations[0] = activation;
		double[][][] zs = new double[this.numLayers - 1][][];

		int n = this.numLayers - 1;
		for (int i = 0; i < n; i++)
		{
			double[][] z = Matrix.add2(Matrix.matrixMultiply(this.weights[i], activation), this.biases[i]);
			zs[i] = z;
			activation = sigmoid(z);
			activations[i+1] = activation;
		}
		double[][] delta_1 = Matrix.elementwiseMultiply(this.costDerivative(activations[activations.length - 1], y), //TODO:
				 						 			sigmoid(zs[zs.length - 1]));
		double[][] delta_2 = Matrix.elementwiseMultiply(this.costFunction(activations[activations.length-1],y), sigmoidPrime(zs[zs.length-1]));
		double[][] weight_delta_1 = new double[delta_1.length][delta_1[0].length];
		for (int i = 0; i < delta_1.length; i++) {
			for (int j = 0; j < delta_1[0].length; j++) {
				delta_1[i][j] *= -1;
				weight_delta_1[i][j] = (delta_1[i][j]*y);
			}
		}
		double[][] weight_delta = Matrix.add2(weight_delta_1, delta_2);
		double[][] delta = Matrix.add2(delta_1,delta_2);

		
		gradientsB[gradientsB.length - 1] = delta;
		gradientsW[gradientsW.length - 1] = Matrix.matrixMultiply(weight_delta, Matrix.transpose(activations[activations.length - 2])); //TODO: remove transpose
		for (int l = 2; l < this.numLayers; l++)
		{
			double[][] z = zs[zs.length - l];
			double sp[][] = sigmoidPrime(z);
			delta = Matrix.elementwiseMultiply(Matrix.matrixMultiply(Matrix.transpose(this.weights[this.weights.length-l+1]), delta), //matrix.transpose
											   sp);
			/*weight_delta = new double[delta.length][delta[0].length];
			for (int i = 0; i < delta.length; i++) {
				for (int j = 0; j < delta[0].length; j++) {
					weight_delta[i][j] *= y;
				}
			} */
			gradientsB[gradientsB.length - l] = delta;
			gradientsW[gradientsW.length - l] = Matrix.matrixMultiply(delta, Matrix.transpose(activations[activations.length - l - 1])); // vectortransposemultiply
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
	
	private double[][][] newBiasesMatrix(Boolean zeros)
	{
		double[][][] matrix = new double[this.numLayers-1][][];
		for (int i = 0; i < this.numLayers - 1; i++)
		{
			matrix[i] = new double[this.sizes[i+1]][];
			for (int j = 0; j < this.sizes[i+1]; j++)
			{
				if (zeros)
				{
					matrix[i][j] = new double[] {0.0};
				}
				else
				{
					matrix[i][j] = new double[] {this.rand.nextGaussian()};
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
			matrix[i] = new double[this.sizes[i+1]][this.sizes[i]];
			for (int j = 0; j < this.sizes[i]; j++)
			{
				for (int k = 0; k < this.sizes[i+1]; k++)
				{
					if (zeros)
					{
						matrix[i][k][j] = 0;
					}
					else
					{
						matrix[i][k][j] = this.rand.nextGaussian();
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
			double[][] result = feedforward(testData.get(i).getFeatureVector().toMatrix(this.sizes[0]));
			if (result[i][0] == Double.parseDouble(testData.get(i).getLabel().toString())) //TODO:
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
			ret[i] = y-outputActivations[i];
		}
		return ret;
	}
	
	private double[][] costFunction(double[][] outputActivations, double y) {
		double[][] ret = new double[outputActivations.length][outputActivations[0].length];
		for (int i = 0; i < outputActivations.length; i++) {
			for (int j = 0; j < outputActivations[0].length; j++) {
			    ret[i][j] = 0.5 * (outputActivations[i][j] - y) * (outputActivations[i][j] - y);
			}
		}
		return ret;
	}
	
	private double[][] costDerivative(double[][] outputActivations, double y)
	{
		double[][] ret = new double[outputActivations.length][outputActivations[0].length];
		for (int i = 0; i < outputActivations.length; i++)
		{
			for (int j = 0; j < outputActivations[0].length; j++)
			{
				ret[i][j] = (outputActivations[i][j]-y);
			}
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
	
	private double[][] sigmoid(double[][] z)
	{
		double[][] sig = new double[z.length][z[0].length];
		for (int i = 0; i < sig.length; i++)
		{
			for (int j = 0; j < sig[0].length; j++)
			{
				sig[i][j] = sigmoid(z[i][j]);
			}
		}
		return sig;
	}
	
	private double sigmoid(double z)
	{
		//System.out.println(z);
		//System.out.println("sigmoid: " + (1.0 / (1.0 + Math.exp(-1.0 * z))));
		return (1.0 / (1.0 + Math.exp(-1.0 * z)));
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
	
	private double[][] sigmoidPrime(double[][] z)
	{
		double[][] sig = new double[z.length][z[0].length];
		for (int i = 0; i < sig.length; i++)
		{
			for (int j = 0; j < sig[0].length; j++)
			{
				sig[i][j] = sigmoidPrime(z[i][j]);
			}
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
		//System.out.println("feature 1: " + instance.getFeatureVector().get(1));
		double[][] a = instance.getFeatureVector().toMatrix(this.sizes[0]);
		double[][] result = feedforward(a);
		//System.out.println("result: " + result[0][0]);
		//System.out.println(instance.getLabel().toString());
		//System.out.println();
		int label = 0;
		if (result[0][0] >= 0.5)
		{
			label = 1;
		}
		else {
			label = -1;
		}
		return new ClassificationLabel(label); //TODO:
	}
}
