package i2l.svm.objects;

/**
 * an Accuracy holder class, with ascending rate of C
 * @author hkh
 *
 */
public class AsvmAcc extends Accuracy {
	
	public double a_rate = 0.0f;
	public double C = 0.0f;
	public double gamma = 0.0f;
	
	public AsvmAcc() {
		
	}
	
	public AsvmAcc(double accuracy, double a_rate) {
		this.value = accuracy;
		this.a_rate = a_rate;
	}
	
	public AsvmAcc(double accuracy, double a_rate, double C, double gamma) {
		this.value = accuracy;
		this.a_rate = a_rate;
		this.C = C;
		this.gamma = gamma;
	}
}
