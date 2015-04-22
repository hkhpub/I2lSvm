package i2l.svm.objects;

/**
 * an Accuracy holder class, with corresponding c, gamma value pair
 * @author hkh
 *
 */
public class SvmOPAcc extends Accuracy {
	
	public double C = 0.0f;
	public double gamma = 0.0f;
	
	public SvmOPAcc() {
		
	}
	
	public SvmOPAcc(double accuracy, double C, double gamma) {
		this.value = accuracy;
		this.C = C;
		this.gamma = gamma;
	}
}
