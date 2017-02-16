import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by David on 3/22/2016.
 */
public class GraphicalCalculator extends JPanel implements Runnable{
    public final int SCREENWIDTH=500,SCREENHEIGHT = 500;
    private Calculator calc;
    private ReversePolishNotation RPN;
    private BufferedImage paintImage = new BufferedImage(500,500,BufferedImage.TYPE_3BYTE_BGR);
    private String[] inputs;
    private String[] answers;
    private int curSize = 5;

    public GraphicalCalculator(){
        super();
        setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));
        calc = new Calculator();
        RPN = new ReversePolishNotation();

        // the size is how many results will stay on the screen
        inputs = new String[5];
        answers = new String[5];
        /*for (int i = 0; i < inputs.length-2; i++) {
            inputs[i] = "15+78*2";
            answers[i] = "42";
        }*/
        updatePaint();
    }
    public Thread thread;
    public void addNotify(){
        super.addNotify();
        if(thread == null){
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(paintImage, 0, 0, null);
    }
    public void updatePaint(){
        Graphics g = paintImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, SCREENWIDTH,SCREENHEIGHT);

        //System.out.println(calc.getInputEquation());
        //g.setColor(Color.BLACK);
        for (int i = 0; i < inputs.length; i++) {
            if(i%2==0){
               // g.setColor(Color.GRAY);
                g.setColor(new Color(0,0,0,34));
            }else {
                g.setColor(Color.WHITE);
            }
            g.fillRect(0, (SCREENWIDTH/5) * i, SCREENWIDTH, (SCREENHEIGHT/5) + (SCREENHEIGHT/5) * i);
            g.setColor(Color.BLACK);
          //  g.setFont(new Font("Century Gothic",16,Font.BOLD));
            if(inputs[i] != null) {
                g.drawString(inputs[i], (int)(SCREENWIDTH*0.04), (int)(SCREENHEIGHT*.05)+ i * (SCREENHEIGHT/5));
            }
            if(answers[i] != null ) {
                int length = g.getFontMetrics().stringWidth(answers[i]);
                if(answers[i].contains(".")&& !answers[i].contains("x")){ // just for the facotring otherwise it will cut it off
                        String roundedAnswer = roundAnswer(answers[i]);
                        length = g.getFontMetrics().stringWidth(roundedAnswer);

                        g.drawString(roundedAnswer, (int)(0.95*SCREENWIDTH)-length, (int)(SCREENHEIGHT*0.1) + i * (SCREENHEIGHT/5));


                }else {
                    g.drawString(answers[i], (int)(0.95*SCREENWIDTH)-length, (int)(SCREENHEIGHT*0.1) + i * (SCREENHEIGHT/5));
                }
            }
        }

        g.dispose();
        repaint();
    }

    private int digitsToRound = 5;
    public String roundAnswer(String decimal) {
        int decimalIndex = decimal.indexOf(".");
        String postDecimal = decimal.substring(decimalIndex);
        char rounding;
        if (postDecimal.length() > digitsToRound) {
            char toround = postDecimal.substring(digitsToRound, digitsToRound+1).toCharArray()[0];
             rounding = postDecimal.substring(digitsToRound-1, digitsToRound).toCharArray()[0];
            if (toround >= '5') {
                rounding++;
            }
            String roundedAnswer = decimal.substring(0, decimalIndex + digitsToRound-1)+rounding;
            return roundedAnswer;
        }
        return decimal;

    }
    //could have a button that sets this
    public void setDigitsToRound(int n){
        digitsToRound = n;
    }

    private static ArrayList<String> FUNCTIONS = new ArrayList<>();
    static {
        FUNCTIONS.add("factor");
        FUNCTIONS.add("expand"); // will foil out expressions
    }
    VariableExpressions varExpres = new VariableExpressions();
    Expression expression = new Expression();
    public void addInput(String s){
        String result = null;
        if(s.contains("x")) {
            if (s.contains("factor(")) { // need to take out the factor and the last paraenthesis
                if (s.substring(0, 1).equals("f") && s.substring(s.length() - 1, s.length()).equals(")")) {
                    String s2 = s.replace("factor(", "");
                    s2 = s2.substring(0, s2.length() - 1);
                    //  result = varExpres.factor(s2);
                    // maybe call foil first to simplify then create a new expression with that string then factor
                    expression = new Expression();
                    String simplified = expression.factor(s2);
                    result = simplified;
                    //String simplified = expression.foil(s2);
                    //expression = new Expression(simplified);
                    //result = expression.factor(expression.getTerms());
                } else {
                    result = "INVALID INPUT";
                }
            }else if(s.contains("expand(")){
                /////////// STILL NEED TO MAKE USER FRIENDLY/////////////////
                String s2 = s.replace("expand(","");
                s2 = s2.substring(0,s2.length()-1); // to remove the last parenthesis
                result = expression.foil(s2);
            }else if(s.contains("expandPower(")){
                String s2 = s.replace("expandPower(", "");
                s2 = s2.substring(0,s2.length() - 1);
                result = "DO THIS";
                //TODO make method in Expression.java that is expand power
                // it will use the multinomial theorem.
                // should do either (x+y)^3 or (x+2)^4 or (a+b+c+...+n)^r;
                // so need to look inside of parenthesis and be able to find and identify more than one variable.

            } else if(s.contains("deriv(")){
                String s2 = s.replace("derivitive(","");
                s2 = s2.substring(0,s2.length() -1);
                Expression e = new Expression(s2);
                result = e.derivitive();
            }
            else if (s.contains("hypot")) {
                if (s.substring(0, 1).equals("h") && s.substring(s.length() - 1, s.length()).equals(")")) {
                    String method = s.substring(0, s.indexOf("("));
                    System.out.println("method string: " + method);
                    String input = s.substring(s.indexOf("(") + 1, s.length() - 1);
                    System.out.println("inside hypot and this is the input string: " + input);
                    String input1 = input.substring(0, input.indexOf(","));
                    String input2 = input.substring(input.indexOf(",") + 1);
                    log("input 1: " + input1 + " input 2: " + input2);
                    double val1 = RPN.calculate(input1);
                    double val2 = RPN.calculate(input2);
                    result = Math.hypot(val1, val2) + "";
                } else {
                    result = "INVALID INPUT";
                }
            }
        }
        else {
            try {
                result = RPN.calculate(s) + "";
            }catch (IllegalArgumentException e){
                result = "INVALID INPUT";
                System.out.println(e.getMessage());
                JOptionPane.showMessageDialog(this, "Syntax error on input");
                String responce = JOptionPane.showInputDialog(this,"POP QUIZ \n Cause you messed up. \n What is 2 + 2?","Pop Quiz Hot Shot",JOptionPane.INFORMATION_MESSAGE);
                if(!responce.equals("4")){
                    System.exit(0);
                }
            }
            /*System.out.println("result after rpn calculate: " + result);
            if(result.equals(null)){
                System.out.println("the result is null");
            }
            if(result.equals("")){
                log("result is empty string");
            }
            if(result == null){
                log("result == null");
            }*/
        }
        //check if the input is even valid
        if(result == null || result.equals(null)){
            System.out.println("RESULT IS NULL");
        }
        if(curSize < inputs.length){
            System.out.println("less than length");
            inputs[curSize] = s;
            if(result == null){
                answers[curSize] = "INVALID INPUT";
            }else {
                answers[curSize] = result + "";
            }
            curSize++;
        }else{
            for (int i = 0; i <inputs.length-1; i++) {
                inputs[i] = inputs[i +1];
                answers[i] = answers[i+1];
            }
            inputs[inputs.length-1] = s;
            if(result == null){
                answers[inputs.length-1] = "INVALID INPUT";
            }else {
                answers[inputs.length - 1] = result + "";
            }
        }
        if(result== null){
            JOptionPane.showInputDialog(this,"error");
        }
        updatePaint();
    }
    public void convertToFraction(){
        if(curSize < inputs.length){
            inputs[curSize] = answers[curSize-1];
            Rational r = new Rational(Double.parseDouble(answers[curSize-1]));
            answers[curSize] =  r.toString();
            curSize++;
        }else{
            for (int i = 0; i <inputs.length-1; i++) {
                inputs[i] = inputs[i +1];
                answers[i] = answers[i+1];
            }
            inputs[inputs.length-1] = answers[curSize-1];
            Rational r = new Rational(Double.parseDouble(answers[curSize-1]));
            answers[inputs.length-1] =  r.toString();
        }
        updatePaint();
        //repaint();
    }
    // to return the painted image to set the imagepanel to this image
    public BufferedImage getPaintImage(){
        return paintImage;
    }
    //for like the ans button
    public String getMostRecentAnswer(){
        return answers[answers.length-1];
    }

    //save the inputs and answers currently on the screen
    public void saveInputs(){
        PrintWriter pw = null;
        try{
            pw = new PrintWriter(new FileOutputStream(new File("Calc Input.txt")));
        }catch (Exception e){
            System.out.println("saving INputs error");
        }
        for(String s: inputs){
            pw.println(s);
        }
        for(String s: answers){
            pw.println(s);
        }
        pw.close();
    }
    public void loadSave(){
        Scanner s = null;
        try{
            s = new Scanner(new File("Calc Input.txt"));
        }catch (Exception e){
            System.out.println("ERROR READING SAVE");
        }
        log("Scanner created");
        int i =0;
        while(s.hasNextLine()){
            if(i<5){
                inputs[i] = s.nextLine();
                log("inputs: " + inputs[i]);
            }else{
                answers[i-5] = s.nextLine();
                log("answers: " + answers[i-5]);
            }
            i++;
        }
        s.close();
        updatePaint();
        updatePaint();
        repaint();
    }
    public void log(String s){
        System.out.println(s);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("CALCULATOR");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         /*   Calculator c = new Calculator();
            JPanel container = new JPanel();
            //container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
            container.setLayout(new GridLayout(2,1));


            container.add(c);
            JPanel p = new JPanel();
            p.setPreferredSize(new Dimension(100,100));
            p.setBackground(Color.BLUE);
            container.add(p);*/
        f.setContentPane(new GraphicalCalculator());
        // f.add(container);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }

    @Override
    public void run() {

        while(true) {

            update();
            updatePaint();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
    }
    int time;
    public void update(){
        time++;
        if(time%100==0){
            if(curSize < inputs.length){
                inputs[curSize] = "53454352 ";
                answers[curSize] = "420";
                curSize++;
            }else{
                for (int i = 0; i <inputs.length-1; i++) {
                    //String s = inputs[i];
                    //inputs[i] = inputs[i+1];
                    inputs[i] = inputs[i +1];
                    answers[i] = answers[i+1];
                }
                inputs[inputs.length-1] = "helloooo";
                answers[answers.length-1] = "ansdfaskdl";
            }
        }
    }
}
