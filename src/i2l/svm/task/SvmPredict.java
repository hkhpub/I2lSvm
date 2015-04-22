package i2l.svm.task;

import i2l.svm.core.Svm;
import i2l.svm.core.io.ModelConnector;
import i2l.svm.core.model.SvmModel;
import i2l.svm.core.model.SvmNode;
import i2l.svm.core.model.SvmProblem;
import i2l.svm.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class SvmPredict {
	
	private String model_file_name = null;
	private String test_file_name = null;
	private String output_file_name = null;
	
	// Time-series overlapping dataset index
	private int datasetIndex = 0;
	
	/**
	 * Time-series dataset 테스트 시 사용
	 * @param index overlapping dataset index
	 */
	public void setDatasetIndex(int index) {
		this.datasetIndex = index;
	}
		
	public void run() {
		readConfiguration();
		predictProblem();
	}
	
	public double run(SvmModel model) {
		readConfiguration();
		return predict(model);
	}
	
	public void setTestFile(String test_file_name) {
		this.test_file_name = test_file_name;
	}
	
	/**
	 * svm 관련 설정값 읽음
	 */
	private void readConfiguration() {
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
		
		String input_datapath = config.getString("input_datapath");
		model_file_name = config.getString("model_file");
		test_file_name = input_datapath+config.getString("input_test_file");
		output_file_name = config.getString("output_file");
	}
	
	private void predictProblem() {
		SvmModel model = ModelConnector.loadModel(model_file_name);
		if (model == null) {
			System.err.print("can't open model file.. \n");
			System.exit(1);
		}
		predict(model);
	}
	
	private double predict(SvmModel model) {
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;

		int correct = 0;
		int total = 0;
		double error = 0;
		double accuracy = 0.0;
		
		try {
			fr = new FileReader(test_file_name+"_"+datasetIndex);
			br = new BufferedReader(fr);
			if (output_file_name != null) {
				fw = new FileWriter(output_file_name);
				bw = new BufferedWriter(fw);
			}
			String line = null;
		
			while ((line=br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

				double target = Utils.atof(st.nextToken());
				int m = st.countTokens()/2;
				SvmNode[] x = new SvmNode[m];
				for(int j=0;j<m;j++) {
					x[j] = new SvmNode();
					x[j].index = Utils.atoi(st.nextToken());
					x[j].value = Utils.atof(st.nextToken());
				}
				
				double v = Svm.svm_predict(model,x);
				if (bw != null) {
					bw.write(v+"\n");
					bw.flush();
				}
				if(v == target) {
					++correct;
				}
				error += (v-target)*(v-target);
				++total;
				Utils.info("Accuracy = "+(double)correct/total*100+
						 "% ("+correct+"/"+total+") (classification)\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fr != null) try { fr.close(); } catch (Exception ex) {};
			if (br != null) try { br.close(); } catch (Exception ex) {};
			if (fw != null) try { fw.close(); } catch (Exception ex) {};
			if (bw != null) try { bw.close(); } catch (Exception ex) {};
		}
//		Utils.info("Accuracy = "+(double)correct/total*100+
//				 "% ("+correct+"/"+total+") (classification)\n");
		accuracy = (double)correct/total;
		return accuracy;
	}
	
	/**
	 * test file 을 포함한 SvmProblem 객체를 받아 accuracy를 측정한다.
	 * @param model
	 * @param prob
	 * @return
	 */
	public static double predict(SvmModel model, SvmProblem prob) {
		int correct = 0;
		int total = 0;
		double accuracy = 0.0;
		
		for (int i=0; i<prob.l; i++) {
			SvmNode[] example = prob.x[i];
			double target = prob.y[i];
			double v = Svm.svm_predict(model, example);
			if(v == target) {
				++correct;
			}
			++total;
//			Utils.info("Accuracy = "+(double)correct/total*100+
//					 "% ("+correct+"/"+total+") (classification)\n");
		}
		if (total > 0) {
			accuracy = (double)correct/total;
		} else {
			accuracy = -1;
		}
		return accuracy;
	}
	
	public static void main(String args[]) {
		SvmPredict predict = new SvmPredict();
		predict.run();
	}
}
