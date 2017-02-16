import java.util.ArrayList;

/**
 * Created by David on 2/25/2016.
 */
public class Main {

    public static void main(String[] args){
      /*  String equation = "((15-3)*2)*(8-3)";
        ArrayList<String> parts = new ArrayList<>();
        int closeParenIndex;
        for (int i = 0; i < equation.length(); i++) {
            if(equation.substring(i,i+1).equals("(")){
                int firstClose=i;
                for (int j = i+1; j <equation.length() ; j++) {

                    if(equation.substring(j,j+1).equals("(")){
                        for (int k = j+1; k < equation.length(); k++) {
                            if(equation.substring(k,k+1).equals(")")){
                                parts.add(equation.substring(j+1,k));
                                j=k;
                                break;
                            }
                        }
                        firstClose = j+1;
                    }else if(equation.substring(j,j+1).equals(")")){
                        parts.add(equation.substring(firstClose+1,j));
                        i=j;
                        break;
                    }
                }
            }else{
                if(equation.substring(i,i+1).equals("*")||equation.substring(i,i+1).equals("/")||equation.substring(i,i+1).equals("+")||equation.substring(i,i+1).equals("-")){
                    parts.add(equation.substring(i,i+1));
                }else{
                    int numDigits = check(i,equation);
                    parts.add(equation.substring(i,i+numDigits));
                    i = i+numDigits-1;
                }

            }
            System.out.println(equation.substring(i,i+1));
        }

        ArrayList<String> symbols = new ArrayList<>();
        ArrayList<Double> numbers = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            if(parts.get(i).substring(0, 1).equals("*")||parts.get(i).substring(0,1).equals("/")||parts.get(i).substring(0, 1).equals("+")||parts.get(i).substring(0, 1).equals("-")){
                symbols.add(parts.get(i));
            }else{
               // solve(parts.get(i));
                try{
                    double num = Double.parseDouble(parts.get(i));
                    numbers.add(num);
                }catch (Exception e){
                    numbers.add(solve(parts.get(i)));
                }
            }
        }
        System.out.println("parts " + parts);
        System.out.println("numbers " +numbers);
        System.out.println("symbols " + symbols);

        for (int i = 0; i < symbols.size(); i++) {
            numbers.set(0, solve(numbers.get(0) + symbols.get(i) + numbers.get(1)));
            numbers.remove(1);
        }
        System.out.println("numbers " +numbers);
        //solve("5*3*5");
*/
        Calculator calc = new Calculator();
       // System.out.println(calc.solve("5+8"));
        Functions f = new Functions();
        System.out.println(f.quadraticEquation(1,5,6)[0]);

    }

    public static Double solve(String s){
        ArrayList<Integer> operatorIndex = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            if(s.substring(i,i+1).equals("*")||s.substring(i,i+1).equals("/")||s.substring(i,i+1).equals("+")||s.substring(i,i+1).equals("-")){
                // if true then that means it is more than just an number and needs to be simplified
                operatorIndex.add(i);
            }
        }
        ArrayList<Double> nums = new ArrayList<>();
        int j =0;
        for (int i = 0; i < s.length(); i++) {
            if(j >= operatorIndex.size()&& operatorIndex.size() >=1){
                nums.add(Double.parseDouble(s.substring(operatorIndex.get(operatorIndex.size() - 1)+1)));
            }else {
                System.out.println(j);
                nums.add(Double.parseDouble(s.substring(i, operatorIndex.get(j))));
                i = operatorIndex.get(j);
                j++;
            }

        }
        double result =0;
        for (int i = 0; i < operatorIndex.size(); i++) {
            if(s.substring(operatorIndex.get(i),operatorIndex.get(i)+1).equals("-")){
                nums.set(0,nums.get(0) - nums.get(1));
            }else  if(s.substring(operatorIndex.get(i),operatorIndex.get(i)+1).equals("+")){
                nums.set(0,nums.get(0) + nums.get(1));
            }else  if(s.substring(operatorIndex.get(i),operatorIndex.get(i)+1).equals("*")){
                nums.set(0,nums.get(0) * nums.get(1));
            }else  if(s.substring(operatorIndex.get(i),operatorIndex.get(i)+1).equals("/")){
                nums.set(0,nums.get(0) / nums.get(1));
            }

            nums.remove(1);
        }
        return nums.get(0);
    }
    //will return the index where the next symbol is after a number-- to check if have a 2 or greater digit number
    public static int check(int j, String s){
        int numDigits=0;

        for (int i = j; i < s.length(); i++) {
            if(s.substring(i,i+1).equals("*")||s.substring(i,i+1).equals("/")||s.substring(i,i+1).equals("+")||s.substring(i,i+1).equals("-")){
                break;
            }else{
               numDigits++;
            }
        }

        System.out.println(j);
        return numDigits;

    }
}
