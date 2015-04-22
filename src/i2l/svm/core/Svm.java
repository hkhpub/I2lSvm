package i2l.svm.core;

import i2l.svm.core.kernel.Kernel;
import i2l.svm.core.kernel.SVC_Q;
import i2l.svm.core.kernel.Solver_Asvm;
import i2l.svm.core.model.SvmModel;
import i2l.svm.core.model.SvmNode;
import i2l.svm.core.model.SvmParam;
import i2l.svm.core.model.SvmProblem;
import i2l.svm.utils.Utils;

import java.util.Random;

/**
 * Construct and solve various formulations
 * 
 * @author hkh
 *
 */
public class Svm {
	public static final int LIBSVM_VERSION = 319;
	public static final Random rand = new Random();

	/**
	 * 일반 svm classification
	 * @param prob
	 * @param param
	 * @param alpha
	 * @param si
	 * @param Cp
	 * @param Cn
	 */
	private static void solve_c_svc(SvmProblem prob, SvmParam param,
			double[] alpha, Solver.SolutionInfo si, double Cp, double Cn) {
		int l = prob.l;
		double[] minus_ones = new double[l];
		byte[] y = new byte[l];

		int i;

		for (i = 0; i < l; i++) {
			alpha[i] = 0;
			minus_ones[i] = -1;
			if (prob.y[i] > 0)
				y[i] = +1;
			else
				y[i] = -1;
		}

		Solver s = new Solver();
		s.Solve(l, new SVC_Q(prob, param, y), minus_ones, y, alpha, Cp, Cn,
				param.tolerance, si, param.shrinking);

		for (i = 0; i < l; i++) {
			alpha[i] *= y[i];
		}
	}
	
	/**
	 * Adaptive Svm with ascending C
	 * @param prob
	 * @param param
	 * @param alpha
	 * @param si
	 * @param Cp
	 * @param Cn
	 */
	private static void solve_c_asvc(SvmProblem prob, SvmParam param,
			double[] alpha, Solver.SolutionInfo si, double Cp, double Cn) {
		int l = prob.l;
		double[] minus_ones = new double[l];
		byte[] y = new byte[l];

		int i;

		for (i = 0; i < l; i++) {
			alpha[i] = 0;
			minus_ones[i] = -1;
			if (prob.y[i] > 0)
				y[i] = +1;
			else
				y[i] = -1;
		}

		Solver_Asvm s = new Solver_Asvm(param.a_rate);
		s.Solve(l, new SVC_Q(prob, param, y), minus_ones, y, alpha, Cp, Cn,
				param.tolerance, si, param.shrinking);

		for (i = 0; i < l; i++) {
			alpha[i] *= y[i];
		}
	}

	/**
	 * decision_function
	 * 
	 * @author hkh
	 *
	 */
	public static class decision_function {
		double[] alpha;
		double rho;
	};

	private static decision_function svm_train_one(SvmProblem prob,
			SvmParam param, double Cp, double Cn) {
		double[] alpha = new double[prob.l];
		Solver.SolutionInfo si = new Solver.SolutionInfo();
		switch (param.svmType) {
		case SvmParam.C_SVC:
			solve_c_svc(prob, param, alpha, si, Cp, Cn);
			break;
		case SvmParam.C_ASVC:
			solve_c_asvc(prob, param, alpha, si, Cp, Cn);
			break;
		}

		// output SVs
		int nSV = 0;
		int nBSV = 0;
		for (int i = 0; i < prob.l; i++) {
			if (Math.abs(alpha[i]) > 0) {
				++nSV;
				if (prob.y[i] > 0) {
					if (Math.abs(alpha[i]) >= si.upper_bound_p)
						++nBSV;
				} else {
					if (Math.abs(alpha[i]) >= si.upper_bound_n)
						++nBSV;
				}
			}
		}

		Utils.info("nSV = " + nSV + ", nBSV = " + nBSV + "\n");

		decision_function f = new decision_function();
		f.alpha = alpha;
		f.rho = si.rho;
		return f;
	}

