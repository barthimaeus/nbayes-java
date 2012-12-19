import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JFileChooser;

public class NBayes {

	public String[][][] data = null;
	HashMap<String, Integer> class_counts = null;
	HashMap<String, Double> class_probs = null;
	HashMap<String, HashMap<String, Integer>> classified_attribute_counts = null;
	HashMap<String, HashMap<String, Double>> classified_attribute_probs = null;

	public NBayes(File file) {
		data = readData(file);
		class_counts = new HashMap<String, Integer>();
		class_probs = new HashMap<String, Double>();
		classified_attribute_counts = new HashMap<String, HashMap<String, Integer>>();
		classified_attribute_probs = new HashMap<String, HashMap<String, Double>>();
		for (String[] in : data[0]) {
			String label = in[in.length - 1];
			class_counts
					.put(label,
							class_counts.get(label) != null ? class_counts
									.get(label) + 1 : 1);

			if (classified_attribute_counts.get(label) == null) {
				classified_attribute_counts.put(label,
						new HashMap<String, Integer>());
			}

			for (int i = 0; i < in.length - 1; i++) {
				Integer class_attr_count = classified_attribute_counts.get(
						label).get(in[i]);
				if (class_attr_count == null) {
					class_attr_count = 1;
				} else {
					class_attr_count++;
				}
				classified_attribute_counts.get(label).put(in[i],
						class_attr_count);
			}
		}

		for (String label : class_counts.keySet()) {
			class_probs.put(label, class_counts.get(label)
					/ (double) data[0].length);
			classified_attribute_probs
					.put(label, new HashMap<String, Double>());
			double sum = 0;
			for (String attr_label : classified_attribute_counts.get(label)
					.keySet()) {
				classified_attribute_probs.get(label).put(
						attr_label,
						classified_attribute_counts.get(label).get(attr_label)
								/ (double) class_counts.get(label));
				sum += classified_attribute_probs.get(label).get(attr_label);
			}
		}
	}

	public String[][][] readData(File file) {
		ArrayList<String[]> data_list = new ArrayList<String[]>();
		try {
			RandomAccessFile f = new RandomAccessFile(file, "r");
			String text = null;
			while ((text = f.readLine()) != null) {
				String[] instance = text.split(",");
				for(int i = 0; i<instance.length; i++) {
					instance[i] = "" + i + instance[i];
				}
				data_list.add(instance);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[][][] data = new String[2][][];
		data[0] = new String[data_list.size()*2/3][];
		data[1] = new String[data_list.size()- data[0].length][];
		int j = 0;
		int k = 0;
		for (int i = 0; i < data_list.size(); i++) {
			double r = Math.random();
			if(j < data[0].length && r < 2.0 / 3.0 || k >= data[1].length) {
				data[0][j] = data_list.get(i);
				j++;
			} else {
				data[1][k] = data_list.get(i);
				k++;
			}
		}
		return data;
	}

	public String classify(String[] instance) {
		HashMap<Double, String> probs = new HashMap<Double, String>();
		for (String label : class_counts.keySet()) {
			double prob = class_probs.get(label);
			for (String attr : instance) {
				if(!attr.startsWith("6")) {
					Double p = classified_attribute_probs.get(label).get(attr);
					if (p == null) {
						p = 0.0;
					}
					prob *= p;
				}
			}
			probs.put(prob, label);
		}

		return probs.get(Collections.max(probs.keySet()));
	}
	
	public double crossvalidate() {
		int pos = 0;
		int neg = 0;
		for (int i = 0; i < data[1].length; i++) {
			if(classify(data[1][i]).equals(data[1][i][data[1][i].length - 1]))
				pos++;
			else
				neg++;
		}
		return pos * 1.0 / (pos + neg);
	}
	
	public void printConfusionMatrix() {
		HashMap<String, HashMap<String, Integer>> m = new HashMap<String, HashMap<String, Integer>>();
		for(String l1 : class_counts.keySet()) {
			m.put(l1, new HashMap<String, Integer>());
			for(String l2 : class_counts.keySet()) {
				m.get(l1).put(l2, 0);
			}
		}
		for(String[] d : data[1]) {
			m.get(classify(d)).put(d[d.length -1], m.get(classify(d)).get(d[d.length -1])+1);
		}
		
		for(String l1 : class_counts.keySet()) {
			System.out.print(l1 + "\t");
			for(String l2 : class_counts.keySet()) {
				System.out.print(m.get(l1).get(l2) + "\t");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		JFileChooser jfc = new JFileChooser();
		jfc.showOpenDialog(null);
		double sum = 0;
		int num = 100;
		for(int i = 0; i<num; i++) {
			NBayes nb = new NBayes(jfc.getSelectedFile());
			sum += nb.crossvalidate();
		}
		System.out.println(sum/num);
		// ~84.9%
		NBayes nb = new NBayes(jfc.getSelectedFile());
		nb.printConfusionMatrix();
	}

}
