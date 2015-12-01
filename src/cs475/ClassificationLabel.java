package cs475;

import java.io.Serializable;

public class ClassificationLabel extends Label implements Serializable {
	int label;

	public ClassificationLabel(int label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "" + this.label;
	}

}
