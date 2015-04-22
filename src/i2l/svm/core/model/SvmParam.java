package i2l.svm.core.model;

import java.io.Serializable;

public class SvmParam implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SvmParam() {
		// default values
		svmType = C_SVC;
		kernelType = RBF;
		degree = 3;
		gamma = 0;	// 1/num_features
		coef0 = 0;
		cacheSize = 100;
		C = 1;
		tolerance = 1e-3;
		shrinking = 1;
		nWeight = 0;
		weightLabel = new int[0];
		weight = new double[0];
		a_rate = 0;
	}

	/**
	 * multi-class classification
	 * for now, just consider classification problem
	 */
	public static final int C_SVC = 0;				// multi-class classification
	public static final int C_ASVC = 1;				// ascending C adaptive svm classification
	
	public static final int LINEAR = 0;
	public static final int POLY = 1;
	public static final int RBF = 2;
	public static final int SIGMOID = 3;
	
	public int svmType;
	public int kernelType;
	public int degree;			// for poly kernel
	public double gamma;		// for poly/rbf/sigmoid
	public double coef0;		// for poly/sigmoid
	
	// these are for training only
	public double cacheSize;		// in MB
	public double tolerance;				// stopping criteria (or tolerance?)
	public double C;				
	public int nWeight;				// number of weight, for C_SVC (multi-classification?)
	public int[] weightLabel;		// for C_SVC
	public double[] weight;			// for C_SVC
	public int shrinking;			// use the shrinking heuristics (Quadratic problem 풀때 사용) 
	
	// my added variables
	public double a_rate;			// rate of ascending C value
	
	public Object clone() {
		try {
			return super.clone();
			
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
}
