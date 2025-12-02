package utility;
import java.io.*;
public class ExitCode
{
    private int code;
    public ExitCode(int x)
    {
        code = x;
    }
    private int printSucessMessage(File outDir)
    {
        int frameCount = outDir.listFiles(
            (dir, name) -> name.endsWith(".jpg")
        ).length;
        System.out.println("Successfully extracted " + frameCount + " frames");
        return frameCount;
    }
    private int printFailureMessage()
    {
        System.err.println("FFmpeg exited with code: " + this.code);
        return -1;
    }
    public int printMessage(File outputDirectory)
    {
        if(this.code==0)
            return this.printSucessMessage(outputDirectory);
        else return this.printFailureMessage();
    }
}