package i2l.svm.core.io;

import i2l.svm.core.model.SvmParam;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * configuration 파일에 설정된 파라미터를 읽는다.
 * @author hkh
 *
 */
public class ParamReader {

	public static SvmParam readParam() {
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

		SvmParam param = new SvmParam();
		
		param.svmType = config.getInt("svm_type");
		param.kernelType = config.getInt("kernel_type");
		param.degree = config.getInt("degree");
		param.gamma = config.getFloat("gamma");
		// param.coef0 = config.getFloat("coef0");
		param.cacheSize = config.getFloat("cache_size");
		param.C = config.getFloat("C");
		param.tolerance = config.getFloat("tolerance");
		param.shrinking = config.getInt("shrinking");
		
		return param;
	}
}
