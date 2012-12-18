import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JFileChooser;

public class NBayes {

	String[][] data = null;
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
		for (String[] in : data) {
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
					/ (double) data.length);
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

	public String[][] readData(File file) {
		ArrayList<String[]> data_list = new ArrayList<String[]>();
		try {
			RandomAccessFile f = new RandomAccessFile(file, "r");
			String instane = null;
			while ((instane = f.readLine()) != null) {
				data_list.add(instane.split(","));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[][] data = new String[data_list.size()][];
		for (int i = 0; i < data.length; i++) {
			data[i] = data_list.get(i);
		}
		return data;
	}

	public String classify(String[] instance) {
		HashMap<Double, String> probs = new HashMap<Double, String>();
		for (String label : class_counts.keySet()) {
			double prob = class_probs.get(label);
			for (String attr : instance) {
				Double p = classified_attribute_probs.get(label).get(attr);
				if (p == null) {
					p = 0.0;
				}
				prob *= p;
			}
			probs.put(prob, label);
		}

		return probs.get(Collections.max(probs.keySet()));
	}

	public static void main(String[] args) {
		JFileChooser jfc = new JFileChooser();
		jfc.showOpenDialog(null);
		NBayes nb = new NBayes(jfc.getSelectedFile());
		//Example for Car Dataset
		String[] i1 = { "low", "low", "5more", "4", "big", "high" };
		System.out.println(nb.classify(i1));
	}

}
