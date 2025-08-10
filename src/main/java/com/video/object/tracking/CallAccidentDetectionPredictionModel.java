package com.video.object.tracking;

import com.video.object.tracking.utility.AccidentDetectionPredictionModel;

/**
 * @author lalamanil
 *
 */
public class CallAccidentDetectionPredictionModel {
	public static void main(String[] args) {
		String projectId = "<GCP PROJECT ID>";
		// for example: us-central1
		String location = "<LOCATION OF Object detection Model >";
		String endpointId = "<End Point>";
		String locationPath = "<File path of Image in your local machine>";
		AccidentDetectionPredictionModel.predictImage(projectId, location, endpointId, locationPath);
	}
}
