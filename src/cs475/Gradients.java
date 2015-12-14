package cs475;

public class Gradients {
	private double[][][] gradientsB;
	private double[][][] gradientsW;
	
	public Gradients()
	{
	}
	
	public double[][][] getGradientsB()
	{
		return this.gradientsB;
	}
	
	public double[][][] getGradientsW()
	{
		return this.gradientsW;
	}
	
	public double[][][] getGradients()
	{
		return this.gradientsW;
	}
	
	public void setGradients(double[][][] gradientsB, double[][][] gradientsW)
	{
		this.setGradientsB(gradientsB);
		this.setGradientsW(gradientsW);
	}
	
	public void setGradientsB(double[][][] gradientsB)
	{
		this.gradientsB = gradientsB;
	}
	
	public void setGradientsW(double[][][] gradientsW)
	{
		this.gradientsW = gradientsW;
	}
	
	
}