	/**
	 * label: label name, start: begin of each class, count: #data of classes,
	 * perm: indices to the original data perm, length l, must be allocated
	 * before calling this subroutine
	 * 
	 * @param prob
	 * @param nr_class_ret
	 * @param label_ret
	 * @param start_ret
	 * @param count_ret
	 * @param perm
	 */
	private static void svm_group_classes(SvmProblem prob, int[] nr_class_ret,
			int[][] label_ret, int[][] start_ret, int[][] count_ret, int[] perm) {
		int l = prob.l;
		int max_nr_class = 16;
		int nr_class = 0;
		int[] label = new int[max_nr_class];
		int[] count = new int[max_nr_class];
		int[] data_label = new int[l];
		int i;

		for (i = 0; i < l; i++) {
			int this_label = (int) (prob.y[i]);
			int j;
			for (j = 0; j < nr_class; j++) {
				if (this_label == label[j]) {
					++count[j];
					break;
				}
			}
			data_label[i] = j;
			if (j == nr_class) {
				if (nr_class == max_nr_class) {
					max_nr_class *= 2;
					int[] new_data = new int[max_nr_class];
					System.arraycopy(label, 0, new_data, 0, label.length);
					label = new_data;
					new_data = new int[max_nr_class];
					System.arraycopy(count, 0, new_data, 0, count.length);
					count = new_data;
				}
				label[nr_class] = this_label;
				count[nr_class] = 1;
				++nr_class;
			}
		}

		//
		// Labels are ordered by their first occurrence in the training set.
		// However, for two-class sets with -1/+1 labels and -1 appears first,
		// we swap labels to ensure that internally the binary SVM has positive
		// data corresponding to the +1 instances.
		//
		if (nr_class == 2 && label[0] == -1 && label[1] == +1) {
			do {
				int _ = label[0];
				label[0] = label[1];
				label[1] = _;
			} while (false);
			do {
				int _ = count[0];
				count[0] = count[1];
				count[1] = _;
			} while (false);
			for (i = 0; i < l; i++) {
				if (data_label[i] == 0)
					data_label[i] = 1;
				else
					data_label[i] = 0;
			}
		}

		int[] start = new int[nr_class];
		start[0] = 0;
		for (i = 1; i < nr_class; i++)
			start[i] = start[i - 1] + count[i - 1];
		for (i = 0; i < l; i++) {
			perm[start[data_label[i]]] = i;
			++start[data_label[i]];
		}
		start[0] = 0;
		for (i = 1; i < nr_class; i++)
			start[i] = start[i - 1] + count[i - 1];

		nr_class_ret[0] = nr_class;
		label_ret[0] = label;
		start_ret[0] = start;
		count_ret[0] = count;
	}

