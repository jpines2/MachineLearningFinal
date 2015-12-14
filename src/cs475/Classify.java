package cs475;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class Classify {
	static public LinkedList<Option> options = new LinkedList<Option>();
	
	public static void main(String[] args) throws IOException {
		// Parse the command line.
		String[] manditory_args = { "mode"};
		createCommandLineOptions();
		CommandLineUtilities.initCommandLineParameters(args, Classify.options, manditory_args);
	
		String mode = CommandLineUtilities.getOptionValue("mode");
		String[] data = CommandLineUtilities.getOptionValues("data");
		String predictions_file = CommandLineUtilities.getOptionValue("predictions_file");
		String algorithm = CommandLineUtilities.getOptionValue("algorithm");
		String model_file = CommandLineUtilities.getOptionValue("model_file");

		int sgd_iterations = 30;
		if (CommandLineUtilities.hasArg("sgd_iterations"))
			sgd_iterations = CommandLineUtilities.getOptionValueAsInt("sgd_iterations");
		double sgd_eta0 = 1.0;
		if (CommandLineUtilities.hasArg("sgd_eta0"))
			sgd_eta0 = CommandLineUtilities.getOptionValueAsFloat("sgd_eta0");
		double pegasos_lambda = 1e-4;
		if (CommandLineUtilities.hasArg("pegasos_lambda"))
		    pegasos_lambda = CommandLineUtilities.getOptionValueAsFloat("pegasos_lambda");
		int folds = 7;
		if (CommandLineUtilities.hasArg("folds"))
		    folds = CommandLineUtilities.getOptionValueAsInt("folds");
		
		if (mode.equalsIgnoreCase("train")) {
			if (data == null || algorithm == null || model_file == null) {
				System.out.println("Train requires the following arguments: data, algorithm, model_file");
				System.exit(0);
			}
			// Load the training data.
			DataReader data_reader = new DataReader(data, true);
			List<Instance> instances = data_reader.readData();
			data_reader.close();
			
			// Train the model.
			Predictor predictor = train(instances, algorithm, sgd_iterations, sgd_eta0, pegasos_lambda);
			saveObject(predictor, model_file);		
			
		} else if (mode.equalsIgnoreCase("test")) {
			if (data == null || predictions_file == null || model_file == null) {
				System.out.println("Train requires the following arguments: data, predictions_file, model_file");
				System.exit(0);
			}
			
			// Load the test data.
			DataReader data_reader = new DataReader(data, true);
			List<Instance> instances = data_reader.readData();
			data_reader.close();
			
			// Load the model.
			Predictor predictor = (Predictor)loadObject(model_file);
			evaluateAndSavePredictions(predictor, instances, predictions_file);
		} else if (mode.equalsIgnoreCase("cross_validate")) {
			if (data == null || predictions_file == null ||algorithm == null || model_file == null) {
				System.out.println("Cross_validate requires the following arguments: data, algorithm, model_file, predictions_file");
				System.exit(0);
			}
			
			// Load the data.
			DataReader data_reader = new DataReader(data, true);
			List<Instance> instances = data_reader.readData();
			data_reader.close();
			
			// Partition data.
			Collections.shuffle(instances);
			List<Instance>[] subsamples = (List<Instance>[]) new ArrayList[folds];
			int subsampleSize = instances.size() / folds;
			for (int i = 0; i < folds; i++)
			{
				subsamples[i] = new ArrayList<Instance>(instances.subList(i * subsampleSize, (i + 1) * subsampleSize));
			}
			
			double totalAccuracy = 0.0;
			for (int i = 0; i < subsamples.length; i++)
			{
				List<Instance> trainingInstances = new ArrayList<Instance>();
				for (int j = 0; j < subsamples.length; j++)
				{
					if (i != j)
					{
						trainingInstances.addAll(subsamples[j]);
					}
				}
				// Train the model.
				Predictor predictor = train(instances, algorithm, sgd_iterations, sgd_eta0, pegasos_lambda);
				// Test
				totalAccuracy += evaluateAndSavePredictions(predictor, subsamples[i], predictions_file);
			}
			System.out.println("Accuracy: " + totalAccuracy / subsamples.length);
		} else if (mode.equalsIgnoreCase("all")) {
			String[] tumorFiles = new String[] {"m20330_supervised_dump.txt", "m30330_supervised_dump.txt", "m40329_supervised_dump.txt", "m40330_supervised_dump.txt", "m50329_supervised_dump.txt"};
			String healthyFile = "Hm10_supervised_dump.txt";
			for (int k = 0; k < tumorFiles.length; k++)
			{
				String[] datas = new String[] { "MachineLearningFinal/data/supervised/" + healthyFile, "MachineLearningFinal/data/supervised/" + tumorFiles[k]};
				DataReader data_reader = new DataReader(datas, true);
				List<Instance> instances = data_reader.readData();
				data_reader.close();
				
				int numTrials = 20;
				double[] accuracies = new double[25];
				for (int l = 0; l < numTrials; l++)
				{
					// Partition data.
					Collections.shuffle(instances);
					List<Instance>[] subsamples = (List<Instance>[]) new ArrayList[folds];
					int subsampleSize = instances.size() / folds;
					for (int i = 0; i < folds; i++)
					{
						subsamples[i] = new ArrayList<Instance>(instances.subList(i * subsampleSize, (i + 1) * subsampleSize));
					}
					
					double totalAccuracy = 0.0;
					for (int i = 0; i < subsamples.length; i++)
					{
						List<Instance> trainingInstances = new ArrayList<Instance>();
						for (int j = 0; j < subsamples.length; j++)
						{
							if (i != j)
							{
								trainingInstances.addAll(subsamples[j]);
							}
						}
						// Train the model.
						Predictor predictor = train(instances, algorithm, sgd_iterations, sgd_eta0, pegasos_lambda);
						// Test
						totalAccuracy += evaluateAndSavePredictions(predictor, subsamples[i], predictions_file);
					}
					//System.out.println("Accuracy: " + totalAccuracy / subsamples.length);
					accuracies[l] = totalAccuracy / subsamples.length;
				}
				System.out.println("File: " + tumorFiles[k]);
				System.out.println("Mean: " + mean(accuracies));
				System.out.println("Variance: " + variance(accuracies));
				System.out.println();
			}
		}
		else {
			System.out.println("Requires mode argument.");
		}
	}
	

	private static Predictor train(List<Instance> instances, String algorithm, int sgd_iterations, double sgd_eta0, double pegasos_lambda) {
		// TODO Train the model using "algorithm" on "data"
		Predictor predictor;
		int[] sizes = new int[]{13, 10, 1};
		predictor = new Network(sizes, sgd_iterations, 10 , sgd_eta0);
		predictor.train(instances);
		// TODO Evaluate the model
		//Evaluator evaluator = new AccuracyEvaluator();
		//System.out.println(evaluator.evaluate(instances, predictor));
		return predictor;
	}

	private static double evaluateAndSavePredictions(Predictor predictor,
			List<Instance> instances, String predictions_file) throws IOException {
		PredictionsWriter writer = new PredictionsWriter(predictions_file);
		// TODO Evaluate the model if labels are available. 
		Evaluator evaluator = new AccuracyEvaluator();
		double result = evaluator.evaluate(instances, predictor);
		for (Instance instance : instances) {
			Label label = predictor.predict(instance);
			writer.writePrediction(label);
		}
		
		writer.close();
		return result;
		
	}

	public static void saveObject(Object object, String file_name) {
		try {
			ObjectOutputStream oos =
				new ObjectOutputStream(new BufferedOutputStream(
						new FileOutputStream(new File(file_name))));
			oos.writeObject(object);
			oos.close();
		}
		catch (IOException e) {
			System.err.println("Exception writing file " + file_name + ": " + e);
		}
	}

	/**
	 * Load a single object from a filename. 
	 * @param file_name
	 * @return
	 */
	public static Object loadObject(String file_name) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(file_name))));
			Object object = ois.readObject();
			ois.close();
			return object;
		} catch (IOException e) {
			System.err.println("Error loading: " + file_name);
		} catch (ClassNotFoundException e) {
			System.err.println("Error loading: " + file_name);
		}
		return null;
	}
	
	public static void registerOption(String option_name, String arg_name, boolean has_arg, String description) {
		OptionBuilder.withArgName(arg_name);
		OptionBuilder.hasArg(has_arg);
		OptionBuilder.withDescription(description);
		Option option = OptionBuilder.create(option_name);
		
		Classify.options.add(option);		
	}
	
	private static void createCommandLineOptions() {
		registerOption("data", "String", true, "The data to use.");
		registerOption("mode", "String", true, "Operating mode: train or test.");
		registerOption("predictions_file", "String", true, "The predictions file to create.");
		registerOption("algorithm", "String", true, "The name of the algorithm for training.");
		registerOption("model_file", "String", true, "The name of the model file to create/load.");
		registerOption("sgd_eta0", "double", true, "The constant scalar for learning rate in AdaGrad.");
		registerOption("sgd_iterations", "int", true, "The number of SGD iterations.");
		registerOption("pegasos_lambda", "double", true, "The regularization parameter for Pegasos.");
		
		// Other options will be added here.
	}
	
	private static double mean(double[] a)
	{
		double total = 0.0;
		for (int i = 0; i < a.length; i++)
		{
			total += a[i];
		}
		return total / a.length;
	}
	
	private static double variance(double[] a)
	{
		double mean = mean(a);
		double total = 0.0;
		for (int i = 0; i < a.length; i++)
		{
			total += (a[i] - mean) * (a[i] - mean);
		}
		return total / a.length;
	}
}
