import videoplayer.AdvancedAsciiPlayer;

public class VideoPlayer {

    int fps;
    int width;
    int height;
    boolean color ;
    boolean detailed;
    String framesDir;
    int totalFrames;
    AdvancedAsciiPlayer asciiplayer;
        
    private VideoPlayer()
    {
        fps = 24;
        width = 120;
        height = 40;
        color = false;
        detailed = false;
        framesDir = null;
        totalFrames = 0;
    }
    private void printConfig()
    {
        System.out.println("\nConfiguration:");
        System.out.println("  Frames Directory: " + framesDir);
        System.out.println("  Total Frames: " + totalFrames);
        System.out.println("  Target FPS: " + fps);
        System.out.println("  Resolution: " + width + "x" + height);
        System.out.println("  Color Mode: " + (color ? "ENABLED" : "DISABLED"));
        System.out.println("  Character Set: " + (detailed ? "DETAILED" : "SIMPLE"));
        System.out.println("\nStarting in 2 seconds... (Press Ctrl+C to stop)");
    }
    private void setVideoPlayer(String[] args)
    {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                case "--color":
                    color = true;
                    break;
                case "-d":
                case "--detailed":
                    detailed = true;
                    break;
                case "-f":
                case "--fps":
                    fps = Integer.parseInt(args[++i]);
                    break;
                case "-w":
                case "--width":
                    width = Integer.parseInt(args[++i]);
                    break;
                case "-h":
                case "--height":
                    height = Integer.parseInt(args[++i]);
                    break;
                case "--help":
                    AdvancedAsciiPlayer.printHelp();
                    return ;
                default:
                    if (framesDir == null) {
                        framesDir = args[i];
                    } else if (totalFrames == 0) {
                        totalFrames = Integer.parseInt(args[i]);
                    }
            }
        }
        asciiplayer =  new AdvancedAsciiPlayer(fps, width, height, color, detailed);
    }
    private void printIntro()
    {
        System.out.println("******************************************************");
        System.out.println("      ASCII VIDEO PLAYER                     ");
        System.out.println("******************************************************");
        printConfig();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return;
        }
    }
    void checkIfReady()
    {
        if (framesDir == null || totalFrames == 0) {
            AdvancedAsciiPlayer.printHelp();
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        
        VideoPlayer player = new VideoPlayer();
        player.setVideoPlayer(args);
        player.checkIfReady();
        player.printIntro();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            player.asciiplayer.stop();
            player.asciiplayer.showCursor();
            System.out.println("\n\nPlayback stopped.");
        }));
        player.asciiplayer.play(player.framesDir, player.totalFrames);
    }
}
