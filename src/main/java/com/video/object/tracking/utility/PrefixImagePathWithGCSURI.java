package com.video.object.tracking.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PrefixImagePathWithGCSURI {

	public static void prefixImagePathWithGCSPathInCSV(String inputFile, String outputfile, String gcsprefix) {

		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			br = new BufferedReader(new FileReader(new File(inputFile)));
			bw = new BufferedWriter(new FileWriter(new File(outputfile)));

			StringBuilder builder = new StringBuilder();
			String line = null;

			boolean isFirst = true;
			while ((line = br.readLine()) != null) {

				if (isFirst) {
					isFirst = false;
					continue;
				}

				String[] elements = line.split(",");

				builder.append(elements[0] + "," + gcsprefix + elements[1] + "," + elements[2] + "," + elements[3] + ","
						+ elements[4] + ",,," + elements[5] + "," + elements[6] + ",,");

				builder.append("\r\n");

			}

			bw.write(builder.toString());

			System.out.println("Data written to " + outputfile);

		} catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {

			if (null != br) {

				try {
					br.close();
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}

			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {

		String inputFile = "/Users/lalamanil/voiceanalyzer/accidentDataSet/custom_dataset/RenamedImages/LabelImages/annotation.csv";
		String outputfile = "/Users/lalamanil/voiceanalyzer/accidentDataSet/custom_dataset/RenamedImages/LabelImages/gcsautomlannotation.csv";
		String gcsprefix = "gs://accident-detection-dataset/images/";
		prefixImagePathWithGCSPathInCSV(inputFile, outputfile, gcsprefix);
	}

}
