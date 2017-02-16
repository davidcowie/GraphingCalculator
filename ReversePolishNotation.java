import jdk.internal.org.objectweb.asm.tree.analysis.Interpreter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by David on 3/12/2016.
 */
public class ReversePolishNotation {

    private static final ArrayList<String> TRIG = new ArrayList<>();
    static {
        TRIG.add("sin");
        TRIG.add("cos");
        TRIG.add("tan");
        TRIG.add("csc");
        TRIG.add("cot");
        TRIG.add("sec");
    }
    private static final ArrayList<String> LOGS = new ArrayList<>();
    static {
        LOGS.add("ln");
        LOGS.add("log");
    }

    public static final int LEFT_ASSOC = 0;
    public static final int RIGHT_ASSOC = 1;

    //map<operation, int[pressedence, associativity]
    private static final Map<String, int[]> OPERATIONS = new HashMap<>();
    static {
        OPERATIONS.put("+",new int[] {0,LEFT_ASSOC});
        OPERATIONS.put("-",new int[]{0,LEFT_ASSOC});
        OPERATIONS.put("*",new int[]{5,LEFT_ASSOC});
        OPERATIONS.put("/",new int[] {5,LEFT_ASSOC});
        OPERATIONS.put("^",new int[]{10,RIGHT_ASSOC});
        OPERATIONS.put("sin",new int[]{ 15, LEFT_ASSOC});
        OPERATIONS.put("tan", new int[] { 15, LEFT_ASSOC});
        OPERATIONS.put("cos", new int[] { 15, LEFT_ASSOC});
        OPERATIONS.put("csc", new int[] { 15, LEFT_ASSOC});
        OPERATIONS.put("sqrt",new int[] { 15,LEFT_ASSOC});
        OPERATIONS.put("ln",new int[] { 15,LEFT_ASSOC});
        OPERATIONS.put("log",new int[] { 15,LEFT_ASSOC});
        OPERATIONS.put("abs",new int[] { 15, LEFT_ASSOC});
        OPERATIONS.put("cbrt",new int[] {15,LEFT_ASSOC}); // cubic root
        //OPERATIONS.put("todegrees",new int[] { 15,LEFT_ASSOC}); // still a work in progress
        // operations that require more than 1 argument ie need a comma
        // those should be handled separately and not allowed to be used with other things
    }

    // here i could create my own class(maybe the functions one)
    // and use reflection for that to create other functions not in the math class
    private static final ArrayList<String> FUNCTIONS = new ArrayList<>(); // things that take multiple arguments
    static {
        FUNCTIONS.add("hypot"); // hypot(x,y) returns sqrt(x^2+y^2)
        FUNCTIONS.add("max");
        FUNCTIONS.add("min");

    }
    private static boolean isFunction(String key){ return FUNCTIONS.contains(key);}

    private static boolean isOperator(String key){
        return OPERATIONS.containsKey(key);
    }

    private static boolean isAssociative(String token, int type){
        if(!isOperator(token)){
            throw new IllegalArgumentException("Invalid oporator!!");
        }
        if(OPERATIONS.get(token)[1] == type){
            return true;
        }
        return false;

    }

    /*
    will return 0 if same precedence
    negative if token1 has less precedence than token2
    positive otherwise
     */
    private static int comparePrecedence(String token1,String token2){
        if(!isOperator(token1)|| !isOperator(token2)){
            throw new IllegalArgumentException("ILLEGALLLLL PLZZZZ");
        }

        return OPERATIONS.get(token1)[0] - OPERATIONS.get(token2)[0];
    }

