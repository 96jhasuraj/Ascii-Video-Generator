package utility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class VideoFrameExtractor {
    
    private File makeOutDir(String outputDir)
    {
        File outputDirectory = new File(outputDir);
        
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        return outputDirectory;
    }
    private ProcessBuilder buildFFMEGCommand(Integer fps,String videoPath,String outputDir)
    {
            String fps_edited = (fps!=null) ? fps.toString() : "";
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", videoPath,
                "-vf", "fps=" + fps_edited,
                "-q:v", "1",  // Quality
                outputDir + "/frame_%06d.jpg"
            );      
            pb.redirectErrorStream(true);
            return pb; 
    }
    private ProcessBuilder buildFFProbeCommand(String videoPath)
    {
        ProcessBuilder pb = new ProcessBuilder(
            "ffprobe",
            "-v", "error",
            "-select_streams", "v:0",
            "-show_entries", "stream=width,height,r_frame_rate,nb_frames",
            "-of", "csv=p=0",
            videoPath
        );    
        pb.redirectErrorStream(true);
        return pb;
    }
    private void readInputStream(Process process) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Extracting frames...");
            
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
    }
    private int handleExitCode(int exitCode,File outputDirectory)
    {
        return new ExitCode(exitCode).printMessage(outputDirectory);
    }
    public int extractFrames(String videoPath, String outputDir, Integer fps) 
    {
        File outputDirectory = makeOutDir(outputDir);
        try {

            
            ProcessBuilder pb = buildFFMEGCommand(fps,videoPath,outputDir);
            Process process = pb.start();
            readInputStream(process);            
            int exitCode = process.waitFor();
            int frames = handleExitCode(exitCode,outputDirectory);
            return frames;
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Error extracting frames: " + e.getMessage());
            return 0;
        }
    }
    private void getstats(VideoInfo info,String line)
    {
        String[] parts = line.split(",");
        info.width = Integer.parseInt(parts[0]);
        info.height = Integer.parseInt(parts[1]);

        String[] fpsParts = parts[2].split("/");
        if (fpsParts.length == 2) {
            info.fps = Double.parseDouble(fpsParts[0]) / Double.parseDouble(fpsParts[1]);
        } else {
            info.fps = Double.parseDouble(fpsParts[0]);
        }
        
        if (parts.length > 3 && !parts[3].isEmpty()) {
            info.totalFrames = Integer.parseInt(parts[3]);
        }
    }
    public VideoInfo getInfo(String line,Process process)
    {
        if (line != null) {
            VideoInfo info = new VideoInfo();
            getstats(info,line);
            return info;
        }
        else 
            return null;
    }
    public VideoInfo getVideoInfo(String videoPath) {
        try 
        {
            ProcessBuilder pb = buildFFProbeCommand(videoPath);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            VideoInfo info = getInfo(line,process);
            process.waitFor();
            return info;
        } catch (Exception e) {
            System.err.println("Error getting video info: " + e.getMessage());
        }
        return null;
    }
    public Dimension calculateAsciiDimensions(int videoWidth, int videoHeight, int maxWidth) {
        double aspectRatio = (double) videoWidth / videoHeight;
        
        double charAspectRatio = 0.5;         
        int asciiWidth = maxWidth;
        int asciiHeight = (int) (asciiWidth / aspectRatio * charAspectRatio);
        
        return new Dimension(asciiWidth, asciiHeight);
    }
    
}
