package i2l.svm.task;

public class SvmOptimalParamTrain extends SvmTrain {

	private double C = 0;
	private double gamma = 0;
	
	public void setParamC(double C) {
		this.C = C;
	}
	
	public void setParamGamma(double gamma) {
		this.gamma = gamma;
	}
	
	@Override
	protected void readConfiguration() {
		super.readConfiguration();
		
		if (this.C > 0) {
			param.C = C;
		}
		if (this.gamma > 0) {
			param.gamma = gamma;
		}
	}
}
