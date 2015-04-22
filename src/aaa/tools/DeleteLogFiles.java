package aaa.tools;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class DeleteLogFiles {

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
		
		String accuracy_path = config.getString("output_accuracy_path");
		String optimal_param_path = config.getString("output_optimal_param_path");
		String optimal_accuracy_path = config.getString("output_optimal_accuracy_path");
		
		File dir1 = new File(accuracy_path);
		for(File file: dir1.listFiles()) {
			file.delete();
		}
		
		File dir2 = new File(optimal_param_path);
		for(File file: dir2.listFiles()) {
			file.delete();
		}
		
		File dir3 = new File(optimal_accuracy_path);
		for(File file: dir3.listFiles()) {
			file.delete();
		}
		
		File dir4 = new File("outputs/log/");
		for(File file: dir4.listFiles()) {
			file.delete();
		}
		
		System.out.println("clear finished");
	}
}
