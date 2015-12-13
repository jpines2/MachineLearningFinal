package cs475.KCluster;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import cs475.FeatureVector;
import cs475.Instance;
import cs475.Label;
import cs475.ClassificationLabel;


public class KClusteringParameters {

    private ArrayList<Instance> examples;
    private double cluster[][];
    private int assignment[];

    private int num_clusters;
    private int num_features;
    private int num_examples;
    
    
    public KClusteringParameters(String data_file, int num_clusters) throws FileNotFoundException {
	this.examples = new ArrayList<Instance>();
	this.num_clusters = num_clusters;	
	Scanner scanner = new Scanner(new BufferedInputStream(new FileInputStream(data_file))); 
	while (scanner.hasNextLine()) {
	    String line = scanner.nextLine();
	    if (line.trim().length() == 0) {
		continue;
	    }
	    Label label = new ClassificationLabel(-1);
	    FeatureVector fv = new FeatureVector();
	    String[] split_line = line.split(" ");
	    this.num_features = split_line.length;
	    for (int ii = 0; ii < split_line.length; ii++) {
	        //System.out.println(split_line[ii]);
	        String item = split_line[ii];
	        String name = item.split(":")[0];
		int index = Integer.parseInt(name);
		double value = Double.parseDouble(item.split(":")[1]);
		fv.add(index, value);
	    }
	    Instance instance = new Instance(fv, label);
	    examples.add(instance);
	}
	this.num_examples = this.examples.size();
	//init_clusters();
	this.cluster = new double[this.num_clusters][this.num_features];
	this.assignment = new int[this.num_examples];
	
    }
    /*private void init_clusters() {
	int dimension_size = 2;
	double init_loc = (double) dimension_size / this.num_clusters;
	this.cluster = new double[this.num_clusters][this.num_features];
		    
	for (int i = 0; i < this.num_clusters; i++) {
	    for (int j = 0; j < this.num_features; j++) {
		this.cluster[i][j] = -1*dimension_size+(init_loc*i*2);
	    }
	}
	this.assignment = new int[this.num_examples];
    } */
 
    public int numExamples() {
	return this.num_examples;
    }
    public int numClusters() {
        return this.num_clusters;
    }
    public int numFeatures() {
        return this.num_features;
    }
    public double[][] getClusters() {
        return this.cluster;
    }
    public double getClusterDimensionValue(int cluster, int dimension) {
	return this.cluster[cluster][dimension];
    }
    public int getClusterAssignment(int example_num) {
	return this.assignment[example_num];
    }
    public void updateAssignment(int[] updated_assignment)  {
	this.assignment = updated_assignment;
    }
    public ArrayList<Instance> getExamples() {
	return this.examples;
    }
    public void updateClusters(double[][] updated_clusters) {
	this.cluster = updated_clusters;
    }
	
}
