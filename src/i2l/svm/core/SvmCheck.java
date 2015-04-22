package i2l.svm.core;

import i2l.svm.core.model.SvmParam;
import i2l.svm.core.model.SvmProblem;

public class SvmCheck {

	/**
	 * SvmProblem, SvmParam 에 설정된 값들이 정상인지 체크후 에러메시지 생성
	 * 
	 * @param prob
	 * @param param
	 * @return
	 */
	public static String SvmCheckParameter(SvmProblem prob, SvmParam param) {
		// svm_type
		int svmType = param.svmType;
		if (svmType != SvmParam.C_SVC && svmType != SvmParam.C_ASVC) {
			return "unknown svm type";
		}

		// kernel type, degree, gamma
		int kernelType = param.kernelType;
		if (kernelType != SvmParam.LINEAR && kernelType != SvmParam.POLY
				&& kernelType != SvmParam.RBF && kernelType != SvmParam.SIGMOID) {
			return "unknown kernel type";
		}

		if (param.gamma < 0) {
			return "gamma < 0";
		}

		if (param.degree < 0) {
			return "degree of polynomial kernel < 0";
		}

		// cache_size, eps, C, nu, p, shrinking
		if (param.cacheSize <= 0) {
			return "cache_size <= 0";
		}

		if (param.tolerance <= 0) {
			return "eps <= 0";
		}

		if (param.C <= 0) {
			return "C <= 0";
		}

		if (param.shrinking != 0 && param.shrinking != 1) {
			return "shrinking != 0 and shrinking != 1";
		}

		return null;
	}
}
