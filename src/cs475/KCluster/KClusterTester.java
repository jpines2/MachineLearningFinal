package cs475.KCluster;
import java.util.LinkedList;
import java.util.Scanner;
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
                int testing = 0;
                if (CommandLineUtilities.hasArg("testing")) {
                    testing = CommandLineUtilities.getOptionValueAsInt("testing");
                }
                double alpha = 1.0;
                if (CommandLineUtilities.hasArg("alpha")) {
		    alpha = CommandLineUtilities.getOptionValueAsFloat("alpha");
		}
	        int random = 0;
	        if (CommandLineUtilities.hasArg("init_random")) {
		    random = CommandLineUtilities.getOptionValueAsInt("init_random");
		}
		cluster(data_file, gd_iterations, convergence, num_clusters, testing, alpha, random);
	}

	public static void cluster(String data_file, int gd_iterations, double convergence, int num_clusters, int testing, double alpha, int random) throws FileNotFoundException {
		KClusteringParameters parameters = new KClusteringParameters(data_file, num_clusters);
		KClusteringAlgorithm kclustering = new KClusteringAlgorithm(parameters, gd_iterations, convergence);
		kclustering.cluster(alpha, random);
	        kclustering.reportClusterInformation(true);
                if (testing == 0) { return; }
                double[][] clusters = parameters.getClusters();
		

                KClusteringParameters p2;
                KClusteringAlgorithm k2;
                for (int i = 0; i < 7; i++) {
                    String dset;
                    if (i == 0) {
                        dset = "../data/unsupervised/m10330_unsupervised_dump.txt";
                    } else if (i == 1) {
                        dset = "../data/unsupervised/m20329_unsupervised_dump.txt";
                    } else if (i == 2) {
                        dset = "../data/unsupervised/m20330_unsupervised_dump.txt";
		    } else if (i == 3) {
                        dset = "../data/unsupervised/m30330_unsupervised_dump.txt";
                    } else if (i == 4) {
                        dset = "../data/unsupervised/m40329_unsupervised_dump.txt";
                    } else if (i == 5) {
                        dset = "../data/unsupervised/m40330_unsupervised_dump.txt";
                    } else {
                        dset = "../data/unsupervised/m50329_unsupervised_dump.txt";
                    }

		    p2 = new KClusteringParameters(dset, num_clusters);
                    k2 = new KClusteringAlgorithm(p2, 0, 0);

                    k2.single_iteration(clusters);
                    k2.reportClusterInformation(true);
                }
                /*Scanner scan = new Scanner(System.in);
                while (true) {
                    System.err.print("Do you want to assign clusters to another dataset? (input: y/n): ");
                    char usr = scan.next().charAt(0);
                    if (usr == 'n') {
                        System.err.println("Quitting");
                        break;
                    }
                    System.err.print("Enter dataset: " );
                    String dset = scan.next();
                    p2 = new KClusteringParameters(dset, num_clusters);
                    k2 = new KClusteringAlgorithm(p2, 0, 0);
	            
		    k2.single_iteration(clusters);
                    k2.reportClusterInformation();
                } */
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
                registerOption("testing", "int", true, "Whether or not to use the clusters obtained from data as the assignmed clusters for other data sets");
		registerOption("alpha", "double", true, "Controls the probability distribution over distance");
		registerOption("init_random", "int", true, "Whether or not to randomely initialize first cluster");
		// Other options will be added here.
	}
}


