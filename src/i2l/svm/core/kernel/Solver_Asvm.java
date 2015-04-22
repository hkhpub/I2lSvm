package i2l.svm.core.kernel;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

import i2l.svm.core.Solver;
import i2l.svm.utils.LogUtils;

public class Solver_Asvm extends Solver {

	private double a = 0.0;
	private double[] Cp_array = null;
	private double[] Cn_array = null;
	
	public Solver_Asvm(double a_rate) {
		super();
		this.a = a_rate;
	}
	
	@Override
	protected double get_C(int i) {
		if (Cp_array == null && Cn_array == null) {
			Cp_array = new double[l];
			Cn_array = new double[l];
			
			for (int j=0; j<l; j++) {
				Cp_array[j] = (double) (Cp * (2 / (1+Math.exp(a-2*a*(j+1)/l))));
				Cn_array[j] = (double) (Cn * (2 / (1+Math.exp(a-2*a*(j+1)/l))));
//				LogUtils.logString("C: "+Cp_array[j]);
			}
		}
		
		double ret = 0.0;
		int reverseIndex = (l-1)-i;
		if (y[reverseIndex] > 0) {
			ret = Cp_array[reverseIndex];
		} else {
			ret = Cn_array[reverseIndex];
		}
		if (ret == 0) {
			ret+=1.0E-4;
		}
		return ret;
	}
}
