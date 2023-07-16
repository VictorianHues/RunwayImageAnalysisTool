package com.victoria.runway.image.modification;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;



public class imageAdjustment {
    public static Mat colorAdjustment(Mat inputImage, int colorConversionCode) throws Exception {
        try {
            // Create the empty destination matrix
            Mat convertedImage = new Mat();

            // Convert the image to the specified Imgproc color conversion and save the converted image
            Imgproc.cvtColor(inputImage, convertedImage, colorConversionCode);

            return convertedImage;
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new Exception(e);
        }
    }
}
