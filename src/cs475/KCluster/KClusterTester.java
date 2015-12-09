package cs475.KCluster;
import java.util.LinkedList;
import java.io.FileNotFoundException;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import cs475.CommandLineUtilities;

public class KClusterTester {
	static public LinkedList<Option> options = new LinkedList<Option>();
	
	public static void main(String[] args) throws FileNotFoundException {
		String[] manditory_args = { "data"};
		createCommandLineOptions();
		CommandLineUtilities.initCommandLineParameters(args, KClusterTester.options, manditory_args);
		
		String data_file = CommandLineUtilities.getOptionValue("data");
				
		int gd_iterations = 20;
		if (CommandLineUtilities.hasArg("gd_iterations"))
			gd_iterations = CommandLineUtilities.getOptionValueAsInt("gd_iterations");
		double convergence = 1e-5;
		if (CommandLineUtilities.hasArg("gd_convergence"))
			convergence = CommandLineUtilities.getOptionValueAsFloat("gd_convergence");
		int num_clusters = 5;
		if (CommandLineUtilities.hasArg("clusters")) {
	            num_clusters = CommandLineUtilities.getOptionValueAsInt("clusters");
		}
		cluster(data_file, gd_iterations, convergence, num_clusters);
	}

	public static void cluster(String data_file, int gd_iterations, double convergence, int num_clusters) throws FileNotFoundException {
		KClusteringParameters parameters = new KClusteringParameters(data_file, num_clusters);
		KClusteringAlgorithm kclustering = new KClusteringAlgorithm(parameters, gd_iterations, convergence);
		kclustering.cluster();
	        kclustering.reportClusterInformation();
	}
	
	public static void registerOption(String option_name, String arg_name, boolean has_arg, String description) {
		OptionBuilder.withArgName(arg_name);
		OptionBuilder.hasArg(has_arg);
		OptionBuilder.withDescription(description);
		Option option = OptionBuilder.create(option_name);
		
		KClusterTester.options.add(option);		
	}
	
	private static void createCommandLineOptions() {
		registerOption("data", "String", true, "The data to use.");
		registerOption("gd_convergence", "double", true, "The constant scalar for learning rate.");
		registerOption("gd_iterations", "int", true, "The number of iterations.");
		registerOption("clusters", "int", true, "The number of clusters for the algorithm");
		// Other options will be added here.
	}
}


