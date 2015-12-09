package cs475.KCluster;

import cs475.Instance;
import cs475.FeatureVector;

import java.util.ArrayList;
import java.util.Set;

public class KClusteringAlgorithm {

    private KClusteringParameters parameters;
    private int iterations;
    private double convergence;

    public KClusteringAlgorithm(KClusteringParameters parameters, int iterations, double convergence) {
	this.parameters = parameters;
	this.iterations = iterations;
	this.convergence = convergence;
    }

    public void reportClusterInformation() {
	int[] cluster_count = new int[parameters.numClusters()];
	for (int i = 0; i < parameters.numExamples(); i++) {
	    int ca = parameters.getClusterAssignment(i);
	    cluster_count[ca] += 1;
	} 
	for (int i = 0; i < parameters.numClusters(); i++) {
	    System.out.println("Number voxels in cluster " + (i+1) + ": " + cluster_count[i]);
	}
    }

    public void cluster() {
	System.out.println("Examples: " + parameters.numExamples());
        ArrayList<Instance> examples = parameters.getExamples();
        int[] init_assignment = this.init_assignments(examples);
	parameters.updateAssignment(init_assignment);
        for (int t = 0; t < iterations; t++) {
	    System.out.println("Iteration: " + (t+1));
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
                System.out.println("Has converged after: " + (t+1) + " iterations.");
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
	    assignments[ca] = 1;
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
		updated_clusters[i][j] = (cluster_sum[i][j]/cluster_count[i]);
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