    private static String[] parse(String[] inputTokens){
        ArrayList<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>(); // will just be used for the operators
        for(String token: inputTokens){ // cycle through the equation
            System.out.println("the stack: " + stack);
            System.out.println("the output: " + output);
            if(isOperator(token)){
                while(!stack.empty() && isOperator(stack.peek())) {
                    // checking the associativitiy because it matters for the powers. the ^ has right assoc so itll solve it starting from the right if have 2^3^4^5 itll do 2^(3^(4^5))
                    if ((isAssociative(token,LEFT_ASSOC) && (comparePrecedence(token, stack.peek()) <= 0))|| (isAssociative(token,RIGHT_ASSOC) && (comparePrecedence(token,stack.peek())<0))) {
                        output.add(stack.pop());
                        continue;
                    }
                    break;
                }

                stack.push(token);
                //System.out.println(stack);
            }else if(token.equals("(")){
              /*  if(stack.peek().equals("^")){
                    output.add(stack.pop());
                }*/
                stack.push(token);
            }else if(token.equals(")")){
                while (!stack.empty() && !stack.peek().equals("(")){
                    System.out.println("reached end of parenthesis: " + stack.peek());
                    output.add(stack.pop());
                }
                stack.pop();
            }
            else {
                output.add(token);
            }

        }

        while (!stack.empty()){
            output.add(stack.pop());
        }

        String[] rpnoutput = new String[output.size()];
        return output.toArray(rpnoutput);
    }


    /*
    The power aspect does not work yet!!
    jk i did it!
     */

    public static void main(String args[])
    {
        String input11 = "3*(2+4*(5+22))+15";
        String input3 = "( 4 + 8 * ( 2 * 5 ) ) / ( 2 + ( 1+3 ) )";
        String input13 = "2*tan(45)";
        String[] input2 = input3.split(" ");
        String in = "x^2+4x+4";//"3*(2 ^(2))^3 + 1";//"sqrt(((6+2)*2 - (8/2))*12)";


        // remove all posible spaces within the input
        String s = input3.replaceAll(" ", "");
       // String s1 = s.replaceAll("pi","3.14");
        String[] tokens = createTokenArray(s);
        try {
            validateInput(tokens);
        }catch (IllegalArgumentException e){
            System.out.println("NOT VALID INPUT");
            System.exit(0);
        }
        // transform the input into RPN
        String[] rpnoutput = parse(tokens);
        System.out.print("RPN: ");
        for (int i = 0; i < rpnoutput.length; i++) {
            System.out.print(rpnoutput[i] + " ");
        }
        System.out.println();
        //Solve the RPN
        System.out.println(solveRPN(rpnoutput));
    }
    public Double calculate(String equation) throws IllegalArgumentException{
        String input = equation.replaceAll(" ", "");
        String input2 = input.replaceAll("pi","3.14");
        for (int i = 0; i < input2.length(); i++) {
            //System.out.println("inside the loopy");
            if(i +1 < input2.length() && input2.substring(i,i+1).equals(")") && input2.substring(i+1,i+2).equals("(")){ // if there are two parenthesis touching insert a multiplicaiton sign
                input2 = input2.substring(0,i+1) + "*" + input2.substring(i+1);
                i++;
            }
        }
        System.out.println("input with fixed back to back parenthesis: " + input2);
        input2 = input2.toLowerCase();
        String[] tokens = createTokenArray(input2);
        try {
            validateInput(tokens);
        }catch (IllegalArgumentException e){
            System.out.println("Tokens input are invalid");
            throw new IllegalArgumentException(e.getMessage());
            //return null;
        }
      /*  System.out.print("The equation with fixed negative signs: ");
        for (int i = 0; i < tokens.length; i++) {
            System.out.print(tokens[i] + "");
        }
        System.out.println();*/

        double result;
        if(isFunction(tokens[0])){
            // maybe reflection to another class here
            String[] newtokens = new String[tokens.length-3]; // this is to remove the function call and the parenthesis with it
            for (int i = 0; i <newtokens.length ; i++) {
                newtokens[i] = tokens[i+2];
            }
            hypot(newtokens);
        }else {
            //solves something that is not a function
            result = convertAndSolveRPN(tokens);
        }
        result = convertAndSolveRPN(tokens);

        return result;
    }
    private Double hypot(String[] tokens){
        tokens[tokens.length-1] = null; // wamt to get rid of the end parenthesis


        return 5.0;
    }
    private Double hypot(double x,double y){

        return 5.0;
    }
    private Double convertAndSolveRPN(String[] tokens){
        //transform the input into RPN
        String[] rpnoutput = parse(tokens);
        System.out.print("RPN: ");
        for (int i = 0; i < rpnoutput.length; i++) {
            System.out.print(rpnoutput[i] + " ");
        }
        System.out.println();
        return solveRPN(rpnoutput);
    }
    //if passing in a variable but this isnt done or used
    public Double calculate(String equation,char var){
        String input = equation.replaceAll(" ", "");
        String[] tokens = createTokenArray(input);
        try {
            validateInput(tokens);
        }catch (IllegalArgumentException e){
            return null;
        }
      /*  System.out.print("The equation with fixed negative signs: ");
        for (int i = 0; i < tokens.length; i++) {
            System.out.print(tokens[i] + "");
        }
        System.out.println();*/
        //transform the input into RPN
        String[] rpnoutput = parse(tokens);
       /* System.out.print("RPN: ");
        for (int i = 0; i < rpnoutput.length; i++) {
            System.out.print(rpnoutput[i] + " ");
        }
        System.out.println();*/
        return solveRPN(rpnoutput);
    }

