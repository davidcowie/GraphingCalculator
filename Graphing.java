import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by David on 3/10/2016.
 */
public class Graphing extends JPanel {

    private ArrayList<Point> points;
    private int xscale=25; // like the zoom
    private int yscale = 25;
    private ArrayList<Double> xpoints;
    private ArrayList<Double> ypoints;
    //maybe an ArrayList of arraylist if graphing multiple functions

    Functions functions;
    private int axisHeight, axisWidth;
    private int centerX, centerY;
    private String input = "";
    private boolean error;

    private final int DEFAULTBOUNDS = 10;

    public Graphing(){
        super();
        int SCREENWIDTH = 500, SCREENHEIGHT = 500;
        setPreferredSize(new Dimension(SCREENWIDTH,SCREENHEIGHT));
        centerX = SCREENWIDTH/2;
        centerY = SCREENHEIGHT/2;
        xpoints = new ArrayList<>();
        ypoints = new ArrayList<>();
        functions = new Functions();
        setBounds(DEFAULTBOUNDS, DEFAULTBOUNDS);
        //linearFunction(2, new Point(0, 2));
       // quadratic(1,5,6);
        //sin();
        String input = "x^2-2"; // thinking find the substring with the x then replace it in a for loop with the value to plot
        //graph(input);
       /* Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the equation:f(x) =");
        String input = scanner.nextLine();*/
       // graph(input);
        updatePaint();
    }
    public void graph(String input){
        clear();
        this.input = input;
        error = false;
        System.out.println("GRAPHING-input: " + input);
        //here i want to find the location of the x's in the input
        ArrayList<Integer> xpositions = new ArrayList<>();
        ArrayList<String> separatedFunction = new ArrayList<>();
        int lastStart = 0;
       /* for (int i = 0; i < input.length(); i++) {
            if(input.substring(i,i+1).equals("x")){
                xpositions.add(i);
                String partToAdd = "";
                if(i > 0){
                    char c = input.charAt(i-1);
                    if(c >= '0' && c <= '9'){
                        partToAdd = input.substring(lastStart,i) + "*";
                    }
                    else{
                        partToAdd = input.substring(lastStart,i);
                    }
                } else{
                    partToAdd = input.substring(lastStart,i);
                }
                separatedFunction.add(partToAdd);
                System.out.println(input.substring(lastStart, i));
                lastStart = i+1;
            }
        }

        separatedFunction.add(input.substring(lastStart));
        System.out.println(separatedFunction); */

        for (int j = 0; j < input.length(); j++) {
            if(j>0 &&input.substring(j,j+1).equals("x")){
                char c = input.charAt(j-1);
                if(c >='0' && c <= '9'){
                    input = input.substring(0,j) + "*" + input.substring(j);
                    //input.replace(j-1, input.substring(j-1,j) +"*");
                }
            }
        }
       // System.out.println("GRAPHING-input: " + input);

        ReversePolishNotation rpn = new ReversePolishNotation();
        double x,y=0;
        BigDecimal dec;
        for (double i = -axisWidth; i < axisWidth ; i+=0.1) {
            //System.out.println(i);
            dec = new BigDecimal(i);
            x =dec.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
           // System.out.println(x);
            //x=i;
            String strX = x +"";
            //String equation = "";
            //String newPart = x+"";
            String equ = input.replaceAll("x", roundAnswer(strX));
            //x= Double.parseDouble(roundAnswer(strX));
            //System.out.println("GRAPHING- equ: " + equ);
            Double result = rpn.calculate(equ);
            if(result == null){
                System.out.println("result is null in graph method");
                error = true;
                updatePaint();
                return;
            }else {
                y = result;
            }
            if(!(y> axisHeight || y < -axisHeight)){
                xpoints.add(x);
                ypoints.add(y);
            }
        }
        //setBounds();
        updatePaint();
        //repaint();
    }
    public String roundAnswer(String decimal) {
        int decimalIndex = decimal.indexOf(".");
        String postDecimal = decimal.substring(decimalIndex);
        if (postDecimal.length() > 4) {
            char toround = postDecimal.substring(5, 6).toCharArray()[0];
            char rounding = postDecimal.substring(4, 5).toCharArray()[0];
            if (toround >= '5') {
                rounding++;
            }
            String roundedAnswer = decimal.substring(0, decimalIndex + 4);
            return roundedAnswer;
        }
        return decimal;

    }
    //width and height should be the number of units want on the x and y axis
    public void setBounds(int width,int height){
        axisHeight = height;
        axisWidth = width;
        xscale = 250/width;
        yscale = 250/height;
    }

    public void setBounds(){
        axisHeight =(int) ymax() + 2;
        yscale = 250/axisHeight;

    }
    public double ymax(){
        if(ypoints.size() > 0) {
            double max = ypoints.get(0);
            for (int i = 0; i < ypoints.size(); i++) {
                if (ypoints.get(i) > max) {
                    max = ypoints.get(i);
                }
            }
            if(max > 10){
                return 10;
            }
            return max;
        }
        return 10;
    }
    public void ymin(){

    }