	/**
	 * Interface function
	 * 
	 * @param prob
	 * @param param
	 * @return
	 */
	public static SvmModel svm_train(SvmProblem prob, SvmParam param) {
		SvmModel model = new SvmModel();
		model.param = param;

		// classification
		int l = prob.l;
		int[] tmp_nr_class = new int[1];
		int[][] tmp_label = new int[1][];
		int[][] tmp_start = new int[1][];
		int[][] tmp_count = new int[1][];
		int[] perm = new int[l];

		// group training data of the same class
		svm_group_classes(prob, tmp_nr_class, tmp_label, tmp_start, tmp_count,
				perm);
		int nr_class = tmp_nr_class[0];
		int[] label = tmp_label[0];
		int[] start = tmp_start[0];
		int[] count = tmp_count[0];

		if (nr_class == 1)
			Utils.info("WARNING: training data in only one class. See README for details.\n");

		SvmNode[][] x = new SvmNode[l][];
		int i;
		for (i = 0; i < l; i++) {
			x[i] = prob.x[perm[i]];
		}

		// calculate weighted C
		double[] weighted_C = new double[nr_class];
		for (i = 0; i < nr_class; i++)
			weighted_C[i] = param.C;
		for (i = 0; i < param.nWeight; i++) {
			int j;
			for (j = 0; j < nr_class; j++) {
				if (param.weightLabel[i] == label[j]) {
					break;
				}
			}
			if (j == nr_class) {
				System.err.print("WARNING: class label " + param.weightLabel[i]
						+ " specified in weight is not found\n");
			} else {
				weighted_C[j] *= param.weight[i];
			}
		}

		// train k*(k-1)/2 models
		boolean[] nonzero = new boolean[l];
		for (i = 0; i < l; i++) {
			nonzero[i] = false;
		}
		decision_function[] f = new decision_function[nr_class * (nr_class - 1) / 2];

		int p = 0;
		for (i = 0; i < nr_class; i++) {
			for (int j = i + 1; j < nr_class; j++) {
				SvmProblem sub_prob = new SvmProblem();
				int si = start[i], sj = start[j];
				int ci = count[i], cj = count[j];
				sub_prob.l = ci + cj;
				sub_prob.x = new SvmNode[sub_prob.l][];
				sub_prob.y = new double[sub_prob.l];
				int k;
				for (k = 0; k < ci; k++) {
					sub_prob.x[k] = x[si + k];
					sub_prob.y[k] = +1;
				}
				for (k = 0; k < cj; k++) {
					sub_prob.x[ci + k] = x[sj + k];
					sub_prob.y[ci + k] = -1;
				}

				f[p] = svm_train_one(sub_prob, param, weighted_C[i],
						weighted_C[j]);
				for (k = 0; k < ci; k++) {
					if (!nonzero[si + k] && Math.abs(f[p].alpha[k]) > 0) {
						nonzero[si + k] = true;
					}
				}
				for (k = 0; k < cj; k++) {
					if (!nonzero[sj + k] && Math.abs(f[p].alpha[ci + k]) > 0) {
						nonzero[sj + k] = true;
					}
				}
				++p;
			}
		}

		// build output
		model.nClass = nr_class;

		model.label = new int[nr_class];
		for (i = 0; i < nr_class; i++)
			model.label[i] = label[i];

		model.rho = new double[nr_class * (nr_class - 1) / 2];
		for (i = 0; i < nr_class * (nr_class - 1) / 2; i++) {
			model.rho[i] = f[i].rho;
		}

		int nnz = 0;
		int[] nz_count = new int[nr_class];
		model.nSV = new int[nr_class];
		for (i = 0; i < nr_class; i++) {
			int nSV = 0;
			for (int j = 0; j < count[i]; j++)
				if (nonzero[start[i] + j]) {
					++nSV;
					++nnz;
				}
			model.nSV[i] = nSV;
			nz_count[i] = nSV;
		}

		Utils.info("Total nSV = " + nnz + "\n");

		model.l = nnz;
		model.SV = new SvmNode[nnz][];
		model.svIndices = new int[nnz];
		p = 0;
		for (i = 0; i < l; i++) {
			if (nonzero[i]) {
				model.SV[p] = x[i];
				model.svIndices[p++] = perm[i] + 1;
			}
		}

		int[] nz_start = new int[nr_class];
		nz_start[0] = 0;
		for (i = 1; i < nr_class; i++) {
			nz_start[i] = nz_start[i - 1] + nz_count[i - 1];
		}

		model.sv_coef = new double[nr_class - 1][];
		for (i = 0; i < nr_class - 1; i++) {
			model.sv_coef[i] = new double[nnz];
		}

		p = 0;
		for (i = 0; i < nr_class; i++) {
			for (int j = i + 1; j < nr_class; j++) {
				// classifier (i,j): coefficients with
				// i are in sv_coef[j-1][nz_start[i]...],
				// j are in sv_coef[i][nz_start[j]...]

				int si = start[i];
				int sj = start[j];
				int ci = count[i];
				int cj = count[j];

				int q = nz_start[i];
				int k;
				for (k = 0; k < ci; k++) {
					if (nonzero[si + k]) {
						model.sv_coef[j - 1][q++] = f[p].alpha[k];
					}
				}
				q = nz_start[j];
				for (k = 0; k < cj; k++)
					if (nonzero[sj + k])
						model.sv_coef[i][q++] = f[p].alpha[ci + k];
				++p;
			}
		}
		return model;
	}

