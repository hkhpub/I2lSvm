package aaa.test;

import i2l.svm.core.io.ParamGenerator;
import i2l.svm.core.model.SvmModel;
import i2l.svm.objects.AsvmAcc;
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
 * Asvm 으로 일반 svm과 다른 점은
 * data example에 따라 C값을 조절하는 매커니즘을 도입, C 값의 변화율은 Ascending Rate라고 하는 파라미터 (a_rate)로 반영한다.
 * 
 * 로직설명: AsvmOptimalParamRun의 로직과 비슷하지만 유일한 구별점은 시작부터 최적의 (C, gamma, a_rate) pair를 구한다. (for loop 3개 중첩) O(n3)
 * 			1) validation set으로 최적의 (C, gamma, a_rate) pair를 구한다.
 * 			2) (C, gamma, a_rate) pair로 test set accuracy를 측정한다.
 * 
 * Type: C_ASVC (1) Adaptive SVM
 * @author hkh
 *
 */
public class AsvmOptimalAscRateRun {
	
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
//		train.setSvmType(SvmParam.C_SVC);
		SvmPredict predict = new SvmPredict();
		
		int OVERLAP = setSize;
		AsvmAcc[] topAccs = new AsvmAcc[OVERLAP];
		SvmModel[] topModels = new SvmModel[OVERLAP];
		for (int i=0; i<OVERLAP; i++) {
			topAccs[i] = new AsvmAcc();
		}
		Double[] a_rates = ParamGenerator.getFixed_a_values(config);
		Double[] c_array = ParamGenerator.getFixed_C_values(config);
		Double[] gamma_array = ParamGenerator.getFixed_gamma_values(config);
		
		for (int k=0; k<OVERLAP; k++) {
			int file_index = k+1;

			for (int m=0; m<a_rates.length; m++) {
				for (int i=0; i<c_array.length; i++) {
					for (int j=0; j<gamma_array.length; j++) {
						
						train.setDatasetIndex(k+1);
						train.setAscendingRate(a_rates[m]);
						train.setParamC(c_array[i]);
						train.setParamGamma(gamma_array[j]);
						
						SvmModel model = train.run();
						
						predict.setDatasetIndex(k+1);
						predict.setTestFile(validation_file);
						double accuracy = predict.run(model);
						
						AsvmAcc acc = new AsvmAcc(accuracy, a_rates[m], c_array[i], gamma_array[j]);
						// replace top accuracy
						if (acc.value > topAccs[k].value) {
							topAccs[k] = acc;
							topModels[k] = model;
						}
						LogUtils.logAsvmAccuracy2(accuracy_file+"_"+file_index+".txt", acc);
						System.out.println("["+new Date().toString()+"] Accuracy: "+acc.value+" | C: "+acc.C+ " | gamma: "+acc.gamma);
					}
				}
			}
		} // end of one dataset test
		LogUtils.logTopAsvmAccuracy2(optimal_param_file, topAccs);
		
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
