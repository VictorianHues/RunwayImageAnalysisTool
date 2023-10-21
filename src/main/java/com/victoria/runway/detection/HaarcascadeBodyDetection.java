package com.victoria.runway.detection;

import com.victoria.runway.image.modification.ImageAdjustment;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.List;

public class HaarcascadeBodyDetection {
    protected final Mat originalImage;
    protected final Mat grayImage;
    protected static Mat equalizedGrayImage;
    public static Rect largestFaceDetection;
    public static Rect largestFullBodyDetection;
    public static Rect largestUpperBodyDetection;
    public static Rect largestLowerBodyDetection;
    private final Mat largestFaceDetectionImage;
    private final Mat largestFullBodyDetectionImage;
    private final Mat largestUpperBodyDetectionImage;
    private final Mat largestLowerBodyDetectionImage;

    public HaarcascadeBodyDetection(Mat inputImage) throws Exception {
        this.originalImage = inputImage;

        // Convert the image to grayscale
        this.grayImage = ImageAdjustment.colorAdjustment(originalImage, Imgproc.COLOR_RGB2GRAY);

        equalizedGrayImage = grayImage;
        Imgproc.equalizeHist(grayImage, equalizedGrayImage);

        largestFullBodyDetection = detection("haarcascade_fullbody.xml");
        largestFaceDetection = detection("haarcascade_frontalface_alt.xml");
        largestUpperBodyDetection = detection("haarcascade_upperbody.xml");
        largestLowerBodyDetection = detection("haarcascade_lowerbody.xml");


        largestFullBodyDetectionImage = drawRectangle(largestFullBodyDetection, new Scalar(0, 255, 0));
        largestFaceDetectionImage = drawCircle(largestFaceDetection, new Scalar(255, 0, 255));
        largestUpperBodyDetectionImage = drawRectangle(largestUpperBodyDetection, new Scalar(255, 0, 0));
        largestLowerBodyDetectionImage = drawRectangle(largestLowerBodyDetection, new Scalar(0, 0, 255));

    }
    protected static Rect detection(String haarscascadeXML) {
        // Load the haar cascades human body detection classifier
        CascadeClassifier cascade = new CascadeClassifier("lib/opencv/build/etc/haarcascades/" + haarscascadeXML);

        // Perform object detection
        MatOfRect detections = new MatOfRect();
        cascade.detectMultiScale(equalizedGrayImage, detections);
        List<Rect> listOfDetections = detections.toList();

        // Find the object face detected
        Rect largestDetection = null;
        double largestArea = 0;
        for (Rect currentDetection : listOfDetections) {
            double currentArea = currentDetection.width * currentDetection.height;
            if (currentArea > largestArea) {
                largestDetection = currentDetection;
                largestArea = currentArea;
            }
        }
        return largestDetection;
    }

    public Mat drawCircle(Rect detection, Scalar color) {
        Mat modifiedImage = originalImage.clone();
        if (detection != null) {
            Point center = new Point(detection.x + detection.width / 2.0, detection.y + detection.height / 2.0);
            Imgproc.ellipse(modifiedImage, center, new Size(detection.width / 2.0, detection.height / 2.0),
                    0, 0, 360, color);
        }
        return modifiedImage;
    }

    public Mat drawRectangle(Rect detection, Scalar color) {
        Mat modifiedImage = originalImage.clone();
        if (detection != null) {
            Imgproc.rectangle(modifiedImage, detection.tl(), detection.br(), color, 3);
        }
        return modifiedImage;
    }

    public Mat getLargestFaceDetectionImage() {
        return largestFaceDetectionImage;
    }

    public Mat getLargestFullBodyDetectionImage() {
        return largestFullBodyDetectionImage;
    }

    public Mat getLargestUpperBodyDetectionImage() {
        return largestUpperBodyDetectionImage;
    }

    public Mat getLargestLowerBodyDetectionImage() {
        return largestLowerBodyDetectionImage;
    }
}