	// Stratified cross validation
	public static void svm_cross_validation(SvmProblem prob, SvmParam param,
			int nr_fold, double[] target) {
		int i;
		int[] fold_start = new int[nr_fold + 1];
		int l = prob.l;
		int[] perm = new int[l];

		// stratified cv may not give leave-one-out rate
		// Each class to l folds -> some folds may have zero elements
		if (param.svmType == SvmParam.C_SVC && nr_fold < l) {
			int[] tmp_nr_class = new int[1];
			int[][] tmp_label = new int[1][];
			int[][] tmp_start = new int[1][];
			int[][] tmp_count = new int[1][];

			svm_group_classes(prob, tmp_nr_class, tmp_label, tmp_start,
					tmp_count, perm);

			int nr_class = tmp_nr_class[0];
			int[] start = tmp_start[0];
			int[] count = tmp_count[0];

			// random shuffle and then data grouped by fold using the array perm
			int[] fold_count = new int[nr_fold];
			int c;
			int[] index = new int[l];
			for (i = 0; i < l; i++) {
				index[i] = perm[i];
			}
			for (c = 0; c < nr_class; c++) {
				for (i = 0; i < count[c]; i++) {
					int j = i + rand.nextInt(count[c] - i);
					do {
						int _ = index[start[c] + j];
						index[start[c] + j] = index[start[c] + i];
						index[start[c] + i] = _;
					} while (false);
				}
			}
			for (i = 0; i < nr_fold; i++) {
				fold_count[i] = 0;
				for (c = 0; c < nr_class; c++) {
					fold_count[i] += (i + 1) * count[c] / nr_fold - i
							* count[c] / nr_fold;
				}
			}
			fold_start[0] = 0;
			for (i = 1; i <= nr_fold; i++) {
				fold_start[i] = fold_start[i - 1] + fold_count[i - 1];
			}
			for (c = 0; c < nr_class; c++) {
				for (i = 0; i < nr_fold; i++) {
					int begin = start[c] + i * count[c] / nr_fold;
					int end = start[c] + (i + 1) * count[c] / nr_fold;
					for (int j = begin; j < end; j++) {
						perm[fold_start[i]] = index[j];
						fold_start[i]++;
					}
				}
			}
			fold_start[0] = 0;
			for (i = 1; i <= nr_fold; i++) {
				fold_start[i] = fold_start[i - 1] + fold_count[i - 1];
			}
		} else {
			for (i = 0; i < l; i++) {
				perm[i] = i;
			}
			for (i = 0; i < l; i++) {
				int j = i + rand.nextInt(l - i);
				do {
					int _ = perm[i];
					perm[i] = perm[j];
					perm[j] = _;
				} while (false);
			}
			for (i = 0; i <= nr_fold; i++) {
				fold_start[i] = i * l / nr_fold;
			}
		}

		for (i = 0; i < nr_fold; i++) {
			int begin = fold_start[i];
			int end = fold_start[i + 1];
			int j, k;
			SvmProblem subprob = new SvmProblem();

			subprob.l = l - (end - begin);
			subprob.x = new SvmNode[subprob.l][];
			subprob.y = new double[subprob.l];

			k = 0;
			for (j = 0; j < begin; j++) {
				subprob.x[k] = prob.x[perm[j]];
				subprob.y[k] = prob.y[perm[j]];
				++k;
			}
			for (j = end; j < l; j++) {
				subprob.x[k] = prob.x[perm[j]];
				subprob.y[k] = prob.y[perm[j]];
				++k;
			}
			SvmModel submodel = svm_train(subprob, param);
			for (j = begin; j < end; j++) {
				target[perm[j]] = svm_predict(submodel, prob.x[perm[j]]);
			}
		}
	}

	public static int svm_get_svm_type(SvmModel model) {
		return model.param.svmType;
	}

	public static int svm_get_nr_class(SvmModel model) {
		return model.nClass;
	}

	public static void svm_get_labels(SvmModel model, int[] label) {
		if (model.label != null) {
			for (int i = 0; i < model.nClass; i++) {
				label[i] = model.label[i];
			}
		}
	}

	public static void svm_get_sv_indices(SvmModel model, int[] indices) {
		if (model.svIndices != null) {
			for (int i = 0; i < model.l; i++) {
				indices[i] = model.svIndices[i];
			}
		}
	}

	public static int svm_get_nr_sv(SvmModel model) {
		return model.l;
	}

	public static double svm_predict_values(SvmModel model, SvmNode[] x,
			double[] dec_values) {

		int nr_class = model.nClass;
		int l = model.l;

		double[] kvalue = new double[l];
		for (int i = 0; i < l; i++) {
			kvalue[i] = Kernel.k_function(x, model.SV[i], model.param);
		}

		int[] start = new int[nr_class];
		start[0] = 0;
		for (int i = 1; i < nr_class; i++) {
			start[i] = start[i - 1] + model.nSV[i - 1];
		}

		int[] vote = new int[nr_class];
		for (int i = 0; i < nr_class; i++) {
			vote[i] = 0;
		}

		int p = 0;
		for (int i = 0; i < nr_class; i++) {
			for (int j = i + 1; j < nr_class; j++) {
				double sum = 0;
				int si = start[i];
				int sj = start[j];
				int ci = model.nSV[i];
				int cj = model.nSV[j];

				double[] coef1 = model.sv_coef[j - 1];
				double[] coef2 = model.sv_coef[i];
				for (int k = 0; k < ci; k++) {
					sum += coef1[si + k] * kvalue[si + k];
				}
				for (int k = 0; k < cj; k++) {
					sum += coef2[sj + k] * kvalue[sj + k];
				}
				sum -= model.rho[p];
				dec_values[p] = sum;

				if (dec_values[p] > 0)
					++vote[i];
				else
					++vote[j];
				p++;
			}
		}

		int vote_max_idx = 0;
		for (int i = 1; i < nr_class; i++) {
			if (vote[i] > vote[vote_max_idx]) {
				vote_max_idx = i;
			}
		}

		return model.label[vote_max_idx];
	}

	public static double svm_predict(SvmModel model, SvmNode[] x) {
		int nr_class = model.nClass;
		double[] dec_values;
		dec_values = new double[nr_class * (nr_class - 1) / 2];
		double pred_result = svm_predict_values(model, x, dec_values);
		return pred_result;
	}
}
