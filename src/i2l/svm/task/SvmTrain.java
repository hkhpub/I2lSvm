package i2l.svm.task;

import i2l.svm.core.Svm;
import i2l.svm.core.SvmCheck;
import i2l.svm.core.io.ModelConnector;
import i2l.svm.core.io.ProblemReader;
import i2l.svm.core.model.SvmModel;
import i2l.svm.core.model.SvmParam;
import i2l.svm.core.model.SvmProblem;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class SvmTrain {

	protected SvmParam param;
	private SvmProblem prob;
	private SvmModel model;
	private String input_file_name;
	private String model_file_name;
	private boolean crossValidation = false;
	private int nFold = 1;
	String errorMsg = null;
	
	// Time-series overlapping dataset index
	private int datasetIndex = 0;
	
	// ascending rate of C
	private double a_rate = 0.0f;
	
	private int svmType = -1;
	
	/**
	 * Time-series dataset 테스트 시 사용
	 * @param index overlapping dataset index
	 */
	public void setDatasetIndex(int index) {
		this.datasetIndex = index;
	}
	
	/**
	 * set ascending rate of C, only for ASVM
	 * @param a_rate
	 */
	public void setAscendingRate(double a_rate) {
		this.a_rate = a_rate;
	}
	
	public void setSvmType(int svmType) {
		this.svmType = svmType;
	}
	
	public SvmModel run() {
		readConfiguration();
		prob = ProblemReader.readProblem(input_file_name);
		// set default gamma value
		if (param.gamma == 0 && prob.max_feat_dim > 0) {
			param.gamma = 1.0 / prob.max_feat_dim;
		}
		
		errorMsg = SvmCheck.SvmCheckParameter(prob, param);

		if (errorMsg != null) {
			System.err.println("ERROR: " + errorMsg);
			System.exit(1);
		}

		if (crossValidation) {
			doCrossValidation();
		} else {
			model = Svm.svm_train(prob, param);
			if (model_file_name != null) {
				ModelConnector.writeModel(model_file_name, model);
			}
		}
		return model;
	}

	public static void main(String args[]) {
		SvmTrain train = new SvmTrain();
		train.run();
	}

	/**
	 * svm 관련 설정값 읽음
	 */
	protected void readConfiguration() {
		// read configuration
		Configuration config = null;
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
				PropertiesConfiguration.class).configure(params.properties()
				.setFileName("conf/svm_configuration.properties"));

		try {
			config = builder.getConfiguration();
		} catch (ConfigurationException cex) {
		}
		if (config == null) {
			System.err.println("Exception with configurations!");
		}

		param = new SvmParam();

		param.svmType = config.getInt("svm_type");
		if (this.svmType >= 0) {
			param.svmType = this.svmType;
		}
		param.kernelType = config.getInt("kernel_type");
		param.degree = config.getInt("degree");
		param.gamma = config.getFloat("gamma");
		// param.coef0 = config.getFloat("coef0");
		param.cacheSize = config.getFloat("cache_size");
		param.C = config.getFloat("C");
		param.tolerance = config.getFloat("tolerance");
		param.shrinking = config.getInt("shrinking");
		nFold = config.getInt("cross_validation");
		if (nFold >= 2) {
			crossValidation = true;
		}
		if (param.svmType == SvmParam.C_ASVC) {
			param.a_rate = this.a_rate;
		}

		String input_datapath = config.getString("input_datapath");
		input_file_name = input_datapath+config.getString("input_train_file");
		if (datasetIndex > 0) {
			input_file_name += "_"+datasetIndex;
		}
		model_file_name = config.getString("model_file");
	}

	private void doCrossValidation() {
		int i;
		int total_correct = 0;
		double[] target = new double[prob.l];

		Svm.svm_cross_validation(prob, param, nFold, target);
		for (i = 0; i < prob.l; i++) {
			if (target[i] == prob.y[i]) {
				++total_correct;
			}
		}
		System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct
				/ prob.l + "%\n");
	}
}
