package com.video.object.tracking.utility;
/**
 * @author lalamanil 
 *
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConvertMakeSenseToVertexAIAutoMLCSV {

	public static void saveCSV(String data, String outputFilePath) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(outputFilePath)));
			bw.write(data);

			System.out.println("Created VertexAI AutoML compatible CSV annotated file at:" + outputFilePath);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
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

	public static void mapMakeSenceCSVToAutoMlCSV(String makeSenseFilePath, String outputFilePath) {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(makeSenseFilePath)));

			String line = null;

			boolean isFirst = Boolean.TRUE;

			StringBuilder builder = new StringBuilder();

			while ((line = br.readLine()) != null) {

				if (isFirst) {

					builder.append("SET,IMAGE_PATH,LABEL,X_MIN,Y_MIN,X_MAX,Y_MIN,X_MAX,Y_MAX,X_MIN,Y_MAX");
					builder.append("\r\n");
					isFirst = Boolean.FALSE;
					continue;
				}

				String[] elements = line.split(",");

				if (elements.length != 8) {
					continue;
				}

				String label = elements[0];

				double x = Double.parseDouble(elements[1]);
				double y = Double.parseDouble(elements[2]);
				double width = Double.parseDouble(elements[3]);
				double height = Double.parseDouble(elements[4]);
				String imageName = elements[5];

				double imageWidth = Double.parseDouble(elements[6]);
				double imageHeight = Double.parseDouble(elements[7]);

				// converting pixel value to normalized values (0 to 1)
				double xmin = x / imageWidth;
				double ymin = y / imageHeight;
				double xmax = (x + width) / imageWidth;
				double ymax = (y + height) / imageHeight;

				String entry = String.format("UNASSIGNED,%s,%s,%.6f,%.6f,%s,%s,%.6f,%.6f,%s,%s", imageName, label, xmin,
						ymin, "", "", xmax, ymax, "", "");
				builder.append(entry);
				builder.append("\r\n");

			}

			// System.out.println(builder.toString());
			saveCSV(builder.toString(), outputFilePath);

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
		}

	}

	public static void main(String[] args) {

		String makeSenseFile = "/Users/lalamanil/voiceanalyzer/demo/demoaccident.csv";
		String outputFile = "/Users/lalamanil/voiceanalyzer/demo/vertextcompatible.csv";

		mapMakeSenceCSVToAutoMlCSV(makeSenseFile, outputFile);

	}

}
