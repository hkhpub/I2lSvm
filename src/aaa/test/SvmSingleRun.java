package aaa.test;

import i2l.svm.core.model.SvmModel;
import i2l.svm.task.SvmPredict;
import i2l.svm.task.SvmTrain;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * 일반 SVM으로 단 한번의 Train & Test를 수행한다.
 * accuracy를 터미널에 바로 출력한다.
 * 
 * Type: C_SVC (0)
 * @author hkh
 *
 */
public class SvmSingleRun {

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
		
		
		// start train and test
		SvmTrain train = new SvmTrain();
		SvmPredict predict = new SvmPredict();
		
		int setSize = config.getInt("dataset_size");
				
		int k = setSize;
		double[] accs = new double[k];
		for (int i=0; i<k; i++) {
			train.setDatasetIndex(i+1);
			SvmModel model = train.run();
			
			predict.setDatasetIndex(i+1);
			accs[i] = predict.run(model);
		}
		
		for (int i=0; i<accs.length; i++) {
			System.out.println("[Data set "+(i+1)+"] Accuracy: "+String.format("%.4f", accs[i])+"\n");
		}
	}
}
