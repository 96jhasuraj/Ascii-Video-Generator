import utility.Dimension;
import utility.VideoFrameExtractor;
import utility.VideoInfo;
import java.io.*;
public class App {
    static void usage()
    {

        System.out.println("Usage: java VideoFrameExtractor <video_path> <output_directory> [fps]");
        System.out.println("\nExample:");
        System.out.println("  java VideoFrameExtractor video.mp4 ./frames");
        System.out.println("  java VideoFrameExtractor video.mp4 ./frames 24");
        System.exit(1);
    }
    static void intro()
    {
        System.out.println("Video Frame Extractor");
        System.out.println("=====================");
    }
    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
        }
        else 
            intro();
        String videoPath = args[0];
        String outputDir = args[1];
        try{
            Integer fps = args.length > 2 ? Integer.parseInt(args[2]) : null;
            VideoFrameExtractor videoFramExtractor = new VideoFrameExtractor();
            System.out.println("\nAnalyzing video...");
            VideoInfo info = videoFramExtractor.getVideoInfo(videoPath);
            printVideoInfo(info,outputDir,fps);
            int frameCount = videoFramExtractor.extractFrames(videoPath, outputDir, fps);
            printEndMessage(frameCount,fps,info,outputDir);
        }catch (NullPointerException e)
        {
            System.out.println(e.getMessage());
        }
    }

    static void printEndMessage(int frameCount,Integer fps , VideoInfo info,String outputDir)
    {
        if (frameCount > 0) {
            System.out.println("\nExtraction complete!");
            System.out.println("\nTo play the video:");
            System.out.println("  java AsciiVideoPlayer " + outputDir + " " + frameCount +
                    " " + (fps != null ? fps : (info != null ? (int) info.getFps() : 24)));
        } else {
            System.err.println("\n✗ Extraction failed!");
            System.err.println("\nMake sure FFmpeg is installed and in your PATH:");
        }
    }

    static void printVideoInfo(VideoInfo info,String outputDir,Integer fps) throws NullPointerException
    {
        if (info != null) {
            System.out.println("Video Resolution: " + info.getWidth() + "x" + info.getHeight());
            System.out.println("Video FPS: " + String.format("%.2f", info.getFps()));
            if (info.getFrames() > 0) {
                System.out.println("Total Frames: " + info.getFrames());
            }
            System.out.println("\nExtracting frames to: " + outputDir);
            if (fps != null) {
                System.out.println("Target FPS: " + fps);
            }
        }
        else throw new NullPointerException("info is null");
    }
}