    //maybe easier to validate the token array instead of the string
    private static void validateInput(String[] tokens) throws IllegalArgumentException{

        for (int i = 0; i < tokens.length; i++) {
            if(!(isOperator(tokens[i]) || isNumber(tokens[i]) )){
                throw new IllegalArgumentException("INVALID TOKENS");
                //System.out.println("BOTH ERROR");
            }
           /* if(!isOperator(tokens[i])){
                System.out.println("OPORATOR error" + tokens[i]);
            }
            if(!isNumber(tokens[i])){
                System.out.println("NUMBER ERROR" + tokens[i]);
            }*/
        }
    }
    private static boolean isNumber(String s){
        for (int i = 0; i < s.length(); i++) {
            char a = s.charAt(i);
            if(!((a>='0' && a <= '9') || a == '.' || a == '(' || a == ')')){
                return false;
            }
        }
        return true;
    }

    //maybe for pi-- if the user types in pi i replace it in the string with 3.14
    // then it is treated as a number througout then when solving if value = 3.14 then switch with Math.PI

    private static String[] createTokenArray(String s){
        char[] c = s.toCharArray(); // issue with this is if have a greater than 2 digit number

        ArrayList<String> equ = new ArrayList<>();
        for (int i = 0; i < c.length; i++) {
            //System.out.println(c[i]);
            //if it is a number is checks for if it is a greater than one digit number
            if ((c[i] >= '0' && c[i] <= '9')) {
                String number = String.valueOf(c[i]);
                int j = i + 1;
                while (j < c.length && ((c[j] >= '0' && c[j] <= '9')|| c[j] == '.')) {
                    number += c[j];
                    j++;
                }
                i = j - 1;
                equ.add(number);
            } else if (c[i] >= 'a' && c[i] <= 'z') {
                String op = c[i] + "";
                int j = i + 1;
                while (j < c.length && (c[j] >= 'a' && c[j] <= 'z')) {
                    op += c[j];
                    j++;
                }
                i = j - 1;
                equ.add(op);
                //checking if there is a negative sign for a number
            }else if(c[i] == '-'){
                    if(i ==0 || !(c[i-1] >= '0' && c[i-1] <= '9')){ // if the previous character is not a number
                        String number = c[i+1] +"";
                        int w = i+2;
                        while(w < c.length && ((c[w] >= '0' && c[w] <= '9')||c[w] == '.')){
                            number += c[w];
                            w++;
                        }
                        i = w-1;
                        String add = "(0-" + number + ")"; // but what if it is a more than one digit number
                        //char[] chars = add.toCharArray();
                        String[] stuff = createTokenArray(add); // yayy recursion
                        for (int j = 0; j < stuff.length; j++) {
                            equ.add(stuff[j]+"");
                        }
                    }else {
                        equ.add(c[i] +"");
                    }
            }
            else {
                equ.add(c[i] + "");
            }
        }
        System.out.println(equ);
        String[] ttt = new String[equ.size()];
        return equ.toArray(ttt);
    }

