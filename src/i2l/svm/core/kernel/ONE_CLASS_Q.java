package i2l.svm.core.kernel;

import i2l.svm.core.Cache;
import i2l.svm.core.model.SvmParam;
import i2l.svm.core.model.SvmProblem;

class ONE_CLASS_Q extends Kernel {
	private final Cache cache;
	private final double[] QD;

	ONE_CLASS_Q(SvmProblem prob, SvmParam param) {
		super(prob.l, prob.x, param);
		cache = new Cache(prob.l, (long) (param.cacheSize * (1 << 20)));
		QD = new double[prob.l];
		for (int i = 0; i < prob.l; i++)
			QD[i] = kernel_function(i, i);
	}

	public float[] get_Q(int i, int len) {
		float[][] data = new float[1][];
		int start, j;
		if ((start = cache.get_data(i, data, len)) < len) {
			for (j = start; j < len; j++)
				data[0][j] = (float) kernel_function(i, j);
		}
		return data[0];
	}

	public double[] get_QD() {
		return QD;
	}

	public void swap_index(int i, int j) {
		cache.swap_index(i, j);
		super.swap_index(i, j);
		do {
			double _ = QD[i];
			QD[i] = QD[j];
			QD[j] = _;
		} while (false);
	}
}