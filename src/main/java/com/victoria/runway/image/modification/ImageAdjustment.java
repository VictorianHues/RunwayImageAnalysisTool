package com.victoria.runway.image.modification;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;



public class ImageAdjustment {
    public static Mat colorAdjustment(Mat inputImage, int colorConversionCode) {
        try {
            // Check if the image was loaded successfully
            if (inputImage.empty()) {
                throw new IllegalArgumentException("Error: Unable to load the given image.");
            }

            // Create the empty destination matrix
            Mat convertedImage = new Mat();

            // Convert the image to the specified Imgproc color conversion and save the converted image
            Imgproc.cvtColor(inputImage, convertedImage, colorConversionCode);

            return convertedImage;
        }
        catch (Exception e) {
            e.printStackTrace();
            return inputImage;
        }
    }
}
