package org.usfirst.frc.team1318.robot.Vision;

import org.opencv.core.Scalar;

public class VisionConstants
{
    // Debug output settings:
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_PRINT_OUTPUT = true;
    public static final boolean DEBUG_PRINT_ANALYZER_DATA = false;
    public static final int DEBUG_FPS_AVERAGING_INTERVAL = 30;
    public static final boolean DEBUG_FRAME_OUTPUT = false;
    public static final int DEBUG_FRAME_OUTPUT_GAP = 30; // the number of frames to wait between saving debug image output
    public static final String DEBUG_OUTPUT_FOLDER = "/home/pi/vision/";

    // Settings for AXIS IP-based Camera
    public static final int CAMERA_FPS = 30;
    public static final String CAMERA_IP_ADDRESS = "169.254.13.18"; // "169.254.7.31", "169.254.59.141";
    public static final String CAMERA_USERNAME_PASSWORD = "root:1318";
    public static final int CAMERA_RESOLUTION_X = 320;
    public static final int CAMERA_RESOLUTION_Y = 240;
    public static final String CAMERA_RESOLUTION = String.format("%dx%d", VisionConstants.CAMERA_RESOLUTION_X,
        VisionConstants.CAMERA_RESOLUTION_Y);
    public static final String CAMERA_MJPEG_PASS_URL = String.format(
        "http://%s@%s/mjpg/video.mjpg?resolution=%s&req_fps=%d&.mjpg",
        VisionConstants.CAMERA_USERNAME_PASSWORD,
        VisionConstants.CAMERA_IP_ADDRESS,
        VisionConstants.CAMERA_RESOLUTION,
        VisionConstants.CAMERA_FPS);
    public static final String CAMERA_MJPEG_URL = String.format(
        "http://%s/mjpg/video.mjpg?resolution=%s&req_fps=%d&.mjpg",
        VisionConstants.CAMERA_IP_ADDRESS,
        VisionConstants.CAMERA_RESOLUTION,
        VisionConstants.CAMERA_FPS);

    // HSV Filtering constants
    public static final Scalar HSV_FILTER_LOW = new Scalar(70, 150, 150);
    public static final Scalar HSV_FILTER_HIGH = new Scalar(90, 255, 255);
}
