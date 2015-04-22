package aaa.tools;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class DeleteDatasets {

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
		
		String input_datapath = config.getString("input_datapath");
		
		File dir1 = new File(input_datapath);
		for(File file: dir1.listFiles()) {
			file.delete();
		}
		System.out.println("clear finished");
	}
}
