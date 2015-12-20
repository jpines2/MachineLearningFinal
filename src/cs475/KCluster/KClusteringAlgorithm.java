package cs475.KCluster;

import cs475.Instance;
import cs475.FeatureVector;

import java.util.ArrayList;
import java.util.Set;
import java.util.Random;

public class KClusteringAlgorithm {

    private KClusteringParameters parameters;
    private int iterations;
    private double convergence;

    public KClusteringAlgorithm(KClusteringParameters parameters, int iterations, double convergence) {
	this.parameters = parameters;
	this.iterations = iterations;
	this.convergence = convergence;
    }

    public void reportClusterInformation(boolean testing) {
	int[] cluster_count = new int[parameters.numClusters()];
	for (int i = 0; i < parameters.numExamples(); i++) {
	    int ca = parameters.getClusterAssignment(i);
	    cluster_count[ca] += 1;
	} 
	for (int i = 0; i < parameters.numClusters(); i++) {
	    if (!testing) {
	        //System.err.println("Number voxels in cluster " + (i+1) + ": " + ((double)cluster_count[i]/parameters.numExamples()));
	        int five = 0;
	    } else {
	        System.out.print((double)cluster_count[i]/parameters.numExamples() + "\t");
	    }
	}
	if (!testing) { int five = 1; }
	else { System.out.println(); }
    }

    private double[][] init_clusters(ArrayList<Instance> examples, double alpha, int init_random) { 
        double[][] clusters = new double[parameters.numClusters()][parameters.numFeatures()];
	Random candy = new Random(0);
	int[] cluster_examples = new int[parameters.numClusters()];
	for (int i = 0; i < cluster_examples.length; i++) {
	    cluster_examples[i] = -1;
	}
	int init_center;
	if (init_random != 0) {	
	    init_center = candy.nextInt(parameters.numExamples());
	} else {
	    init_center = 0;
	}
	cluster_examples[0] = init_center;
	double[] prob_distribution;
	for (int c = 1; c < parameters.numClusters(); c++) {
	    prob_distribution = new double[parameters.numExamples()];
	    for (int t = 0; t < parameters.numExamples(); t++) {
		double[] point = examples.get(t).getFeatureVector().toArray();
	        int min_cluster = -1;
	        double min_distance = 1000.0;
	        for (int j = 0; j < cluster_examples.length; j++) {
		    if (cluster_examples[j] != -1) {
		        FeatureVector fv = examples.get(cluster_examples[j]).getFeatureVector();
		        double[] center = fv.toArray();
		        double distance = 0.0;
			for (int k = 0; k < parameters.numFeatures(); k++) {
			    distance += this.euclidean_distance(point[k],center[k]);
		        }
			if (distance < min_distance) {
			    min_distance = distance;
			    min_cluster = j;
			}
		       
		    }
		}
		if (min_distance == 0) {
		    prob_distribution[t] = 0.0;
		} else {
		   // prob_distribution[t] = 1/(min_distance*min_distance);
		   //prob_distribution[t] = (min_distance*min_distance);
	           prob_distribution[t] = 1-(Math.exp(-alpha*min_distance*min_distance));
		}
	    }
	    double normalizing_factor = 0.0;
	    for (int i = 0; i < prob_distribution.length; i++) {
	        normalizing_factor += prob_distribution[i];
	    }
	    double random = Math.random();
	    //System.out.println("UF: " + random);
	    double cumulative = 0.0;
	    for (int i = 0; i < prob_distribution.length;i++) {
		prob_distribution[i] /= normalizing_factor;
	        cumulative += prob_distribution[i];
		if (random <= cumulative) {
		    //System.out.println("Example: " + i);
		    cluster_examples[c] = i;
		    break;
		}	            
	    }
	
	}
	for (int i = 0; i < cluster_examples.length; i++) {
	    int example = cluster_examples[i];
	    double[] point = examples.get(example).getFeatureVector().toArray();
	    for (int j = 0; j < parameters.numFeatures(); j++) {
		clusters[i][j] = point[j];
	    }
	}
	return clusters;
    }
		        
    public void single_iteration(double[][] clusters) {
        parameters.updateClusters(clusters);
        ArrayList<Instance> examples = parameters.getExamples();
        int[] init_assignment = this.init_assignments(examples);
	parameters.updateAssignment(init_assignment);
    }
	    
