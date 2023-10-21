package com.victoria.runway.detection;

import com.victoria.runway.image.modification.ImageAdjustment;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

public class HOGBodyDetection {
    private Mat originalImage;
    private Mat processedImage;
    private Mat resizedImage;
    private MatOfRect detections;
    private MatOfDouble weights;
    private HOGDescriptor hogDescriptor;
    private double hitThreshold;

    public HOGBodyDetection(String imagePath, double hitThreshold) {
        try {
            this.hitThreshold = hitThreshold;

            // Load the image from the file path
            this.originalImage = Imgcodecs.imread(imagePath);

            // Check if the image was loaded successfully
            if (this.originalImage.empty()) {
                throw new IllegalArgumentException("Error: Unable to load the image from the given path.");
            }

            // Initialize the processedImage as a deep copy of the original image
            this.processedImage = this.originalImage.clone();

            // Initialize the detections to store detected body rectangles
            this.weights = new MatOfDouble();
            this.detections = new MatOfRect();

            // Set up the HOG descriptor for pedestrian detection
            this.hogDescriptor = new HOGDescriptor();
            this.hogDescriptor.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        } catch (Exception e) {
            // Handle any exceptions during initialization
            e.printStackTrace();
        }
    }

    public void detectHumanBodies() {
        try {
            this.processedImage = ImageAdjustment.colorAdjustment(this.processedImage,Imgproc.COLOR_RGB2GRAY);

            this.hogDescriptor.detectMultiScale(this.processedImage,
                    this.detections,
                    this.weights,
                    hitThreshold,
                    new Size(4, 4),
                    new Size(32, 32),
                    1.05, 2.0, false);

        } catch (Exception e) {
            // Handle any exceptions during body detection
            e.printStackTrace();
        }
    }

    public Rect findLargestBody() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            // Check if human bodies were detected before finding the largest body
            if (this.detections.empty()) {
                detectHumanBodies();
            }

            // Find the largest human body (the one with the maximum area) from the foundLocations
            Rect largestBodyRect = new Rect();
            double maxArea = 0;
            for (Rect rect : this.detections.toArray()) {
                double area = rect.area();
                if (area > maxArea) {
                    maxArea = area;
                    largestBodyRect = rect;
                }
            }

            // Create a new Rect object as the result to avoid any references to the original object
            return new Rect(largestBodyRect.tl(), largestBodyRect.br());
        } catch (Exception e) {
            // Handle any exceptions during finding the largest body
            e.printStackTrace();
            return new Rect(); // Return an empty rectangle in case of error
        }
    }

    public void drawLargestBody(String outputImagePath) {
        try {
            // Find the largest human body (the one with the maximum area)
            Rect largestBodyRect = findLargestBody();

            // Create a copy of the original image to draw the largest body
            Mat outputImage = this.originalImage.clone();

            // Draw the largest body rectangle on the output image
            Imgproc.rectangle(outputImage, largestBodyRect.tl(), largestBodyRect.br(), new Scalar(0, 255, 0), 4);

            // Save the output image to the specified path
            Imgcodecs.imwrite(outputImagePath, outputImage);

            System.out.println("Image with the largest human body is saved at: " + outputImagePath);
        } catch (Exception e) {
            // Handle any exceptions during drawing the largest body
            e.printStackTrace();
        }
    }

    public void drawDetectedBodies(String outputImagePath) {
        try {
            // Check if human bodies were detected before drawing
            if (this.detections.empty()) {
                detectHumanBodies();
            }

            // Create a copy of the original image to draw the detected bodies
            Mat outputImage = this.originalImage.clone();

            // Draw rectangles around all detected bodies on the output image
            for (Rect rect : this.detections.toArray()) {
                Imgproc.rectangle(outputImage, rect.tl(), rect.br(), new Scalar(255, 0, 0), 4);
            }

            // Save the output image to the specified path
            Imgcodecs.imwrite(outputImagePath, outputImage);

            System.out.println("Image with all detected human bodies is saved at: " + outputImagePath);
        } catch (Exception e) {
            // Handle any exceptions during drawing the detected bodies
            e.printStackTrace();
        }
    }

    public void resizeImageToRect(String outputImagePath, Rect resizedImageDimensions) {
        try {
            int centerX = this.originalImage.cols() / 2;

            int cropX = centerX - (resizedImageDimensions.width / 2);
            int cropY = 0;

            Rect cropRegion = new Rect(cropX, cropY,
                    resizedImageDimensions.width, originalImage.rows());

            Mat outputImage = new Mat(originalImage, cropRegion);

            this.resizedImage = outputImage;
            // Save the output image to the specified path
            //Imgcodecs.imwrite(outputImagePath, outputImage);

            //System.out.println("Image with the largest human body is saved at: " + outputImagePath);
        } catch (Exception e) {
            // Handle any exceptions during drawing the largest body
            e.printStackTrace();
        }
    }
}
