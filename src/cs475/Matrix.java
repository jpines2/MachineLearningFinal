package cs475;

import java.util.Random;

public class Matrix {

	private static Random rand = new Random();
	
	public static double[][] newRandom(int m, int n)
	{
		double[][] matrix = new double[m][n];
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				matrix[i][j] = rand.nextGaussian();
			}
		}
		return matrix;
	}
	
	public static double dot(double[] x, double[] y)
	{
		if (x.length != y.length)
		{
			throw new RuntimeException("Illegal vector dimensions.");
		}
		double sum = 0.0;
		for (int i = 0; i < x.length; i++)
		{
			sum += x[i] * y[i];
		}
		return sum;
	}
	
	public static double[] add1(double[] m1, double[] m2)
	{
		return weightedAdd1(m1, 1, m2);
	}
	
	public static double[] weightedAdd1(double[] m1, double w, double[] m2)
	{
		int m = m1.length;
		double[] m3 = new double[m];
		for (int i = 0; i < m; i++)
		{
			m3[i] = m1[i] + w * m2[i];
		}
		return m3;
	}
	
	public static double[][] add2(double[][] m1, double[][] m2)
	{
		//System.out.println("lengths " + m1.length + " " + m1[0].length + "  " + m2.length + " " + m2[0].length);
		return weightedAdd2(m1, 1, m2);
	}
	
	public static double[][] weightedAdd2(double[][] m1, double w, double[][] m2)
	{
		int m = m1.length;
		double[][] m3 = new double[m][];
		for (int i = 0; i < m; i++)
		{
			int n = m1[i].length;
			m3[i] = new double[n];
			for (int j = 0; j < n; j++)
			{
				m3[i][j] = m1[i][j] + w * m2[i][j];
			}
		}
		return m3;
	}
	
	public static double[][][] add3(double[][][] m1, double[][][] m2)
	{
		return weightedAdd3(m1, 1, m2);
	}
	
	public static double[][][] weightedAdd3(double[][][] m1, double w, double[][][] m2)
	{
		int m = m1.length;
		double[][][] m3 = new double[m][][];
		for (int i = 0; i < m; i++)
		{
			int n = m1[i].length;
			m3[i] = new double[n][];
			for (int j = 0; j < n; j++)
			{
				int l = m1[i][j].length;
				m3[i][j] = new double[l];
				for (int k = 0; k < l; k++)
				{
					m3[i][j][k] = m1[i][j][k] + w * m2[i][j][k];
				}
			}
		}
		return m3;
	}
	
	public static double[] multiply(double[] v, double[][] m1)
	{
		int m = m1.length;
		int n = m1[0].length;
//		System.out.println(m);
//		System.out.println(n);
		if (v.length != m) throw new RuntimeException("Illegal matrix dimension."); 
		double[] product = new double[n];
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < m; j++)
			{
				product[i] = m1[j][i] * v[j];
			}
		}
		return product;
	}
	
	public static double[] multiply(double[][] m1, double[] v1)
	{
		int m = m1.length;
		int n = m1[0].length;
//		System.out.println("v1 length: " + v1.length);
//		System.out.println("m: " + m);
//		System.out.println("n: " + n);
		if (v1.length != n) throw new RuntimeException("Illegal matrix dimension.");
		double[] v2 = new double[m];
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				v2[i] += m1[i][j] * v1[j];
			}
		}
		return v2;
	}
	
	public static double[] elementwiseMultiply(double[] v1, double[] v2)
	{
		int m = v1.length;
		int n = v2.length;
		if (m != n) throw new RuntimeException("Illegal vector dimension.");
		double[] v3 = new double[m];
		for (int i = 0; i < m; i++)
		{
			v3[i] = v1[i] * v2[i];
		}
		return v3;
	}
	
	public static double[][] elementwiseMultiply(double[][] m1, double[][] m2)
	{
		int m = m1.length;
		int n = m2.length;
		if (m != n) throw new RuntimeException("Illegal vector dimension.");
		double[][] m3 = new double[m][m1[0].length];
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < m1[0].length; j++)
			{
				m3[i][j] = m1[i][j] * m2[i][j];
			}
		}
		return m3;
	}
	
	public static double[][] matrixMultiply(double[][] m1, double[][] m2)
	{
		//System.out.println("multiply lengths " + m1.length + " " + m1[0].length + "  " + m2.length + " " + m2[0].length);
		if (m1[0].length != m2.length) throw new RuntimeException("Illegal matrix dimension.");
		int m = m1.length;
		int n = m2[0].length;
		double[][] m3 = new double[m][n];
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				m3[i][j] = dot(m1[i], getRow(m2, j));
			}
		}
		
		return m3;
	}
	
	public static double[][] vectorTransposeMultiply(double[] v1, double[] v2)
	{
		int m = v1.length;
		int n = v2.length;
		double[][] m1 = new double[m][n];
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				m1[i][j] = v1[i] * v2[j];
			}
		}
		return m1;
	}
	
	public static double[][] transpose(double[][] m1)
	{
		int m = m1.length;
		int n = m1[0].length;
		double[][] m2 = new double[n][m];
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < m; j++)
			{
				m2[i][j] = m1[j][i];
			}
		}
		return m2;
	}
	
	public static double[] getRow(double[][] m1, int index)
	{
		double[] v = new double[m1.length];
		for (int i = 0; i < m1.length; i++)
		{
			v[i] = m1[i][index];
		}
		return v;
	}
	
	public static void print(double[][][] m1)
	{
		for (int i = 0; i < m1.length; i++)
		{
			for (int j = 0; j < m1[i].length; j++)
			{
				for (int k = 0; k < m1[i][j].length; k++)
				{
					System.out.print(m1[i][j][k] + " ");
				}
			}
		}
		System.out.println();
	}
	
}
