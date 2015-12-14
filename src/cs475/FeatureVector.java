package cs475;

import java.io.Serializable;
import java.util.HashMap;

public class FeatureVector implements Serializable {

	private HashMap<Integer, Double> featureMap;
	private int maxIndex;
	
	public FeatureVector() {
		this.featureMap = new HashMap<Integer, Double>();
		this.maxIndex = 0;
	}

	public void add(int index, double value) {
		this.featureMap.put(index, value);
		this.maxIndex = index > maxIndex ? index : maxIndex;
	}
	
	public double get(int index) {
		if (!this.featureMap.containsKey(index))
		{
			return 0;
		}
		return this.featureMap.get(index);
	}
	
	public int size()
	{
		return this.maxIndex;
	}
	
	public int maxIndex()
	{
		return this.maxIndex;
	}
	
	public double[] toArray()
	{
		return toArray(this.size());
	}
	
	public double[] toArray(int size)
	{
		double[] array = new double[size];
		for (int i = 0; i < this.size(); i++)
		{
			array[i] = this.get(i);
		}
		return array;
	}
	
	public double[][] toMatrix(int size)
	{
		double[][] array = new double[size][1];
		for (int i = 0; i < this.size(); i++)
		{
			array[i] = new double[] {this.get(i)};
		}
		return array;
	}

}
