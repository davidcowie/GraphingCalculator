import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by David on 3/21/2016.
 */
public class ImagePanel extends JPanel {

    BufferedImage image;

    public ImagePanel(){
        super();
        setPreferredSize(new Dimension(500,500));
        setBackground(Color.BLUE);
    }

    public void paint(Graphics g){
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, 500, 500);
        g.drawImage(image, 0, 0, null);
    }

    public void setImage(BufferedImage image){
        this.image = image;
        repaint();
    }
    public void setImage(String fileName){
        BufferedImage i = null;
        try {
            i = ImageIO.read(new File(fileName));
        }catch (Exception e){}

        this.image = i;
        repaint();

    }
    public BufferedImage getImage(){
        return image;
    }
    //could have if for if graphing save image
    // if calc then save a text file
    // then maybe i can get the save and set the image
    public void save(){
        try{
            ImageIO.write(image,"png",new File("calc output.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadSave(){
        try{
           image = ImageIO.read(new File("calc output.png"));
        }catch (Exception e){}
        repaint();
    }
}
