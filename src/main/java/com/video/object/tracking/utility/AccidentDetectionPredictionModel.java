package com.video.object.tracking.utility;

/*
 * @Author Anil Lalam 
 * 
 * 
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictRequest;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

public class AccidentDetectionPredictionModel {

	private static PredictionServiceClient predictionServiceClient;

	static {
		InputStream inputStream = AccidentDetectionPredictionModel.class.getClassLoader()
				.getResourceAsStream("ServiceAccount.json");
		if (null != inputStream) {
			try {
				GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream);
				PredictionServiceSettings predictionServiceSettings = PredictionServiceSettings.newBuilder()
						.setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials)).build();
				predictionServiceClient = PredictionServiceClient.create(predictionServiceSettings);

			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else {
			System.out.println("Inputstream is null. Please check service Account in src/main/resources");
		}

	}

	public static void predictImage(String projectId, String location, String endpointId, String localImagePath) {
		if (null != predictionServiceClient) {
			try {
				// read image as bytes
				byte[] content = Files.readAllBytes(new File(localImagePath).toPath());
				String base64Image = Base64.getEncoder().encodeToString(content);
				// Build instance
				Struct instance = Struct.newBuilder()
						.putFields("content", Value.newBuilder().setStringValue(base64Image).build()).build();

				EndpointName endpointName = EndpointName.of(projectId, location, endpointId);
				PredictRequest request = PredictRequest.newBuilder().setEndpoint(endpointName.toString())
						.addInstances(Value.newBuilder().setStructValue(instance).build()).build();
				PredictResponse predictResponse = predictionServiceClient.predict(request);
				System.out.println("Prediction results:");
				if (null != predictResponse) {
					List<Value> predictionList = predictResponse.getPredictionsList();
					if (null != predictionList && !predictionList.isEmpty()) {
						for (Value prediction : predictionList) {
							Struct predictionStruct = prediction.getStructValue();
							// labels
							List<Value> labels = predictionStruct.getFieldsOrThrow("displayNames").getListValue()
									.getValuesList();
							// confidence scores
							List<Value> scores = predictionStruct.getFieldsOrThrow("confidences").getListValue()
									.getValuesList();
							// Bounding boxes
							List<Value> bboxes = predictionStruct.getFieldsOrThrow("bboxes").getListValue()
									.getValuesList();

							for (int i = 0; i < labels.size(); i++) {
								String label = labels.get(i).getStringValue();
								double score = scores.get(i).getNumberValue();
								List<Value> box = bboxes.get(i).getListValue().getValuesList();
								double yMin = box.get(0).getNumberValue();
								double xMin = box.get(1).getNumberValue();
								double yMax = box.get(2).getNumberValue();
								double xMax = box.get(3).getNumberValue();
								System.out.printf(
										"Object: %s, Confidence: %.2f,BBox[yMin=%.2f, xMin=%.2f, yMax=%.2f,xMax=%.2f]",
										label, score, yMin, xMin, yMax, xMax);
								System.out.println();
							}

						}

					} else {
						System.out.println("predictionList is null or empty");
					}
				} else {

					System.out.println("predictResponse is null or empty");

				}

			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else {
			System.out.println("predictionServiceClient is null or empty");
		}

	}

}
