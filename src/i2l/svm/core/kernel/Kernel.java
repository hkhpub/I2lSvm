package i2l.svm.core.kernel;

import i2l.svm.core.model.SvmNode;
import i2l.svm.core.model.SvmParam;

public abstract class Kernel extends QMatrix {

	private SvmNode[][] x;
	private double[] x_square;

	// svm parameter
	private int kernelType;
	private int degree;
	private double gamma;
	private double coef0;

	public abstract float[] get_Q(int column, int len);

	public abstract double[] get_QD();

	@Override
	public void swap_index(int i, int j) {
		do {
			SvmNode[] _ = x[i];
			x[i] = x[j];
			x[j] = _;
		} while (false);
		if (x_square != null)
			do {
				double _ = x_square[i];
				x_square[i] = x_square[j];
				x_square[j] = _;
			} while (false);
	}

	Kernel(int l, SvmNode[][] x_, SvmParam param) {
		this.kernelType = param.kernelType;
		this.degree = param.degree;
		this.gamma = param.gamma;
		this.coef0 = param.coef0;

		x = (SvmNode[][]) x_.clone();

		if (kernelType == SvmParam.RBF) {
			x_square = new double[l];
			for (int i = 0; i < l; i++)
				x_square[i] = dot(x[i], x[i]);
		} else
			x_square = null;
	}

	double kernel_function(int i, int j) {
		switch (kernelType) {
		case SvmParam.LINEAR:
			return dot(x[i], x[j]);
		case SvmParam.POLY:
			return Math.pow(gamma * dot(x[i], x[j]) + coef0, degree);
		case SvmParam.RBF:
			return Math.exp(-gamma
					* (x_square[i] + x_square[j] - 2 * dot(x[i], x[j])));
		case SvmParam.SIGMOID:
			return Math.tanh(gamma * dot(x[i], x[j]) + coef0);
		default:
			return 0; // java
		}
	}

	/**
	 * dot product of two points
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	static double dot(SvmNode[] x, SvmNode[] y) {
		double sum = 0;
		int xlen = x.length;
		int ylen = y.length;
		int i = 0;
		int j = 0;
		while (i < xlen && j < ylen) {
			if (x[i].index == y[j].index)
				sum += x[i++].value * y[j++].value;
			else {
				if (x[i].index > y[j].index)
					++j;
				else
					++i;
			}
		}
		return sum;
	}

	public static double k_function(SvmNode[] x, SvmNode[] y, SvmParam param) {
		switch (param.kernelType) {
		case SvmParam.LINEAR:
			return dot(x, y);
		case SvmParam.POLY:
			return Math
					.pow(param.gamma * dot(x, y) + param.coef0, param.degree);
		case SvmParam.RBF: {
			double sum = 0;
			int xlen = x.length;
			int ylen = y.length;
			int i = 0;
			int j = 0;
			while (i < xlen && j < ylen) {
				if (x[i].index == y[j].index) {
					double d = x[i++].value - y[j++].value;
					sum += d * d;
				} else if (x[i].index > y[j].index) {
					sum += y[j].value * y[j].value;
					++j;
				} else {
					sum += x[i].value * x[i].value;
					++i;
				}
			}

			while (i < xlen) {
				sum += x[i].value * x[i].value;
				++i;
			}

			while (j < ylen) {
				sum += y[j].value * y[j].value;
				++j;
			}

			return Math.exp(-param.gamma * sum);
		}
		case SvmParam.SIGMOID:
			return Math.tanh(param.gamma * dot(x, y) + param.coef0);
		default:
			return 0; // java
		}
	}
}
