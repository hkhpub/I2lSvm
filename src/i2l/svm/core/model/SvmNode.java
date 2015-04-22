package i2l.svm.core.model;

import java.io.Serializable;

/**
 * 한개의 Example을 나타내는 class, n개 features, index는 feature index
 * input data 파일에 명시된 index (1부터 시작)
 * @author hkh
 *
 */
public class SvmNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int index;
	public double value;
}
