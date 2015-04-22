package i2l.svm.core.io;

import i2l.svm.core.model.SvmNode;
import i2l.svm.core.model.SvmProblem;
import i2l.svm.utils.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

public class ProblemReader {

	public static SvmProblem readProblem(String input_file_name) {
		BufferedReader br = null;
		FileReader fr = null;
		Vector<Double> labels = new Vector<Double>();
		Vector<SvmNode[]> points = new Vector<SvmNode[]>(); // data points
		int maxIndex = 0; // 최대 feature 개수
		
		SvmProblem prob = null;

		try {
			fr = new FileReader(input_file_name);
			br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

				labels.addElement(Utils.atof(st.nextToken()));
				int m = st.countTokens() / 2;
				SvmNode[] point = new SvmNode[m];
				for (int j = 0; j < m; j++) {
					point[j] = new SvmNode();
					point[j].index = Utils.atoi(st.nextToken());
					point[j].value = Utils.atof(st.nextToken());
				}
				if (m > 0) {
					maxIndex = Math.max(maxIndex, point[m - 1].index);
				}
				points.addElement(point);
			} // end read lines

			prob = new SvmProblem();
			prob.l = labels.size();
			// set data points
			prob.x = new SvmNode[prob.l][];
			for (int i = 0; i < prob.l; i++) {
				prob.x[i] = points.elementAt(i);
			}
			// set labels
			prob.y = new double[prob.l];
			for (int i = 0; i < prob.l; i++) {
				prob.y[i] = labels.elementAt(i);
			}
			
			prob.max_feat_dim = maxIndex;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (fr != null) try { fr.close(); } catch (Exception ex) {} ;
			if (br != null) try { br.close(); } catch (Exception ex) {} ;
		}
		
		return prob;
	}
}
