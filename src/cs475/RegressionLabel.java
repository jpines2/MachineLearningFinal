package cs475;

import java.io.Serializable;

public class RegressionLabel extends Label implements Serializable {
	private double label;

	public RegressionLabel(double label) {
		// TODO Auto-generated constructor stub
		this.label = label;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "" + this.label;
	}

}
