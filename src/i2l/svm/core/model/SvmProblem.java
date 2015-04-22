package i2l.svm.core.model;

import java.io.Serializable;
import java.util.Vector;

/**
 * Svm 데이터 객체다.
 * @author hkh
 *
 */
public class SvmProblem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 최대 feature dimension 수 
	 */
	public int max_feat_dim;
	
	/**
	 * 총 data examples 수
	 */
	public int l;
	
	/**
	 * 이건 라벨 클래스다.
	 */
	public double[] y;
	
	/**
	 * 모든 데이터다. [n_data][n_feature]로 구성되었다. 헷갈리는가? 테스트 해보자.
	 */
	public SvmNode[][] x;
	
	/**
	 * SvmProblem의 일부분으로 구성된 새로운 SvmProblem 객체를 생성해서 리턴한다.
	 * @param dataGroup - SvmProblem 객체의 SvmNode Data index
	 * @return
	 */
	public SvmProblem getProblemsAtRange(Vector<Integer> dataGroup) {
		SvmProblem subProb = new SvmProblem();
		subProb.max_feat_dim = max_feat_dim;
		subProb.l = dataGroup.size();
		subProb.x = new SvmNode[subProb.l][max_feat_dim];
		subProb.y = new double[subProb.l];
		
		for (int i=0; i<dataGroup.size(); i++) {
			int index = dataGroup.get(i);
			subProb.x[i] = x[index];
			subProb.y[i] = y[index];
		}
		return subProb;
	}
	
	public void clear() {
		x = null;
		y = null;
		l = 0;
		max_feat_dim = 0;
	}
}
