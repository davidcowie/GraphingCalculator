import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by David on 3/15/2016.
 */
public class Frame{

    public Graphing grapher;
    public TextInputPanel textInputPanel;
    public ImagePanel imagePanel;

    public static void main(String[] args){
       new Frame();


    }

    public Frame(){
        JFrame f = new JFrame("CALCULATOR");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        grapher = new Graphing();
        imagePanel = new ImagePanel(); // this order matters or null pointer in constructor of textinputpanel constructor
        textInputPanel = new TextInputPanel(this);

       /* if(textInputPanel.isReady()){
            grapher.graph(textInputPanel.getInput());
        }*/
        // JOptionPane.showMessageDialog(f,"the window just opened"); // this makes the dialog appear before the frame opens and wont open until click ok
        //menu stuff
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem menuItem = new JMenuItem("hello");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        fileMenu.add(menuItem);
        menuItem = new JMenuItem("setColor");
        JMenu subMenu = new JMenu("set color");
        subMenu.setMnemonic(KeyEvent.VK_C);
        menuItem = new JMenuItem("this is a item in sub menu");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        subMenu.add(menuItem);
        fileMenu.add(subMenu);

        //load save
        menuItem = new JMenuItem("Load save..");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textInputPanel.load();
            }
        });
        fileMenu.add(menuItem);
        // save menu item
        menuItem = new JMenuItem("Save..");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textInputPanel.save();
            }
        });
        fileMenu.add(menuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu functionsMenu = new JMenu("functions");
        JMenu algebraSubMenu = new JMenu("Polynomial");
        JMenuItem menuItem1 = new JMenuItem("Factor");
        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // set the text of the input thing to be "factor("
                textInputPanel.setText("factor(");
            }
        });
        algebraSubMenu.add(menuItem1);
        menuItem1 = new JMenuItem("Expand");
        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textInputPanel.setText("expand(");
            }
        });
        algebraSubMenu.add(menuItem1);
        menuItem1 = new JMenuItem("Derivitive"); // this will just be for a basic polynomial
        menuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textInputPanel.setText("deriv(");
                JOptionPane.showMessageDialog(f, "Eggs are not supposed to be green"); // cool
            }
        });
        algebraSubMenu.add(menuItem1);
        functionsMenu.add(algebraSubMenu);
        menuBar.add(fileMenu);
        menuBar.add(functionsMenu);
        f.setJMenuBar(menuBar);


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(imagePanel);
        //panel.add(new JMenuBar());
        //panel.add(textInputPanel);
        //f.add(panel);
        JPanel p = new JPanel();
        p.add(textInputPanel);
        JPanel largePanel = new JPanel();
        largePanel.setLayout(new BoxLayout(largePanel, BoxLayout.Y_AXIS));
        largePanel.add(panel);
        largePanel.add(p);
        f.add(largePanel);
        //f.add(p);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);

        int show;
        try{
            Scanner s = new Scanner(new File("dialog save.txt"));
            show = s.nextInt();
        }catch (Exception e){
            System.out.println("error reading file");
            System.out.println(e.getMessage());
            show = -1;
        }
        Object[] options = {"OK","OK and dont \nshow this message again"};
        if(show == -1 || show == 0) {
            //normal default dialog---- maybe add a button to not have the message shown again
           int a = JOptionPane.showOptionDialog(f, "This is a calculator that solves all normal math input \n But it is limited to only polynomial variable input. \n Otherwise it will not understand what you want","Start up",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,null); // this makes it appear after the window opens
           System.out.println("selected option: " + a); // 0 and 1
            if(a == 1){
                try{
                    PrintWriter pw = new PrintWriter(new FileOutputStream(new File("dialog save.txt")));
                    pw.println(1);
                    pw.close();
                }catch (Exception e){

                }
            }
           // JOptionPane.showMessageDialog(f,);
        }
        /// maybe make an option in the menu to show the start up dialog again just in case
        // custom title and warning icon
        //JOptionPane.showMessageDialog(f,"the window just opened","This is the title",JOptionPane.WARNING_MESSAGE);
       // String s = JOptionPane.showInputDialog(f,"this is an input dialog"); // this returns a string so can use that



     /*   JFrame f1 = new JFrame("INPUT");
        f1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f1.add(textInputPanel);
        f1.pack();
        f1.setVisible(true);*/


    }
}
