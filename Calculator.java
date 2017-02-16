import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by David on 2/27/2016.
 */
public class Calculator extends JPanel {

    private JTextField field;
    private JLabel label;
    private JButton decToFracButton;

    private boolean isdec;
    private String answer;
    private String inputEquation;

    private ArrayList<String> fileInput = new ArrayList<>();

    public Calculator(){
        super();
        setPreferredSize(new Dimension(500, 500));
        writeFile("Calculator has started.");
        ReversePolishNotation RPN = new ReversePolishNotation();
        answer = "0";
        label = new JLabel();
        field = new JTextField("enter equation",25);
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String equation = field.getText();
                inputEquation = equation;
                fileInput.add(equation);
                appendFile(equation);
                answer = RPN.calculate(equation)+"";
                fileInput.add(answer);
                appendFile(answer);
                label.setText(answer + "");
                isdec = true;
                repaint();
            }
        });
        decToFracButton = new JButton("Dec--> Frac");
        decToFracButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isdec) {
                    //String stdecimal = label.getText();
                    for (int i = 0; i < answer.length(); i++) {
                        if(answer.substring(i,i+1).equals(".")){
                            double sanswer = Double.parseDouble(answer);
                            Rational r = new Rational(sanswer);
                            label.setText(r.toString());
                            isdec = false;
                            repaint();
                            break;
                        }
                    }

                }
            }
        });
        setLayout(new FlowLayout());
        add(field);
        add(label);
        add(decToFracButton);
    }


    /*@Override
    public void paint(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 100, 500, 400);
        g.setColor(Color.WHITE);
        g.fillRect(250, 250, 50, 50);
        g.setColor(Color.GREEN);
        g.drawString(answer,260,260);
    }*/


    public void appendFile(String s){
        PrintWriter pw = null;
        try{
            pw = new PrintWriter(new FileOutputStream("Calc Input",true));
        }catch (Exception e){
            System.out.println("APPENDING FILE ERROR");
        }

        pw.println(s);

        pw.close();
    }

    public void writeFile(String s){
        PrintWriter pw = null;
        try{
            pw = new PrintWriter(new File("Calc Input"));
        }catch (Exception e){
            System.out.println("WRITING FILE ERROR");
        }
       /* for (int i = 0; i < fileInput.size(); i++) {
            pw.println(fileInput.get(i));
        }*/
        pw.println(s);
        pw.close();
    }

    public String getInputEquation(){
        return inputEquation;
    }
    public String getAnswer(){
        return answer;
    }

    public static void main(String[] args){
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
        f.setContentPane(new Calculator());
       // f.add(container);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
    }

}