    public void cluster(double alpha, int random) {
	//System.out.println("Examples: " + parameters.numExamples());
        ArrayList<Instance> examples = parameters.getExamples();
	double[][] init_clusters = this.init_clusters(examples, alpha, random);
	/*for (int i = 0; i < parameters.numClusters(); i++) {
	    for (int j = 0; j < parameters.numFeatures();j++){
		System.out.print(init_clusters[i][j] + " ");
	    }
	    System.out.println();
	} */
	parameters.updateClusters(init_clusters);
        int[] init_assignment = this.init_assignments(examples);
	parameters.updateAssignment(init_assignment);
	if (iterations == -1) {
	    iterations = 10000;
	}
	//System.err.println("Initial Report");
	reportClusterInformation(false);
        for (int t = 0; t < iterations; t++) {
	    //System.out.println("Iteration: " + (t+1));
	    double[][] updated_cluster = this.calculateClusterMean(examples);
            boolean converged = this.hasConverged(updated_cluster);
	    parameters.updateClusters(updated_cluster);
	    int[] updated_assignment = new int[parameters.numExamples()];
                
	    for (int i = 0; i < parameters.numExamples(); i++) {
                FeatureVector fv = examples.get(i).getFeatureVector();
	
	        int cluster = this.designateCluster(fv, i);
                updated_assignment[i] = cluster;
	    }
            parameters.updateAssignment(updated_assignment);
            if (converged) {
                //System.out.println("Has converged after: " + (t+1) + " iterations.");
		break;
            }
	}
    }
    private boolean hasConverged(double[][] updated_cluster) {
	for (int i = 0; i < parameters.numClusters(); i++) {
	    double difference_distance = 0.0;
	    for (int j = 0; j < parameters.numFeatures(); j++) {
		double new_pos = updated_cluster[i][j];
		double old_pos = parameters.getClusterDimensionValue(i,j);
		difference_distance += this.euclidean_distance(new_pos, old_pos);
		if (difference_distance > this.convergence) {
		    return false;
		}
	    }
        }
	return true;
	
    }
    private int[] init_assignments(ArrayList<Instance> examples) {
	int[] assignments = new int[parameters.numExamples()];
	for (int i = 0; i < parameters.numExamples(); i++) {
	    FeatureVector fv = examples.get(i).getFeatureVector();
	    int ca = this.designateCluster(fv, i);
	    assignments[i] = ca;
	}
	return assignments;
    }
    private double[][] calculateClusterMean(ArrayList<Instance> examples) {
        double[][] updated_clusters = new double[parameters.numClusters()][parameters.numFeatures()];
       
	double[] cluster_count = new double[parameters.numClusters()];
	double[][] cluster_sum = new double[parameters.numClusters()][parameters.numFeatures()];
        for (int i = 0; i < parameters.numExamples(); i++) {
            int cluster_assignment = parameters.getClusterAssignment(i);
	    cluster_count[cluster_assignment] += 1;
	    double[] feature_values = examples.get(i).getFeatureVector().toArray();
	    for (int j = 0; j < parameters.numFeatures(); j++) {
	        cluster_sum[cluster_assignment][j] += feature_values[j];
	    }
	
	}
	for (int i = 0; i < parameters.numClusters(); i++) {
 		
	    for (int j = 0; j < parameters.numFeatures(); j++) {
		if (cluster_count[i] == 0) { 
		    updated_clusters[i][j] = parameters.getClusterDimensionValue(i,j);
		} else {
		    updated_clusters[i][j] = (cluster_sum[i][j]/cluster_count[i]);
	        }
	     }
        }
        return updated_clusters;

    }

    private int designateCluster(FeatureVector fv, int example_num) {
	double[] fv_values = fv.toArray();
        double min_distance = 1000.0;
	int cluster_assignment = -1;
	for (int i = 0; i < parameters.numClusters(); i++) {
	    double cluster_distance = 0.0;
	    for (int j = 0; j < fv_values.length; j++) {
	    	double cluster_pos = parameters.getClusterDimensionValue(i, j);
		cluster_distance += this.euclidean_distance(cluster_pos, fv_values[j]);
	   }
           if (cluster_distance < min_distance) { 
	       min_distance = cluster_distance;
               cluster_assignment = i;
	   }
            
	}
        return cluster_assignment;
    }
    private double euclidean_distance(double x, double y) {
	return ((x-y)*(x-y));
    }
}
