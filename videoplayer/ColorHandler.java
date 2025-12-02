package videoplayer;
import java.awt.image.BufferedImage;
import java.awt.Color;
public class ColorHandler {
    boolean useColor;
    private static final String ASCII_DETAILED = " .'`^\",:;Il!i><~+_-?][}{1)(|\\/123456789abcdefghijk0OZmwqpdbkhao*#MW&8%B@$";
    private static final String ASCII_SIMPLE = " .:-=+*#%@";
    private String asciiChars;
    public ColorHandler(boolean useColor,boolean detailedChars)
    {
        this.useColor=useColor;
        this.asciiChars = detailedChars ? ASCII_DETAILED : ASCII_SIMPLE;
    }
    private void colorChar(int x , int y , StringBuilder sb,Color pixel)
    {
        int gray = (int)(0.299 * pixel.getRed() + 
                        0.587 * pixel.getGreen() + 
                        0.114 * pixel.getBlue());
        
        int charIndex = (gray * (asciiChars.length() - 1)) / 255;
        char asciiChar = asciiChars.charAt(charIndex);
        
        if (useColor) {
            addColoredAscii(sb,pixel,asciiChar);
            sb.append("\033[0m"); // Reset color
        } else {
            sb.append(asciiChar);
        }
    }
    private void addColoredAscii(StringBuilder sb,Color pixel,char asciiChar)
    {
        int r = pixel.getRed() * 5 / 255;
        int g = pixel.getGreen() * 5 / 255;
        int b = pixel.getBlue() * 5 / 255;
        int colorCode = 16 + (36 * r) + (6 * g) + b;
        sb.append(String.format("\033[38;5;%dm%c", colorCode, asciiChar));
    }
    private String imageToColoredAscii(BufferedImage image) {
        StringBuilder ascii = new StringBuilder();
        int width = image.getWidth();
        int height = image.getHeight();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = new Color(image.getRGB(x, y));
                colorChar(x,y,ascii,pixel);
            }
            ascii.append('\n');
        }
        
        return ascii.toString();
    }
    

    public String imageToAscii(BufferedImage image) {
        return imageToColoredAscii(image);
    }
}
