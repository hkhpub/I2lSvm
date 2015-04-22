package i2l.svm.utils;

import i2l.svm.objects.AsvmAcc;
import i2l.svm.objects.SvmOPAcc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

public class LogUtils {
	
	public static void logOPAccuracy(String log_file, SvmOPAcc accuracy) {
		File file = new File(log_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			bw.write("["+new Date().toString()+"] Accuracy: "+String.format("%.4f", accuracy.value)+" | C: "+accuracy.C+ " | gamma: "+accuracy.gamma+"\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	public static void logAsvmAccuracy(String log_file, AsvmAcc accuracy) {
		File file = new File(log_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			bw.write("["+new Date().toString()+"] Asvm-Accuracy: "+String.format("%.4f", accuracy.value)+" | a-Rate: "+accuracy.a_rate+"\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	public static void logAsvmAccuracy2(String log_file, AsvmAcc accuracy) {
		File file = new File(log_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			bw.write("["+new Date().toString()+"] Asvm-Accuracy: "+String.format("%.4f", accuracy.value)+" | a-Rate: "+accuracy.a_rate
					+" | C: "+accuracy.C+ " | gamma: "+accuracy.gamma+"\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	/**
	 * validation set으로 optimal parameter를 선택한 후 test set으로 실험한 accuracy
	 * @param optimal_acc_file
	 * @param optimalAccs
	 */
	public static void logOptimalAccuracy(String optimal_acc_file, double[] optimalAccs) {
		File file = new File(optimal_acc_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			
			for (int i=0; i<optimalAccs.length; i++) {
				bw.write("[Data set "+(i+1)+"] Accuracy: "+String.format("%.4f", optimalAccs[i])+"\n");
				bw.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	public static void logTopOPAccuracy(String optimal_param_file, SvmOPAcc[] optimalAccs) {
		File file = new File(optimal_param_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			
			for (int i=0; i<optimalAccs.length; i++) {
				bw.write("[Data set "+(i+1)+"] Asvm-Accuracy: "+String.format("%.4f", optimalAccs[i].value)+" | C: "+optimalAccs[i].C+ " | gamma: "+optimalAccs[i].gamma+"\n");
				bw.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	/**
	 * for ASVM
	 * validation set으로 optimal parameter를 선택하는 과정에서 나타난 optimal parameter pairs
	 * @param optimal_param_file
	 * @param optimalAccs
	 */
	public static void logTopAsvmAccuracy(String optimal_param_file, AsvmAcc[] optimalAccs) {
		File file = new File(optimal_param_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			
			for (int i=0; i<optimalAccs.length; i++) {
				bw.write("[Data set "+(i+1)+"] Asvm-Accuracy: "+String.format("%.4f", optimalAccs[i].value)+" | a-Rate: "+optimalAccs[i].a_rate
						+" | C: "+optimalAccs[i].C+ " | gamma: "+optimalAccs[i].gamma+"\n");
				bw.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	public static void logTopAsvmAccuracy2(String optimal_param_file, AsvmAcc[] optimalAccs) {
		File file = new File(optimal_param_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			
			for (int i=0; i<optimalAccs.length; i++) {
				bw.write("[Data set "+(i+1)+"] Asvm-Accuracy: "+String.format("%.4f", optimalAccs[i].value)+" | a-Rate: "+optimalAccs[i].a_rate+"\n");
				bw.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	public static void logString(String logtext) {
		File file = new File("outputs/log/log.txt");
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			bw.write(logtext+"\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
}