    private ArrayList calculateXintercepts(){
        ArrayList<Double> xintercepts = new ArrayList<>();
        for (int i = 0; i < ypoints.size()-1; i++) {
            if(ypoints.get(i).equals(0.0)){
                xintercepts.add(xpoints.get(i));
            }
            if((ypoints.get(i) < 0 && ypoints.get(i+1) > 0)|| (ypoints.get(i) >0 && ypoints.get(i+1) < 0)){
                xintercepts.add(xpoints.get(i));
            }

        }

       // System.out.println(xintercepts);
        return xintercepts;
    }
    private boolean showXIntercepts;
    //in future can also have the points painted on the green circles also
    public void setShowXIntercepts(boolean b){
        showXIntercepts = b;
        updatePaint();
    }
    private void clear(){
        xpoints.clear();
        ypoints.clear();
    }

    private BufferedImage paintImage = new BufferedImage(500,500,BufferedImage.TYPE_3BYTE_BGR);
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(paintImage, 0, 0, null);
        try {
            save();
        }catch (IOException e){
            System.out.print("IOEXCEPTION");
        }
    }

   // @Override
    public void updatePaint(){
        Graphics g = paintImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 500, 500);
        g.setColor(Color.BLACK);
        g.drawLine(250, 0, 250, 500);
        g.drawLine(0,250,500,250);

        // like 25 pixels = 1 number value-- if want 10 axis showing
        for (int i = 0; i < axisWidth; i++) {
            g.drawLine(250 + i * xscale, 240, 250 + i * xscale, 260);
            g.drawString((i + 1) + "", 245 + (i + 1) * xscale, 235);
            g.drawLine(250 + i * -xscale, 240, 250 + i * -xscale, 260);
            g.drawString((i + 1) + "", 245 + (i + 1) * -xscale, 235);

        }
        //setBounds();
        for (int i = 0; i < axisHeight; i++) {
            g.drawLine(240, 250 + i * -yscale, 260, 250 + i * -yscale);
            g.drawString((i + 1) + "", 232, 252 + (i + 1) * -yscale);
            g.drawLine(240, 250 + i * yscale, 260, 250 + i * yscale);
            g.drawString((i+1) + "", 235,245 + (i+1)*yscale);
        }

        //g.fillRect((int) (1.5 *25 + 250), 2*-25 + 250,1*25,1*25);

        // if there is an error then it will print a message and will not draw anything for the graph
        if(error){
            g.drawString("INVALID INPUT. PLEASE TRY AGAIN",150,150);
        }

        //drawing the points on the graph
        // have to center the coordinate system

        //scaleCoordinates();
        double[] x = new double[xpoints.size()];
        double[] y = new double[ypoints.size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = xpoints.get(i);
            y[i] = ypoints.get(i);
        }

        for (int i = 0; i < xpoints.size() - 1; i++) {
            System.out.println("x: " + xpoints.get(i) + " y: " + ypoints.get(i));
            g.drawLine((int)(x[i]*xscale)+250,(int)(-y[i]*yscale)+250,(int)(x[i+1]*xscale)+250,(int)(-y[i+1]*yscale)+250);
        }

        // draws the x intercepts
        if(showXIntercepts) {
            ArrayList<Double> xints = calculateXintercepts();
            double[] xintsd = new double[xints.size()];
            for (int i = 0; i < xints.size(); i++) {
                xintsd[i] = xints.get(i);
                g.setColor(Color.GREEN);
                g.fillOval((int) (xintsd[i] * xscale) + 250 - 3, 250 - 3, 3 * 2, 3 * 2);
            }
        }

        g.setColor(Color.BLACK);
        g.drawString(input,10,15);

        g.dispose();

        repaint();

    }
    public BufferedImage getGraphImage(){
        return paintImage;
    }

    private int graphNumber = 3;
    public void save() throws IOException {
        String graphName = "graph" + graphNumber+".png";
        System.out.println("saving.. " + graphName);
        ImageIO.write(paintImage, "PNG", new File(graphName));
        //graphNumber++;
    }


    public static void main(String[] args){
        JFrame f = new JFrame("GRaph");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setContentPane(new Graphing());
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }



    public void linearFunction(double m, Point b){ // slope and y intercept
        double x=b.x,y=b.y;
        for (int i = -axisWidth; i <axisWidth ; i++) {
            x = i;
            y = m*x + b.y;
            System.out.println("x: " + x + " y: " + y);
            xpoints.add(x);
            ypoints.add(y);
        }

    }
    public void sin(){
        double x,  y;
        for (double i = -axisWidth;i<axisWidth; i += 0.1) {
            x = i;
            y = Math.sin(i); // *10 is to scale it so the values are greater than 1
            // so if i label the axis then i need to shift that acordingly
            // System.out.println("x: " + x + " y: " + y);
            xpoints.add(x);
            ypoints.add(y);
        }
    }
    public void quadratic(double a, double b, double c){
        double[] roots = functions.quadraticEquation(a,b,c);
        double y,x;
        for (double i = -axisWidth; i < axisWidth; i+= 0.2) {
            x = i;
            y = a*Math.pow(x,2)+b*x + c;
            if(y > axisHeight || y < -axisHeight){
                continue;
            }
            xpoints.add(x);
            ypoints.add(y);
        }
    }

}
