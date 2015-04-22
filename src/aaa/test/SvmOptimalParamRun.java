package aaa.test;

import i2l.svm.core.io.ParamGenerator;
import i2l.svm.core.model.SvmModel;
import i2l.svm.core.model.SvmParam;
import i2l.svm.objects.SvmOPAcc;
import i2l.svm.task.SvmOptimalParamTrain;
import i2l.svm.task.SvmPredict;
import i2l.svm.utils.LogUtils;
import i2l.svm.utils.Utils;

import java.util.Date;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * 일반SVM 과 다른 점은 
 * validation set이 개입되고 validation 과정의 가장 높은 accuracy를 가진 (C, gamma) pair를 찾고
 * 최적의 (C, gamma) pair로 test set의 accuracy를 측정한다.
 * 
 * Type: C_SVC (0)
 * @author hkh
 *
 */
public class SvmOptimalParamRun {
	
	public static void main(String args[]) {
		// read configuration
		Configuration config = null;
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = 
				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
						.setFileName("conf/svm_configuration.properties"));
		
		try {
			config = builder.getConfiguration();
		} catch (ConfigurationException cex) {
		}
		if (config == null) {
			System.err.println("Exception with configurations!");
		}
		
		int setSize = config.getInt("dataset_size");
		
		String timeTag = Utils.getTimeTag();
		String input_datapath = config.getString("input_datapath");
		String validation_file = input_datapath+config.getString("input_validation_file");
		String test_file = input_datapath+config.getString("input_test_file");
		
		String accuracy_file = config.getString("output_accuracy_path")+timeTag+".txt";
		String optimal_param_file = config.getString("output_optimal_param_path")+timeTag+".txt";
		String optimal_accuracy_file = config.getString("output_optimal_accuracy_path")+timeTag+".txt";
				
		SvmOptimalParamTrain train = new SvmOptimalParamTrain();
		train.setSvmType(SvmParam.C_SVC);
		SvmPredict predict = new SvmPredict();
		
		int OVERLAP = setSize;
		SvmOPAcc[] topAccs = new SvmOPAcc[OVERLAP];
		SvmModel[] topModels = new SvmModel[OVERLAP];
		for (int i=0; i<OVERLAP; i++) {
			topAccs[i] = new SvmOPAcc();
		}
		Double[] c_array = ParamGenerator.getFixed_C_values(config);
		Double[] gamma_array = ParamGenerator.getFixed_gamma_values(config);
		
		for (int k=0; k<OVERLAP; k++) {
			int file_index = k+1;
			
			for (int i=0; i<c_array.length; i++) {
				for (int j=0; j<gamma_array.length; j++) {

					train.setDatasetIndex(k+1);
					train.setParamC(c_array[i]);
					train.setParamGamma(gamma_array[j]);
					
					SvmModel model = train.run();
					
					predict.setDatasetIndex(k+1);
					predict.setTestFile(validation_file);
					double accuracy = predict.run(model);
					
					SvmOPAcc acc = new SvmOPAcc(accuracy, c_array[i], gamma_array[j]);
					// replace top accuracy
					if (acc.value > topAccs[k].value) {
						topAccs[k] = acc;
						topModels[k] = model;
					}
					LogUtils.logOPAccuracy(accuracy_file+"_"+file_index+".txt", acc);
					System.out.println("["+new Date().toString()+"] Accuracy: "+acc.value+" | C: "+acc.C+ " | gamma: "+acc.gamma);
				}
			}
		} // end of one dataset test
		LogUtils.logTopOPAccuracy(optimal_param_file, topAccs);
		
		// test with optimal parameter pairs
		double[] optimalAccs = new double[OVERLAP];
		for (int k=0; k<OVERLAP; k++) {
			predict = new SvmPredict();
			predict.setDatasetIndex(k+1);
			predict.setTestFile(test_file);
			double accuracy = predict.run(topModels[k]);
			optimalAccs[k] = accuracy;
		}
		LogUtils.logOptimalAccuracy(optimal_accuracy_file, optimalAccs);
	}
}
