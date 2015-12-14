package cs475;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class DataReader {

	private Scanner[] _scanners;
	// Classification or regression?
	private boolean _classification;

	public DataReader(String[] filenames, boolean classification) throws FileNotFoundException {
		this._scanners = new Scanner[filenames.length];
		for (int i = 0; i < filenames.length; i++)
		{
			this._scanners[i] = new Scanner(new BufferedInputStream(new FileInputStream(filenames[i])));
		}
		this._classification = classification;
	}
	
	public void close() {
		for (Scanner scanner: this._scanners)
		{
			scanner.close();
		}
	}
	
	public List<Instance> readData() {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		
		for (Scanner scanner: this._scanners)
		{
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.trim().length() == 0)
					continue;
				
				FeatureVector feature_vector = new FeatureVector();
			
				// Divide the line into features and label.
				String[] split_line = line.split(" ");
	
				String label_string = split_line[0];
				Label label = null;
				if (this._classification) {
					int int_label = Integer.parseInt(label_string);
					if (int_label == 0) {
						int_label = -1;
					}
					label = new ClassificationLabel(int_label);
				} else {
					try {
						double double_label = Double.parseDouble(label_string);
						label = new RegressionLabel(double_label);
					} catch (Exception e) {
						
					}
				}
				for (int ii = 1; ii < split_line.length; ii++) {
					String item = split_line[ii];
					String name = item.split(":")[0];
					int index = Integer.parseInt(name);
					double value = Double.parseDouble(item.split(":")[1]);
					
					if (value != 0)
						feature_vector.add(index, value);
				}
				
				Instance instance = new Instance(feature_vector, label);
				instances.add(instance);
			}	
		}
		
		return instances;
	}
}
