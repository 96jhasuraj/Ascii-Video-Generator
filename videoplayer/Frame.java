package videoplayer;

public class Frame {
    String content;
    int frameNumber;
    long timestamp;
    
    Frame(String content, int frameNumber) {
        this.content = content;
        this.frameNumber = frameNumber;
        this.timestamp = System.currentTimeMillis();
    }
}
