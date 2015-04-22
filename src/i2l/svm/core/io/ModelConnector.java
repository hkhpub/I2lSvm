package i2l.svm.core.io;

import i2l.svm.core.model.SvmModel;
import i2l.svm.core.model.SvmNode;
import i2l.svm.core.model.SvmParam;
import i2l.svm.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

public class ModelConnector {

	// see SvmParam class
	static final String svm_type_table[] =
	{
		"c_svc", "c_asvc"
	};

	static final String kernel_type_table[]=
	{
		"linear","polynomial","rbf","sigmoid","precomputed"
	};
	
	public static void writeModel(String model_file_name, SvmModel model) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(model_file_name));
			SvmParam param = model.param;
			
			bw.write("svm_type "+svm_type_table[param.svmType]+"\n");
			bw.write("kernel_type "+kernel_type_table[param.kernelType]+"\n");
			
			if (param.kernelType == SvmParam.POLY) {
				bw.write("degree "+param.degree+"\n");
			}
			
			bw.write("gamma "+param.gamma+"\n");
			
			if (param.kernelType == SvmParam.POLY ||
				param.kernelType == SvmParam.SIGMOID) {
				bw.write("coef0 "+param.coef0+"\n");
			}
			
			int nClass = model.nClass;
			int l = model.l;
			bw.write("n_class "+nClass+"\n");
			bw.write("total_sv "+l+"\n");
			
			{
				bw.write("rho");
				for(int i=0;i<nClass*(nClass-1)/2;i++)
					bw.write(" "+model.rho[i]);
				bw.write("\n");
			}
			
			if(model.label != null)
			{
				bw.write("label");
				for(int i=0;i<nClass;i++)
					bw.write(" "+model.label[i]);
				bw.write("\n");
			}
			
			if(model.nSV != null)
			{
				bw.write("n_sv");
				for(int i=0;i<nClass;i++)
					bw.write(" "+model.nSV[i]);
				bw.write("\n");
			}
			
			bw.write("SV\n");
			double[][] sv_coef = model.sv_coef;
			SvmNode[][] SV = model.SV;
			
			for(int i=0;i<l;i++)
			{
				for(int j=0;j<nClass-1;j++)
					bw.write(sv_coef[j][i]+" ");

				SvmNode[] p = SV[i];
				for(int j=0;j<p.length;j++) {
					bw.write(p[j].index+":"+p[j].value+" ");
				}
				bw.write("\n");
			}
			
			bw.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if (fw != null) try { fw.close(); } catch (Exception ex) {};
			if (bw != null) try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	public static SvmModel loadModel(String model_file_name) {
		FileReader fr = null;
		BufferedReader br = null;
		SvmModel model = null;
		try {
			fr = new FileReader(model_file_name);
			br = new BufferedReader(fr);
			
			// read parameters
			model = new SvmModel();
			model.rho = null;
			model.label = null;
			model.nSV = null;
			
			if (readModelHeader(br, model) == false)
			{
				System.err.print("ERROR: failed to read model\n");
				return null;
			}
			
			// read sv_coef and SV
			int m = model.nClass - 1;
			int l = model.l;
			model.sv_coef = new double[m][l];
			model.SV = new SvmNode[l][];
			
			for (int i=0; i<l; i++) {
				String line = br.readLine();
				StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

				for(int k=0;k<m;k++)
					model.sv_coef[k][i] = Utils.atof(st.nextToken());
				int n = st.countTokens()/2;
				model.SV[i] = new SvmNode[n];
				for(int j=0;j<n;j++)
				{
					model.SV[i][j] = new SvmNode();
					model.SV[i][j].index = Utils.atoi(st.nextToken());
					model.SV[i][j].value = Utils.atof(st.nextToken());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if (fr != null) try { fr.close(); } catch (Exception ex) {};
			if (br != null) try { br.close(); } catch (Exception ex) {};
		}
		
		return model;
	}
	
	private static boolean readModelHeader(BufferedReader br, SvmModel model) throws Exception {
		
		SvmParam param = new SvmParam();
		model.param = param;
		
		String line = null;
		while ((line = br.readLine()) != null) {
			String cmd = line;
			String arg = cmd.substring(cmd.indexOf(' ')+1);
			
			if (cmd.startsWith("svm_type")) {
				int i;
				for (i=0; i<svm_type_table.length; i++) {
					if(arg.indexOf(svm_type_table[i])!=-1)
					{
						param.svmType=i;
						break;
					}
				}
				if(i == svm_type_table.length)
				{
					System.err.print("unknown svm type.\n");
					return false;
				}
				
			} 
			else if (cmd.startsWith("kernel_type")) {
				int i;
				for(i=0;i<kernel_type_table.length;i++)
				{
					if(arg.indexOf(kernel_type_table[i])!=-1)
					{
						param.kernelType=i;
						break;
					}
				}
				if(i == kernel_type_table.length)
				{
					System.err.print("unknown kernel function.\n");
					return false;
				}
			} 
			else if(cmd.startsWith("degree")) {
				param.degree = Utils.atoi(arg);
			}
			else if(cmd.startsWith("gamma")) {
				param.gamma = Utils.atof(arg);
			}
			else if(cmd.startsWith("coef0")) {
				param.coef0 = Utils.atof(arg);
			}
			else if(cmd.startsWith("n_class")) {
				model.nClass = Utils.atoi(arg);
			}
			else if(cmd.startsWith("total_sv")) {
				model.l = Utils.atoi(arg);
			} 
			else if(cmd.startsWith("rho")) {
				int n = model.nClass * (model.nClass-1)/2;
				model.rho = new double[n];
				StringTokenizer st = new StringTokenizer(arg);
				for(int i=0;i<n;i++)
					model.rho[i] = Utils.atof(st.nextToken());
			}
			else if(cmd.startsWith("label"))
			{
				int n = model.nClass;
				model.label = new int[n];
				StringTokenizer st = new StringTokenizer(arg);
				for(int i=0;i<n;i++)
					model.label[i] = Utils.atoi(st.nextToken());					
			}
			else if(cmd.startsWith("n_sv"))
			{
				int n = model.nClass;
				model.nSV = new int[n];
				StringTokenizer st = new StringTokenizer(arg);
				for(int i=0;i<n;i++)
					model.nSV[i] = Utils.atoi(st.nextToken());
			}
			else if(cmd.startsWith("SV"))
			{
				break;
			}
			else
			{
				System.err.print("unknown text in model file: ["+cmd+"]\n");
				return false;
			}
		} // end of while
		
		return true;
	}
}
