package com.video.object.tracking.utility;
/**
 *  @author lalamanil */
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_videoio.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

//Utility  to extract image frames from video to annotate dataset for Custom Object tracking model
public class ExtractFramesAsImagesFromVideo {

	public static void getFramesFromVideo(String videoPath, String outputpath) {
		VideoCapture videoCapture = new VideoCapture(videoPath);
		Mat frame = new Mat();
		int frameNumber = 0;
		int imageCount = 0;
		while (videoCapture.read(frame)) {
			// logic to pull every 50th frame. if we put 100, It pulls every 100th frame
			if (frameNumber % 50 == 0) {
				imwrite(outputpath + "frame_" + imageCount + ".jpg", frame);
				System.out.println(outputpath + "frame_" + imageCount + ".jpg");
				imageCount++;
			}
			frameNumber++;
		}
		videoCapture.release();
		videoCapture.close();
		System.out.println("Done!!");
	}

	public static void main(String[] args) {

		// Path to mp4 video
		String inputFilePath = "/Users/lalamanil/voiceanalyzer/CarAccidentTest.mp4";
		// Folder path where Image frames need to be stored 
		String outputFilePath = "/Users/lalamanil/voiceanalyzer/CarAccidentTestFrames/";
		getFramesFromVideo(inputFilePath, outputFilePath);
	}

}
