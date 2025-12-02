package videoplayer;

import java.io.*;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import videoplayer.ColorHandler;

public class AdvancedAsciiPlayer {
    private BlockingQueue<Frame> frameBuffer;
    private int frameRate;
    private int width;
    private int height;
    private volatile boolean isPlaying;
    private boolean useColor;
    private ColorHandler colorHandler ;
    private TerminalHelper terminalHelp;
    private long frameDelayMs;
    private int framesDisplayed;

    public AdvancedAsciiPlayer(int frameRate, int width, int height, boolean useColor, boolean detailedChars) 
    {
        this.frameRate = frameRate;
        this.width = width;
        this.height = height;
        this.useColor = useColor;
        this.frameBuffer = new LinkedBlockingQueue<>(60);
        this.isPlaying = false;
        this.colorHandler = new ColorHandler(useColor,detailedChars);
        this.terminalHelp = new TerminalHelper();
        this.frameDelayMs = 1000 / frameRate;
        this.framesDisplayed=0;
    }

    private BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = resized.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                          java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resized;
    }
    
    private class FrameProducer implements Runnable {
        private String framesDirectory;
        private int totalFrames;
        
        public FrameProducer(String framesDirectory, int totalFrames) {
            this.framesDirectory = framesDirectory;
            this.totalFrames = totalFrames;
        }
        
        @Override
        public void run() {
            try 
            {
                for (int i = 1; i <= totalFrames && isPlaying; i++) 
                {
                    File frameFile = new File(framesDirectory + "/frame_" + String.format("%06d", i) + ".jpg");
                    if (!frameFile.exists()) 
                    {
                        frameFile = new File(framesDirectory + "/frame_" + String.format("%06d", i) + ".png");
                    }
                    
                    if (!frameFile.exists()) {
                        System.err.println("Frame not found: " + i);
                        continue;
                    }
                    
                    BufferedImage image = ImageIO.read(frameFile);
                    BufferedImage resized = resizeImage(image, width, height);
                    String asciiFrame = colorHandler.imageToAscii(resized);
                    frameBuffer.put(new Frame(asciiFrame, i));
                }
            } catch (Exception e) {
                System.err.println("Error in frame producer: " + e.getMessage());
            }
        }
    }
    private int helpstartTerminal(){
        isPlaying = true;
        terminalHelp.hideCursor();
        terminalHelp.clearScreen();
        return 0;
    }
    private boolean continuePlaying(int framesDisplayed,int totalFrames)
    {
        terminalHelp.moveCursorHome();
        return (framesDisplayed < totalFrames && isPlaying);
    }
    private void printPerFrame(int framesDisplayed,int totalFrames,double actualFps)
    {
        System.out.printf("\033[%d;1H", height + 2); // Move below frame
        System.out.printf("Frame: %d/%d | Buffer: %d | Target FPS: %d | Actual FPS: %.1f | %s",
                        framesDisplayed, totalFrames, frameBuffer.size(), 
                        frameRate, actualFps,
                        useColor ? "COLOR" : "B&W");   
    }
    private long bookKeepingPerFrame(long startTime,int totalFrames,long frameStartTime)
    {
        long elapsed = System.currentTimeMillis() - startTime;
        double actualFps = framesDisplayed * 1000.0 / Math.max(elapsed, 1);
        printPerFrame(framesDisplayed,totalFrames,actualFps);
        long frameElapsed = System.currentTimeMillis() - frameStartTime;
        long sleepTime = frameDelayMs - frameElapsed;
        return sleepTime;
    }
    void sleepOrSkip(long sleepTime) throws InterruptedException
    {
        if (sleepTime > 0) 
        {
            Thread.sleep(sleepTime);
        } else if (sleepTime < -frameDelayMs) { 
            int framesToSkip = (int)(-sleepTime / frameDelayMs);
            for (int i = 0; i < framesToSkip && frameBuffer.size() > 0; i++) {
                frameBuffer.poll();
                framesDisplayed++;
            }
        }
    }
    public void play(String framesDirectory, int totalFrames) {
        framesDisplayed = helpstartTerminal();
        Thread producerThread = new Thread(new FrameProducer(framesDirectory, totalFrames));
        producerThread.start();
        long startTime = System.currentTimeMillis();
        
        try {
            while (continuePlaying(framesDisplayed,totalFrames)) {
                long frameStartTime = System.currentTimeMillis();
                Frame frame = frameBuffer.poll(5, TimeUnit.SECONDS);
                
                if (frame == null) {
                    System.err.println("\nBuffer timeout - waiting for frames...");
                    continue;
                }
                System.out.print(frame.content);
                framesDisplayed++;
                long sleepTime = bookKeepingPerFrame(startTime,totalFrames,frameStartTime);
                sleepOrSkip(sleepTime);
            }
            producerThread.join(5000);
            
        } catch (InterruptedException e) {
            System.err.println("\nPlayback interrupted");
            stop();
        } finally {
            isPlaying = false;
            terminalHelp.showCursor();
        }
        
        System.out.println("\n\n Playback complete!");
    }
    
    public void stop() {
        isPlaying = false;
    }
    
    public static void printHelp() {
        System.out.println("Usage: java AdvancedAsciiPlayer [options] <frames_dir> <total_frames>");
        System.out.println("\nOptions:");
        System.out.println("  -c, --color       Enable color ASCII output");
        System.out.println("  -d, --detailed    Use detailed ASCII character set");
        System.out.println("  -f, --fps <n>     Set target FPS (default: 24)");
        System.out.println("  -w, --width <n>   Set ASCII width (default: 120)");
        System.out.println("  -h, --height <n>  Set ASCII height (default: 40)");
        System.out.println("  --help            Show this help message");
        System.out.println("\nExamples:");
        System.out.println("  java AdvancedAsciiPlayer ./frames 300");
        System.out.println("  java AdvancedAsciiPlayer -c -f 30 ./frames 300");
        System.out.println("  java AdvancedAsciiPlayer -c -d -w 160 -h 50 ./frames 300");
    }
    public void showCursor()
    {
        this.terminalHelp.showCursor();
    }
}
