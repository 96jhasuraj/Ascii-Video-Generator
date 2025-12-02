package videoplayer;

public class TerminalHelper {
    void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    void moveCursorHome() {
        System.out.print("\033[H");
        System.out.flush();
    }
    
    void hideCursor() {
        System.out.print("\033[?25l");
        System.out.flush();
    }
    
    void showCursor() {
        System.out.print("\033[?25h");
        System.out.flush();
    }
}