    //solving reverse polish notation
    public static Double solveRPN(String[] t){
        ArrayList<String> tokens = new ArrayList<>();
        for (int i = 0; i < t.length; i++) {
            tokens.add(t[i]);
        }
        boolean error = false;
        for (int i = 0; i < tokens.size(); i++) {
            if(isOperator(tokens.get(i)) && !TRIG.contains(tokens.get(i)) && !tokens.get(i).equals("sqrt") && !LOGS.contains(tokens.get(i)) && !tokens.get(i).equals("abs")) {
                double value1;
                double value2;
                if(tokens.get(i-2).equals("pi") || tokens.get(i-2).equals("3.14")){
                    value1 = Math.PI;
                }else{
                    value1 = Double.parseDouble(tokens.get(i-2));
                }
                if(tokens.get(i-1).equals("pi") || tokens.get(i-1).equals("3.14")){
                    value2 = Math.PI;
                }else{
                    value2 = Double.parseDouble(tokens.get(i-1));
                }
                double result=0;
                if(tokens.get(i).equals("+")){
                    result = value1+value2;
                }else if(tokens.get(i).equals("-")){
                    result = value1 - value2;
                }else if(tokens.get(i).equals("*")){
                    result = value1*value2;
                }else if(tokens.get(i).equals("/")){
                    result = value1/value2;
                }else if(tokens.get(i).equals("^")){
                    result = Math.pow(value1,value2);
                }
                tokens.remove(i);
                tokens.remove(i-1);
                tokens.set(i - 2, result + "");
                i = i-2;
            }else if(isOperator(tokens.get(i))){
                double value1;
                if(tokens.get(i-1).equals("pi")){
                    value1 = Math.PI;
                }else {
                    try {
                        value1 = Double.parseDouble(tokens.get(i - 1));
                    }catch (NumberFormatException e){
                        error = true;
                        break;
                    }
                }
                double result =0;

                try {
                    Method method = Math.class.getMethod(tokens.get(i),double.class);
                    result = (double) method.invoke(null,value1);
                } catch (Exception e) {
                    if(tokens.get(i).equals("ln")){
                        result = Math.log(value1);
                    }else if(tokens.get(i).equals("log")){
                        result = Math.log10(value1);
                    }else {
                        e.printStackTrace();
                    }
                }
               /* if(tokens.get(i).equals("sin")){
                    if(tokens.get(i-1).equals("3.14")){ // I could do this to account for the special angles
                        result = 0;
                    }else {
                        result = Math.sin(value1);
                    }
                }else if(tokens.get(i).equals("cos")){
                    result = Math.cos(value1);
                }else if(tokens.get(i).equals("tan")){
                    result = Math.tan(value1);
                }else if(tokens.get(i).equals("csc")){
                    result = 1/Math.sin(value1);
                }else if(tokens.get(i).equals("sqrt")){
                    result = Math.sqrt(value1);
                }else if(tokens.get(i).equals("ln")){
                    result = Math.log(value1);
                }else if(tokens.get(i).equals("log")){
                    result = Math.log10(value1);
                }else if(tokens.get(i).equals("abs")){
                    result = Math.abs(value1);
                }*/
                tokens.remove(i);
                tokens.set(i-1,result+"");
                i=i-1;
            }
          //  System.out.println("Solving the RPN: " + tokens);
        }
        if(error){
            return null;
        }
        return Double.parseDouble(tokens.get(0));
    }

    public static void stackExample(){
        // Create a new, empty stack
        Stack lifo = new Stack();

        // Let's add some items to it
        for (int i = 1; i <= 10; i++)
        {
            lifo.push ( new Integer(i) );
        }

        // Last in first out means reverse order
        while ( !lifo.empty() )
        {
            System.out.print ( lifo.pop() );
            System.out.print ( ',' );
        }

        // Empty, let's lift off!
        System.out.println (" LIFT-OFF!");
    }
}
