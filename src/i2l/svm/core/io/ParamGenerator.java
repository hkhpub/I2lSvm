package i2l.svm.core.io;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ParamGenerator {

	/**
	 * a is the parameter to control the ascending rate of C <br />
	 * C[i] is called the ascending regularization constant.
	 * 
	 * @param config
	 * @return
	 */
	public static Double[] getFixed_a_values(Configuration config) {
		int aCount = config.getInt("a_count");
		Double[] aValues = new Double[aCount];
		for (int i=0; i<aCount; i++) {
			aValues[i] = Math.pow(10, i-aCount/2);
		}
		return aValues;
	}
	
	public static Double[] getFixed_gamma_values (Configuration config) {
		String[] gamma_values = config.getString("gamma_array").split(",");
		return StringToDoubleArray(gamma_values);
	}
	
	public static Double[] getFixed_C_values() {
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
		return getFixed_C_values(config);
	}

	public static Double[] getFixed_C_values (Configuration config) {
		String[] c_values = config.getString("C_array").split(",");
		return StringToDoubleArray(c_values);
	}
	
	private static Double[] StringToDoubleArray(String[] array) {
		if (array == null)
			return null;
		
		Double[] doubleArr = new Double[array.length];
		for (int i=0; i<array.length; i++) {
			doubleArr[i] = Double.parseDouble(array[i].trim());
		}
		return doubleArr;
	}
}
