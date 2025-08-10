package com.video.object.tracking.utility;
/*
 * @author lalamanil 
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GCSStorageUtility {

	private static Storage storage;
	//Bucket name created in GCP console
	private static String bucketName = "accident-detection-dataset";
	//Folder where images will be stored with in the bucket
	private static String imageprefix = "images/";
	//Folder where Annotated CSV file will be stored with in the bucket
	private static String annotatedCSVPrefix = "annotations/";
	//Local ImageFolder path where images are located
	private static String imageFolderPath = "/Users/lalamanil/voiceanalyzer/accidentDataSet/custom_dataset/RenamedImages";
	//Local annotated CSV file path where annotated CSV file is located 
	private static String annotationCSVFilePath = "/Users/lalamanil/voiceanalyzer/accidentDataSet/custom_dataset/RenamedImages/LabelImages/gcsautomlannotation.csv";

	static {
		try {
			//reading service account
			InputStream inputStream = GCSStorageUtility.class.getClassLoader()
					.getResourceAsStream("ServiceAccount.json");
			GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream)
					.createScoped("https://www.googleapis.com/auth/cloud-platform");
			storage = StorageOptions.newBuilder().setCredentials(googleCredentials).build().getService();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// uploading images (dataset) to google cloud storage bucket
	public static void uploadImageFiles() {
		File imageFolder = new File(imageFolderPath);
		File[] files = imageFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (File file : files) {
			executorService.submit(() -> {
				try {
					BlobId blobId = BlobId.of(bucketName, imageprefix + file.getName());
					BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
					Blob savedBlob = storage.create(blobInfo, Files.readAllBytes(file.toPath()));
					System.out.println(savedBlob.getBlobId() + " " + file.getName());
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			});
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("✅ All uploads finished.");

	}

	// uploading Annotated csv file to GCS bucket
	public static void uploadAnnotatedCSVFile() {
		File file = new File(annotationCSVFilePath);
		BlobId blobId = BlobId.of(bucketName, annotatedCSVPrefix + "accident_annotations.csv");
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		try {
			Blob blob = storage.create(blobInfo, Files.readAllBytes(file.toPath()));
			System.out.println(blob.getBlobId() + ":" + file.getName());
			System.out.println("✅  upload successful.");
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		uploadImageFiles();
		//uploadAnnotatedCSVFile();
	}

}
