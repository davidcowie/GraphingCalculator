import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by David on 3/15/2016.
 */
public class TextInputPanel extends JPanel {

    private JTextField textField;
    private String input;
    private boolean ready = false;
    private Graphing graphing;
    private boolean isGraphing = false;
    private GraphicalCalculator calculator;
    private JButton switchMode;
    private JButton decToFracButton;
    private boolean isdec;
    private JButton xIntercepts;
    private Frame frame;

    private VariableExpressions varExpres;

    public TextInputPanel(Frame frame){
        super();
        setPreferredSize(new Dimension(500, 100));
        this.frame = frame;
        graphing = new Graphing();
        calculator = new GraphicalCalculator();
        varExpres = new VariableExpressions();
        textField = new JTextField("enter equation",20);
        switchMode = new JButton("switch to graphing or calc");
        decToFracButton = new JButton("Dec->Frac");
        xIntercepts = new JButton("xintercepts");
        if(isGraphing){
            frame.imagePanel.setImage(graphing.getGraphImage());
        }else{
            frame.imagePanel.setImage(calculator.getPaintImage());
        }
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input = textField.getText();
                textField.setText("");
                System.out.println("original input: "   + input);
                if(isGraphing) {
                    graphing.graph(input);
                    ready = true;
                    frame.imagePanel.setImage(graphing.getGraphImage());
                }else{
                    calculator.addInput(input);
                    frame.imagePanel.setImage(calculator.getPaintImage());
                    isdec = true;
                }

             // frame.grapher.graph(input);
            }
        });
        switchMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isGraphing){
                    isGraphing = false;
                    frame.imagePanel.setImage(calculator.getPaintImage());
                    decToFracButton.setText("Dec->Frac"); // now can have less buttons that only show up if on the correct screen
                }else{
                    isGraphing = true;
                    frame.imagePanel.setImage(graphing.getGraphImage());
                    decToFracButton.setText("xintercepts");
                }
            }
        });
        decToFracButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isdec && !isGraphing) {
                    // double sanswer = Double.parseDouble(answer);
                    // Rational r = new Rational(sanswer);
                    calculator.convertToFraction();
                    // calculator.addInput("");
                    frame.imagePanel.setImage(calculator.getPaintImage());
                    isdec = false;
                }else if(decToFracButton.getText().equals("xintercepts")){ // then it is the xintercepts button
                    graphing.setShowXIntercepts(true);
                    frame.imagePanel.setImage(graphing.getGraphImage());
                }

            }
        });

        add(textField);
        add(switchMode);
        add(decToFracButton);
    }

    public boolean isGraphing(){
        return isGraphing;
    }

   // public String getAnswer(){return an}
    public String getInput(){
        return input;
    }
    public boolean isReady(){
        return ready;
    }

    public void save(){
        if(!isGraphing){
            calculator.saveInputs();
        }else{
            frame.imagePanel.save();
        }
    }
    public void load(){
        if(!isGraphing){
            calculator.loadSave();
            frame.imagePanel.setImage(calculator.getPaintImage());
        }else{
            frame.imagePanel.loadSave();
           // frame.imagePanel.setImage(graphing.getGraphImage());
        }
    }
    public void setText(String s){
        textField.setText(s);
    }
    public void setIsdec(boolean b){
        isdec = b;
    }
}
