import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by David on 4/3/2016.
 *
 * javaCC, ANTLR
 *
 * reflection---- with the math class dynamic method invocajtion
 * given a string find the correstopoding methods
 *
 * machine learning type thing
 *
 *
 * this is designed to hold all of the terms ie the whole expression
 *
 * maybe make it hold an arraylist of expressions rather than terms
 * and have terms extend expression
 * --- this is to hopefully hold like sin(x+2) as an expression--- because if a term is an expression then can still put in like x^2
 * --- so expressions have trig and those types of functions
 * ---- and terms are either constants or have a variable with a power
 * --- But then have to rewrite a lot of like everything... damn
 * --- maybe to make easier to change, i can leave the constructors with terms but add in
 * --- some that take arrays(lists) of expressions.
 * -------** so start of maybe by having all be terms then when like simplify them like by adding and combining like terms then create that expression and add it to the list
 *
 * -- or perhaps another class that just holds and arraylist of expressions otherwise each created expression will be an arraylist of expressions
 * -- so can keep expression being an arraylist of terms but should also have either another list or something that can store a correspoding trig function
 *
 * --- and or maybe use the mathobjext interface and have expressions be math objects and terms extend expressions then can have like the functions
 * -- like sin(trig and log stuff) be mathobjects so can create an arraylist of both. but would need to separate
 *
 * -- keep expressrion having the trig function then have the whole thing and like what is inside the parenthesis be simplified-- like create an expression of the inside
 * --- then have the expression hold terms and trig functions so an expression will just be sin(x+2) not sin(x+2)+cos(4x) those would be two different expressions
 * --- so the expressions are different if when parseing encounter a trig function then that will be a new expression
 */
public class Expression extends MathStatement {

    //maybe an array list that holds the terms
    private ArrayList<Term> terms;

    // this is for like trig functions
    private String function;
    private boolean hasFunction = false;
    private double funcitonCoeficient;
    // also could need a function degree

    // the string is the function associated with it-- could have a switch statement and have it correspond to a number
    public Expression(String fun, Term[] terms){
        this(terms);
        function = fun;
        hasFunction = true;
        this.funcitonCoeficient = 1;
    }
    public Expression(String fun,double coe,Term[] terms){
        this(fun,terms);
        this.funcitonCoeficient = coe;
    }
    public Expression(String fun,double coe,ArrayList<Term> terms){
        this(terms);
        function = fun;
        funcitonCoeficient = coe;
        hasFunction = true;
    }
    public Expression(String fun, double coe, Expression e){
        this.terms = e.getTerms();
        funcitonCoeficient = coe;
        function = fun;
        hasFunction = true;
    }

    public Expression(){

    }
    public Expression(Term t){
        terms = new ArrayList<>();
        terms.add(t);
    }

    public Expression(Term[] terms){
        this.terms = new ArrayList<>();
        for (int i = 0; i < terms.length; i++) {
            this.terms.add(terms[i]);
        }
        simplify();
    }
    public Expression(ArrayList<Term> terms){
        this.terms = terms;
    }
    //there is a problem when parenthasis are passed in(x^2+1)(x+2)
    public Expression(String exp){
        this.terms = new ArrayList<>();
        String[] termTokens = createTokenArray(exp,true);

        // need to separate out whether if it has paraenthesis or not
        boolean hasParaenthesis = false;
        for (int i = 0; i < termTokens.length; i++) {
            if(termTokens[i].contains("(")){
                hasParaenthesis = true;
                break;
            }
        }
        if(hasParaenthesis) {
            ArrayList<ArrayList<Term>> allExpressions = new ArrayList<>();
            for (int i = 0; i < termTokens.length; i++) {
                // if find an open parenthesis
                //need a check if the first token is not an parenthiesis ex: x(x+3)
                if (termTokens[i].contains("(")) {
                    log("Contains parenthesis:  " + i);
                    //cycle through tokens until find the close one and all of those in there will become there own expression
                    // then will multiply the two together(foil) and call a different constructor
                    // will later have to check that the two paraenthesis things arent being addedd or something not multiplied
                    int j;
                    for (j = i; j < termTokens.length; j++) {
                        if (termTokens[j].equals(")")) {
                            break;
                        }
                    }
                    ArrayList<Term> firstExpressionTerms = new ArrayList<>();
                    for (int k = i + 1; k < j; k++) {
                        firstExpressionTerms.add(new Term(termTokens[k]));
                    }
                    System.out.println("first expression terms: " + firstExpressionTerms);
                    allExpressions.add(firstExpressionTerms);
                    i = j;
                } else {
                    // maybe can make this work for if like x(x+3)
                    Term addTerm = new Term(termTokens[i]);
                    ArrayList<Term> tt = new ArrayList<>();
                    tt.add(addTerm);
                    allExpressions.add(tt);
                }
            }
            System.out.println("Array list of all expressions: " + allExpressions);
            //maybe recall the string constructor incase inner parranthesis ex: ((x+2)(x+2))
            ArrayList<Expression> expressionsList = new ArrayList<>(); // incase there are more than 2 i cant just multily them
            for (int i = 0; i < allExpressions.size(); i++) {
                expressionsList.add(new Expression(allExpressions.get(i)));
            }
            System.out.println("actually of type expression list: " + expressionsList);
            //this(expressionsList);
            this.terms = reduceExpressions(expressionsList).getTerms();
        }else{
            // need to simplify the multiplication here first
            for (int i = 0; i < termTokens.length; i++) {
                Term addTerm = new Term(termTokens[i]);
                terms.add(addTerm);
            }
            this.simplify();
        }


        System.out.println("input: " +exp);
    }

    // the int a doesnt do anything its just so i can have two different string accepting constructors
    // this one will only get normal expressions like x+3 or 11x etc
    public Expression(String input,int a){

    }

    // this will take a bunch of multiplied expaned expressions and combine them all
  /*  public Expression(ArrayList<Expression> expressions){
        for (int i = 0; i < expressions.size()-1; i++) {
            Expression e = expressions.get(i).foil(expressions.get(i+1));
            expressions.remove(i+1);
            expressions.set(i,e);
        }
        System.out.println("ArrayList expressions constructor: " + expressions);
    }*/

    // this foils a bunch of expressions
    public Expression reduceExpressions(ArrayList<Expression> expressions){
        for (int i = 0; i < expressions.size()-1; i++) {
            expressions.get(i).simplify();
            expressions.get(i+1).simplify();
            Expression e = expressions.get(i).foil(expressions.get(i+1));
            expressions.remove(i+1);
            expressions.set(i, e);
            i--;
        }
        System.out.println("ArrayList expressions constructor: " + expressions);
        return expressions.get(0);
    }
    public Expression(Expression e){
        this.terms = e.getTerms();
    }

    // so this is for the multiplying two expressions to get (x+1)(x+2)
    // but dont want to simplify in the constructor
    public Expression(Expression e1, Expression e2){

    }

    //like a get coeficients method and return an array or something

    //like multiplying two expressions returns a new expression and would just put parenthesies around the two expressions
    // in string form

    // this just makes a string representation
    public String multiply(Expression e){
        removeZeroCoeficientTerms();
        String newexpress = "("+e.getStringRepresentation() + ")("+this.getStringRepresentation()+")";
        return newexpress;
    }
    //this is where the polynomial long division comes in
    public Expression divide(Expression e){

        return null;
    }
    public Expression subtract(Expression e){
        // could get the highest degree then put in an array all of the terms that have the same degree- then add them
        int highestDegree;
        if(this.getHighestDegree() > e.getHighestDegree()) {
            highestDegree = this.getHighestDegree();
        }else{
            highestDegree = e.getHighestDegree();
        }
        // the highest degree is the number of arrayLists i want holding terms
        ArrayList<ArrayList<Term>> degreeLists = new ArrayList<>();
        for (int i = 0; i <= highestDegree; i++) {
            ArrayList<Term> termsofIdegree = new ArrayList<>();
            for (int j = 0; j < this.terms.size(); j++) {
                if(this.terms.get(j).getDegree() == i){
                    termsofIdegree.add(this.terms.get(j));
                }

            }
            for (int j = 0; j < e.terms.size(); j++) {
                if(e.terms.get(j).getDegree() == i){
                    termsofIdegree.add(e.terms.get(j));
                }
            }
            log("the terms of " + i+ " degree: " + termsofIdegree);
            degreeLists.add(termsofIdegree);
        }
        for (int i = 0; i < degreeLists.size(); i++) {
            for (int j = 0; j < degreeLists.get(i).size(); j++) {
                if(degreeLists.get(i).size()==1){
                   // log("INAD HERERE_E__E_R");
                    Term addedTerm = degreeLists.get(i).get(j);
                    degreeLists.get(i).set(j,addedTerm);
                }else {
                    Term subtractedTerm = degreeLists.get(i).get(j).minus(degreeLists.get(i).get(j + 1));
                    degreeLists.get(i).remove(j + 1);
                    degreeLists.get(i).set(j, subtractedTerm);
                }
            }
        }
        System.out.println("the array list of the array list of terms: " + degreeLists);

        Expression subtractedExpression;
        ArrayList<Term> expressionsToAddBack = new ArrayList<>();
        for (int i = 0; i < degreeLists.size(); i++) {
            if(!degreeLists.get(i).isEmpty()){
                expressionsToAddBack.add(degreeLists.get(i).get(0));
            }
        }
        System.out.println("back to single array list: " + expressionsToAddBack);
        subtractedExpression = new Expression(expressionsToAddBack);

        return subtractedExpression;
    }

    // like (x+2) + (x+3)
    // I am just going to have this add be for basic polynomials ie nothing with trig i will create a new add method for that
    public Expression add(Expression e){

        if(!hasFunction) {
            // could get the highest degree then put in an array all of the terms that have the same degree- then add them
            int highestDegree;
            if (this.getHighestDegree() > e.getHighestDegree()) {
                highestDegree = this.getHighestDegree();
            } else {
                highestDegree = e.getHighestDegree();
            }
            // the highest degree is the number of arrayLists i want holding terms
            ArrayList<ArrayList<Term>> degreeLists = new ArrayList<>();
            for (int i = 0; i <= highestDegree; i++) {
                ArrayList<Term> termsofIdegree = new ArrayList<>();
                for (int j = 0; j < this.terms.size(); j++) {
                    if (this.terms.get(j).getDegree() == i) {
                        termsofIdegree.add(this.terms.get(j));
                    }

                }
                for (int j = 0; j < e.terms.size(); j++) {
                    if (e.terms.get(j).getDegree() == i) {
                        termsofIdegree.add(e.terms.get(j));
                    }
                }
                log("the terms of " + i + " degree: " + termsofIdegree);
                degreeLists.add(termsofIdegree);
            }
            for (int i = 0; i < degreeLists.size(); i++) {
                for (int j = 0; j < degreeLists.get(i).size(); j++) {
                    if (degreeLists.get(i).size() == 1) {
                       // log("INAD HERERE_E__E_R");
                        Term addedTerm = degreeLists.get(i).get(j);
                        degreeLists.get(i).set(j, addedTerm);
                    } else {
                        Term addedTerm = degreeLists.get(i).get(j).add(degreeLists.get(i).get(j + 1));
                        degreeLists.get(i).remove(j + 1);
                        degreeLists.get(i).set(j, addedTerm);
                    }
                }
            }
            System.out.println("the array list of the array list of terms: " + degreeLists);

            Expression addedExpression;
            ArrayList<Term> expressionsToAddBack = new ArrayList<>();
            for (int i = 0; i < degreeLists.size(); i++) {
                if (!degreeLists.get(i).isEmpty()) {
                    expressionsToAddBack.add(degreeLists.get(i).get(0));
                }
            }
            System.out.println("back to single array list: " + expressionsToAddBack);
            addedExpression = new Expression(expressionsToAddBack);

            return addedExpression;
        }else{ // then it has like a trig function associated with it
            if(function.equals(e.getFunction())){ // otherwise cant add so return the two

            }
            return null;
        }
    }

    // will return the expression sith just sin infront of it.
    public Expression sin(){
       return null; // just for now so no errors
    }

    // this is for x(x+2) = x^2 + 2x or (x+3)(x+2) =
    public Expression foil(Expression e){
        this.simplify();
        e.simplify();
      //  System.out.println("expression 1: " + this);
     //   System.out.println("expression 2: " + e);
        ArrayList<Term> foiledTerms = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            for (int j = 0; j < e.terms.size(); j++) {
                Term foilTerm = terms.get(i).multiply(e.terms.get(j));
                foiledTerms.add(foilTerm);
            }
        }
      //  System.out.println("foiled terms: " + foiledTerms);
        Expression foilExpression = new Expression(foiledTerms);
        foilExpression.simplify();
        //would want to sort but the sorting method is in DegreeList
        foilExpression.sortTerms(terms);
        return foilExpression;

    }
    //change from void either to string or expression
    public String foil(String input){
        if(!input.contains("(")){ // if there are no parenthesis then can just return the input
            return input;
        }
        String[] termTokens = createTokenArray(input, false); // maybe dont want term tokens to include the addition signs
        printArray("Foil method-term tokens: ", termTokens);

       return simplify(termTokens);
/*
        ArrayList<ArrayList<Term>> allExpressions = new ArrayList<>();
        for (int i = 0; i < termTokens.length; i++) {
            // if find an open parenthesis
            //need a check if the first token is not an parenthiesis ex: x(x+3)
            // need to create a stack and add paranethis to it to
            if (termTokens[i].contains("(")) {
                log("Contains parenthesis:  " + i);
                //cycle through tokens until find the close one and all of those in there will become there own expression
                // then will multiply the two together(foil) and call a different constructor
                // will later have to check that the two paraenthesis things arent being addedd or something not multiplied
                int j;
                for (j = i; j < termTokens.length; j++) {
                    if (termTokens[j].equals(")")) {
                        break;
                    }
                }
                ArrayList<Term> firstExpressionTerms = new ArrayList<>();
                for (int k = i + 1; k < j; k++) {
                    firstExpressionTerms.add(new Term(termTokens[k]));
                }
                System.out.println("first expression terms: " + firstExpressionTerms);
                allExpressions.add(firstExpressionTerms);
                i = j;
            } else {
                // maybe can make this work for if like x(x+3)
                Term addTerm = new Term(termTokens[i]);
                ArrayList<Term> tt = new ArrayList<>();
                tt.add(addTerm);
                allExpressions.add(tt);
            }
        }
        System.out.println("Array list of all expressions: " + allExpressions);
        //maybe recall the string constructor incase inner parranthesis ex: ((x+2)(x+2))
        ArrayList<Expression> expressionsList = new ArrayList<>(); // incase there are more than 2 i cant just multily them
        for (int i = 0; i < allExpressions.size(); i++) {
            expressionsList.add(new Expression(allExpressions.get(i)));
        }
        System.out.println("actually of type expression list: " + expressionsList);
        //this(expressionsList);
        this.terms = reduceExpressions(expressionsList).getTerms();

        return this.getStringRepresentation();*/
    }
    // this actuall will simplify the paraenthesis and will expand if needed
    // need to simplify ie if(x^2+5x*x) need to shrink that and make it (6x^2)
    public String simplify(String[] args){
        // this would work for each parenthesis so just want the stuff in one paraenthesis
        ArrayList<String> simplified = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            simplified.add(args[i]);
        }
        for (int i = 0; i < simplified.size(); i++) {
            if(i < simplified.size()-1 && simplified.get(i).equals(")") && simplified.get(i+1).equals("(")){
                simplified.add(i+1,"*");
            }
        }
        log("simplified: fixed the multiplication between parenthesis: " + simplified);

        String[] rpnForm = parse(args);
        printArray("RPN Form: ", rpnForm);
        String solved = solveRPN2(rpnForm);

        /*for (int i = 0; i < simplified.size(); i++) {
            if(simplified.get(i).equals("*")){
                //then want the token before and after the *
                // assumine that they are both terms otherwise i guess do nothing
                if(isTerm(simplified.get(i-1)) && isTerm(simplified.get(i+1))){
                    Term term1 = new Term(simplified.get(i-1));
                    Term term2 = new Term(simplified.get(i+1));
                    Term combined = term1.multiply(term2);
                    simplified.set(i - 1, combined.getStringVisualRepresentation());
                    simplified.remove(i); // remove i both times because the arraylist shrinks
                    simplified.remove(i);
                    i--;
                }
            }
        }
        log("the simplified(by multiplication) list is: " +simplified + "");*/
        return solved;
    }

    // so can just pass in multiple expressions
    public void foil(Expression ... expressions){

    }

    // this just combines like terms only adding
    // combineLikeTerms
    // this needs to be d
    public void simplify(){
        //this should be sorted first
        sortTerms(terms);
        for (int i = 0; i < terms.size(); i++) {
            for (int j = i+1; j < terms.size(); j++) {
                if(terms.get(i).getDegree() == terms.get(j).getDegree()){
                    Term addedTerm = terms.get(i).add(terms.get(j));
                    terms.remove(j);
                    terms.set(i,addedTerm);
                    j--;
                }
            }
        }
        System.out.println("simplified terms: " + terms);
    }



    // htis works maybe want to return a new expression instead of modifying this one
    public String derivitive(){
       Expression e = new Expression(simplify(getStringRepresentationInnerTerms())); // this doesnt work if there is a trig function because that is added to the string representation
        for (int i = 0; i < terms.size(); i++) {
            log("pre derive term: " + e.terms.get(i) + " this terms is: " + terms.get(i));
            e.terms.get(i).derivative();
            log("post derive term: " + e.terms.get(i));
        }

        System.out.println("derivative of terms: " + e.terms);
        return e.getStringRepresentationInnerTerms();
    }
    public void removeZeroCoeficientTerms(){
        for (int i = 0; i < terms.size(); i++) {
            if(terms.get(i).getCoeficient() == 0){
                terms.remove(i);
                i--;
            }
        }
    }

    //only use this for printing out
    //remove the 1's from the x's
    public void removeOnesCoeficientsFromVariables(){

        for (int i = 0; i < terms.size(); i++) {
            if(terms.get(i).getCoeficient() == 1 && terms.get(i).getDegree() != 0){
                terms.get(i).setTerm(terms.get(i).getTerm().replace("1.0",""));
            }
        }
    }
    public void removePointZero(){
        for (int i = 0; i < terms.size(); i++) {
            if(terms.get(i).getTerm().contains(".0")){
                terms.get(i).setTerm(terms.get(i).getTerm().replace(".0",""));
            }
        }
    }
    public int getHighestDegree(){
        int max = terms.get(0).getDegree();
        for (int i = 0; i < terms.size(); i++) {
            if(terms.get(i).getDegree() > max){
                max = terms.get(i).getDegree();
            }
        }

        return max;
    }

    // for the input
    public boolean isInFactorableForm(){

        return false;
    }

    public ArrayList<Term> getTerms(){
        return terms;
    }
    public String getStringRepresentation(){
       // log("size of terms pre remove : " + terms.size());
        //I will put these at the end and i dont want to change the actual terms just maybe mnake a copy and change that
       removeZeroCoeficientTerms();
       //// log("size of terms post remove zeros: " + terms.size());
       // removeOnesCoeficientsFromVariables();
        //removePointZero();
       // log("terms pre string representation: " + terms);
        if(terms.size() > 1) {
            sortTerms(terms);
        }
        String s = "";
        if(terms.get(0).getTerm().substring(0,1).equals("+")){
            s = terms.get(0).getStringVisualRepresentation().substring(1);
        }else{
            s= terms.get(0).getStringVisualRepresentation();
        }
        for (int i = 1; i < terms.size(); i++) {
            s += terms.get(i).getStringVisualRepresentation()+ "";
        }
        //log("terms post string representation: " + s);
        if(hasFunction){
            if(funcitonCoeficient ==1) {
                s = function + "(" + s + ")";
            }else{
                s = funcitonCoeficient + function + "("+s+")";
            }
        }
        return s;
    }
    // this is a string representation without the trig function-- use for the derivative method because that uses the string to create a new expression
    public String getStringRepresentationInnerTerms(){
        //I will put these at the end and i dont want to change the actual terms just maybe mnake a copy and change that
        removeZeroCoeficientTerms();
        // removeOnesCoeficientsFromVariables();
        //removePointZero();
        // log("terms pre string representation: " + terms);
        sortTerms(terms);
        String s = "";
        if(terms.get(0).getTerm().substring(0,1).equals("+")){
            s = terms.get(0).getStringVisualRepresentation().substring(1);
        }else{
            s= terms.get(0).getStringVisualRepresentation();
        }
        for (int i = 1; i < terms.size(); i++) {
            s += terms.get(i).getStringVisualRepresentation()+ "";
        }
        //log("terms post string representation: " + s);

        return s;
    }


    public void log(String s ){
        System.out.println(s);
    }
    public void logSameLine(String s){
        System.out.print(s);
    }

    // use this to create the terms
    public String[] createTokenArray(String s,boolean factoring){
        log("createTokenArray: input: " + s);
        char[] c = s.toCharArray(); // issue with this is if have a greater than 2 digit number

        ArrayList<String> equ = new ArrayList<>();
        for (int i = 0; i < c.length; i++) {
            //System.out.println(c[i]);
            //if it is a number is checks for if it is a greater than one digit number
            if ((c[i] >= '0' && c[i] <= '9') || c[i] == 'x') {
                String number = String.valueOf(c[i]);
                int j = i + 1;
                while (j < c.length && ((c[j] >= '0' && c[j] <= '9')|| c[j] == '.' || c[j] == 'x' || c[j] == '^')) {
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
                if(op.equals("sqrt")){ ///////////////////////// this still needs work what if coefirent swrt or negatice
                    if(c[i+2] == 'x' && c[i+3] == ')'){
                        op = "x^0.5";
                    }
                }
                equ.add(op);
                //checking if there is a negative sign for a number
            }else if(c[i] == '-' && !factoring && i == 0){
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
                    String[] stuff = createTokenArray(add,false); // yayy recursion
                    for (int j = 0; j < stuff.length; j++) {
                        equ.add(stuff[j]+"");
                    }
                }
                else {
                    equ.add(c[i] +"");
                }
            }else if(c[i] == '-'){
               // log("DOUING THIS");
                String number = c[i] +""+ c[i+1]+"";
                int w = i+2;
                while(w<c.length && ((c[w] >= '0' && c[w] <= '9') || c[w] == '.' || c[w] == 'x')){
                    number += c[w];
                    w++;
                }
                i = w-1;
                equ.add(number);
            }else if(c[i] == '+'&& c[i+1] != '(' && factoring){
                String number = c[i] +""+ c[i+1]+"";
                int w = i+2;
                while(w<c.length && ((c[w] >= '0' && c[w] <= '9') || c[w] == '.' || c[w] == 'x' || c[w] == '^')){
                    number += c[w];
                    w++;
                }
                i = w-1;
                equ.add(number);
            }
            else {
                equ.add(c[i] + "");
            }
        }

        // if the first thing added has a negatice sign i leave it but if not then i want to add an addition sign to the front
        if(!equ.get(0).contains("-") && !equ.get(0).substring(0,1).equals("(") && !equ.get(0).substring(0,1).equals("+")){
            equ.set(0, "+"+ equ.get(0));
        }
        System.out.println("This is the equation: " + equ);
      /*  System.out.print("is token a term: " );
        for (int i = 0; i < equ.size(); i++) {
            System.out.print(isTerm(equ.get(i))+ ", ");
        }*/
        log("");
        String[] ttt = new String[equ.size()];
        return equ.toArray(ttt);
    }

    public String simplifyWorse(String input){
        String[] termTokens = createTokenArray(input, true);
        String[] expressionArray = createExpressionArray(termTokens);
        String[] rpnExpression = parse(expressionArray);
        Expression simplifiedExpression = solveRPN(rpnExpression);
        return simplifiedExpression.getStringRepresentation();
    }
    public String[] createExpressionArray(String[] tokenArray){

        ArrayList<String> expressionArray = new ArrayList<>();
        for (int i = 0; i < tokenArray.length; i++) {
            expressionArray.add(tokenArray[i]);
        }

        // an arraylist of terms is an expression
        for (int i = 0; i < expressionArray.size()-1; i++) {
            if(isTerm(expressionArray.get(i)) && isTerm(expressionArray.get(i+1))){
                String expression = expressionArray.get(i) + expressionArray.get(i+1);
                Expression ex = new Expression(expression);
                log("ex: " + ex);
                expressionArray.remove(i+1);
                expressionArray.set(i, ex.getStringRepresentation());
                i--;
                log("Create EXpression ARray-- expression: " + ex.getStringRepresentation());
            }
        }
        log("the new exxpression array: " + expressionArray);

        String[] expr = new String[expressionArray.size()];
        //parse(expressionArray.toArray(expr));
        return expressionArray.toArray(expr);
    }



    public static final int LEFT_ASSOC = 0;
    public static final int RIGHT_ASSOC = 1;
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
    }

    private  boolean isOperator(String key){
        return OPERATIONS.containsKey(key);
    }

    private  boolean isAssociative(String token, int type){
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
    private  int comparePrecedence(String token1,String token2){
        if(!isOperator(token1)|| !isOperator(token2)){
            throw new IllegalArgumentException("ILLEGALLLLL PLZZZZ");
        }

        return OPERATIONS.get(token1)[0] - OPERATIONS.get(token2)[0];
    }

    // this puts the expressions into RPN notation
    public  String[] parse(String[] inputTokens){
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

        log("the expressions in RPN: " + output);
        String[] rpnoutput = new String[output.size()];
        return output.toArray(rpnoutput);
    }

    //this will also foil i believe
    public String solveRPN2(String[] t){
        log("$in Method Expression.SolveRPN2 ");
        ArrayList<String> tokens = new ArrayList<>();
        for (int i = 0; i < t.length; i++) {
            tokens.add(t[i]);
        }
        for (int i = 0; i < tokens.size(); i++) {
            if(isOperator(tokens.get(i))){
                Expression value1 = new Expression(tokens.get(i-2));
                Expression value2 = new Expression(tokens.get(i-1)); // here could do a check for if either has a like trig function and if it does thencall a different add method


                Expression result = null;
                if(tokens.get(i).equals("+")){
                    result = value1.add(value2);
                }else if(tokens.get(i).equals("-")){
                    result = value1.subtract(value2);
                }else if(tokens.get(i).equals("*")){
                    result = value1.foil(value2);
                }else if(tokens.get(i).equals("/")){
                    result = value1.divide(value2);
                }else if(tokens.get(i).equals("^")){
                    //result = Math.pow(value1,value2);
                }
                tokens.remove(i);
                tokens.remove(i-1);
                tokens.set(i - 2, result + "");
                i = i-2;
            }
        }
        log("the tokens after solvingRPN2: " + tokens); // now if there is more than 1 term
        // in tokens then can assume that there should be paraenthesis around the two or however many and should foil them
        ArrayList<Expression> expressions = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            expressions.add(new Expression(tokens.get(i)));
        }
        Expression reducedExpression = reduceExpressions(expressions);
        log("After reducing the remaining expressions: " + reducedExpression);
        return reducedExpression.getStringRepresentation();
    }

    //solving reverse polish notation
    public Expression solveRPN(String[] t){
        ArrayList<String> tokens = new ArrayList<>();
        for (int i = 0; i < t.length; i++) {
            tokens.add(t[i]);
        }
        for (int i = 0; i < tokens.size(); i++) {
            if(isOperator(tokens.get(i))) {
                Expression value1 = new Expression(tokens.get(i-2));
                Expression value2 = new Expression(tokens.get(i-1));


                Expression result = null;
                if(tokens.get(i).equals("+")){
                    result = value1.add(value2);
                }else if(tokens.get(i).equals("-")){
                    result = value1.subtract(value2);
                }else if(tokens.get(i).equals("*")){
                    result = value1.foil(value2);
                }else if(tokens.get(i).equals("/")){
                    result = value1.divide(value2);
                }else if(tokens.get(i).equals("^")){
                    //result = Math.pow(value1,value2);
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
                    value1 = Double.parseDouble(tokens.get(i - 1));
                }
                double result =0;
                if(tokens.get(i).equals("sin")){
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
                }
                tokens.remove(i);
                tokens.set(i-1,result+"");
                i=i-1;
            }
            //  System.out.println("Solving the RPN: " + tokens);
        }
        return new Expression(tokens.get(0));
    }



    // also think this will return true if passing an expression in
    // only dealing with +,-,*,/ for now--- thats why the s.length == 1 works
    public boolean isTerm(String s){
        if(s.contains("(") || s.contains(")")){
            return false;
        }
        if(isOperator(s) && s.length() == 1){
            return false;
        }


        return true;
    }
    public String[] creatingTokenArrayForSimplifying(String s,boolean factoring){
        log("createTokenArray: input: " + s);
        char[] c = s.toCharArray(); // issue with this is if have a greater than 2 digit number

        ArrayList<String> equ = new ArrayList<>();
        for (int i = 0; i < c.length; i++) {
            //System.out.println(c[i]);
            //if it is a number is checks for if it is a greater than one digit number
            if ((c[i] >= '0' && c[i] <= '9') || c[i] == 'x') {
                String number = String.valueOf(c[i]);
                int j = i + 1;
                while (j < c.length && ((c[j] >= '0' && c[j] <= '9')|| c[j] == '.' || c[j] == 'x' || c[j] == '^')) {
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
                if(op.equals("sqrt")){ ///////////////////////// this still needs work what if coefirent swrt or negatice
                    if(c[i+2] == 'x' && c[i+3] == ')'){
                        op = "x^0.5";
                    }
                }
                equ.add(op);
                //checking if there is a negative sign for a number

            }/*else if(c[i] == '-'){
                // log("DOUING THIS");
                String number = c[i] +""+ c[i+1]+"";
                int w = i+2;
                while(w<c.length && ((c[w] >= '0' && c[w] <= '9') || c[w] == '.' || c[w] == 'x')){
                    number += c[w];
                    w++;
                }
                i = w-1;
                equ.add(number);
            }*/
           /* else if(c[i] == '+'&& c[i+1] != '(' && factoring){
                String number = c[i] +""+ c[i+1]+"";
                int w = i+2;
                while(w<c.length && ((c[w] >= '0' && c[w] <= '9') || c[w] == '.' || c[w] == 'x' || c[w] == '^')){
                    number += c[w];
                    w++;
                }
                i = w-1;
                equ.add(number);
            }*/
            else {
                equ.add(c[i] + "");
            }
        }

        // if the first thing added has a negatice sign i leave it but if not then i want to add an addition sign to the front
        if(!equ.get(0).contains("-") && !equ.get(0).substring(0,1).equals("(") && !equ.get(0).substring(0,1).equals("+")){
            equ.set(0, "+"+ equ.get(0));
        }
        System.out.println("This is the equation: " + equ);
      /*  System.out.print("is token a term: " );
        for (int i = 0; i < equ.size(); i++) {
            System.out.print(isTerm(equ.get(i))+ ", ");
        }*/
        log("");
        String[] ttt = new String[equ.size()];
        return equ.toArray(ttt);
    }

    public String factor(String input){
       String simplified = simplify(input);
        Expression e = new Expression(simplified);
        String factored = e.factor(e.getTerms());
        log("THIS IS THE FACTORED RESULT: " + factored);
        //after simplified and put into a linear polynomial expression
        // then create a new expression and facor those terms

        return factored;
    }

    public String simplify(String input){
        //first need to simplify the input
        String[] tokens = creatingTokenArrayForSimplifying(input, false);
        printArray("factored original tokens: ",tokens );
        // need to put in RPN notation
        String[] rpnTokens = parse(tokens);
        log("JUST FINISHED CREATING RPN TOKENS AND THEY ARE: ");
        printArray("RPN TOKENS: ", rpnTokens);
        String simplified = solveRPN2(rpnTokens);
        log("The simplified factored expression: " + simplified);
        Expression e = new Expression(simplified);

        return simplified;
    }

    public String factor(ArrayList<Term> terms){
        System.out.println("The input terms: " + terms);
        if(getHighestDegree(terms) == 1){
            return "("+ new Expression(terms).getStringRepresentation()+")";
        }
        //first need to simplify

        sortTerms(terms);
        fillMissingExpression(terms);
        double aN = terms.get(terms.size()-1).getCoeficient(); // if this isnt a constant(ie doesnt have 'x') then need to include 0 in aN
        double a0 = terms.get(0).getCoeficient();
        ArrayList<Double> possibleRoots = new ArrayList<>();
        ArrayList<Double> aNFactors = new ArrayList<>();
        ArrayList<Double> a0Factors = new ArrayList<>();
        double m = 0;
        while (m<10){
            if(aN%m == 0){
                aNFactors.add(m);
                aNFactors.add(m*-1);
            }
            if(a0%m == 0){
                a0Factors.add(m);
            }
            m++;
        }
        for (int j = 0; j < aNFactors.size(); j++) {
            for (int k = 0; k < a0Factors.size(); k++) {
                double posRoot = aNFactors.get(j)/a0Factors.get(k);
                possibleRoots.add(posRoot);
            }
        } // i should go through and remove duplicates to save time
        System.out.println("possible roots: " +possibleRoots);

        // maybe could just to what i do with the graphing calculator but just plug in pos roots and if it equals 0 then yay

        double[] coeficients = getExpressionCoeficients(terms);
        printArray("COeficients: " ,coeficients);
        double[] quotient = new double[coeficients.length];
        //quotient[0] = coeficients[0];
        double root = 0;
        boolean foundRoot = false;
        //synthetic division
        for (int i = 0; i < possibleRoots.size(); i++) {
            double posRoot = possibleRoots.get(i);
            quotient = syntheticDivision(posRoot,coeficients);

            if(quotient[quotient.length-1] == 0){
                //System.out.print("the correct quotients: " );
                for (double b : quotient){
                    System.out.print(b + ", ");
                }
                System.out.println();
                root = posRoot*-1; // because i use this to create the string and while the root is negative that would mean i add the root
                foundRoot = true;
                break;
            }

        }
        if(!foundRoot){
            return "("+new Expression(terms).getStringRepresentation()+")";
            // return null;
        }
        // now have to convert the quotient coeficients back into and expression
        Term[] quotients = new Term[coeficients.length-1];
        int newdegree = getHighestDegree(terms)-1;
        for (int i = 0; i < quotients.length; i++) {
            if(newdegree ==0){
                quotients[i] = new Term(quotient[i]+"");
            }else {
                quotients[i] = new Term(quotient[i] + "x^" + (newdegree));
            }
            newdegree--;
        }
        Expression firstexprs = new Expression(quotients);
        //System.out.println("First expression: " + firstexprs);
        Expression secondexprs;
        String s;
        if(root > 0){
            s = "(x+" + root + ")";
        }else {
            s = "(x" + root+")";
        }
        Term[] secondExpressionTerms = {new Term("x"),new Term(root+"")};
        secondexprs = new Expression(secondExpressionTerms);
        System.out.println("second expression: " + secondexprs);

        //Expression factored = firstexprs.multiply(secondexprs);
        String otherFactor = "";
        String f = "";
        System.out.println("HIGEST DEGREE OF FIRST EXPRES: " + firstexprs.getHighestDegree());
        /*while(f != null && firstexprs.getHighestDegree() != 1){
            System.out.println("trying to factoragain: " + firstexprs.getTerms());
            if(firstexprs.getHighestDegree() == 1){
                break;
            }
            f = factor(firstexprs.getTerms());
            otherFactor+= f;
        }*/
        firstexprs.removeZeroCoeficientTerms();
        String test = "" + firstexprs.getStringRepresentation();
        System.out.println("test first expression remove 0: " + test);
        if(firstexprs.getHighestDegree() != 1 && factor(firstexprs.getTerms()) != null){
            test = factor(firstexprs.getTerms());
        }
        String fullyFactored = "("+test+")" + ""+ s;
        String fix1 =fullyFactored.replace(".0","");
        String fix2 = fix1.replace("^1","");
        fullyFactored = fix2;
        // String factored = firstexprs.multiply(secondexprs);
        //System.out.println("the factored thing: " + factored);

        Expression fExpres = new Expression(firstexprs.getTerms());
        fExpres.removeZeroCoeficientTerms();
        Expression sExpress = new Expression(secondexprs.getTerms());
        sExpress.removeZeroCoeficientTerms();

        String ffExpress =  factor(fExpres.getTerms());
        System.out.println("ffExpress: " + ffExpress);
        String fullyFactored2 = ffExpress + "("+sExpress.getStringRepresentation()+")";
        System.out.println("fullyFactored2: " + fullyFactored2);
        String fullyFactored1 = fExpres.multiply(sExpress);

        return fullyFactored2;

    }
    //this only works if it is already sorted
    //cause want like x^4+0x^3+...
    public void fillMissingExpression(ArrayList<Term> terms){

        for (int i = 0; i < terms.size()-1; i++) {
            if(terms.get(i).getDegree() == (terms.get(i+1).getDegree()+1)){

            }else{
                terms.add(i+1,new Term("+0x^" + (terms.get(i).getDegree()-1)));
            }
        }
        if(terms.get(terms.size()-1).getDegree() != 0){
            terms.add(new Term(0,0));
        }
    }

    //this will get the roots of the polynomail
    public String getRoots(String input){
        String factoredString = factor(input);
        log("the roots method has factored the input: " + factoredString);
        ArrayList<String> roots = new ArrayList<>();
        for (int i = 0; i < factoredString.length(); i++) {
            if(factoredString.substring(i,i+1).equals("(")){
                log("the loop has found an open paranthesis in the factored string");
                int j =i;
                while(!factoredString.substring(j,j+1).equals(")")){
                    j++;
                }
                Expression e = new Expression(factoredString.substring(i,j));
                log("the expression has been created and it is about to solve it");
                roots.add(solve(e));
            }
        }
        String rootsString="";
        //creating the sting i want to return
        for (int i = 0; i < roots.size(); i++) {
            if(i != 0 ){
                rootsString += ",";
            }
            rootsString += roots.get(i);
        }

        return rootsString;
    }

    public void printArray(String title, double[] stuff){
        System.out.print(title);
        for (double b : stuff){
            System.out.print(b +", ");
        }
        System.out.println();
    }
    public void printArray(String title, String[] stuff){
        System.out.print(title);
        for (String b : stuff){
            System.out.print(b +", ");
        }
        System.out.println();
    }
    public double[] syntheticDivision(double divisor,double[] coeficients){

        double[] quotient = new double[coeficients.length];
        quotient[0] = coeficients[0];
        for (int i = 1; i < coeficients.length; i++) {
            quotient[i] = coeficients[i] + (quotient[i-1]*divisor);
        }
        //log("SYNTHETIC DIVISION; quotients:");
        for (double a:quotient){
            System.out.print(a+" ");
        }
        System.out.println();

        return quotient;
    /*    if(quotient[quotient.length-1] == 0){
            //then yayay
            return true;
        }
        return false;*/
    }

    /*
    the dividend is the part inside the division sign
    the divisor is what you are dividing by
     */
  /*  public String polynomialLongDivision(Expression dividend, Expression divisor){
        dividend.fillMissingExpression(dividend.terms);
        double[] coeficientsOfDividend = dividend.getExpressionCoeficients(dividend.terms);
        //if it is linear then easy ill just do synthetic division
        if(divisor.getHighestDegree() == 1){
            double[] quotient = syntheticDivision(coeficientsOfDividend)
        }
    }*/

    // instead of using the quadratic i could just facotr it
    // then I would have to parse that string and just solve those individual expressions which could be easier
    // still deciding on return type
    public String solve(Expression e){
        double ans;
        String answers = "";
        if(e.getHighestDegree() == 1){
           ans = solveBasicLinearFunction(e);
            answers = "x="+ ans+"";
        }else if(e.getHighestDegree() == 2){
            e.fillMissingExpression(e.terms);
            double[] coe = e.getExpressionCoeficients(e.terms);
            String[] ansS = quadraticEquation(coe[0],coe[1],coe[2]);
            answers = "[x=" + ansS[0] +",x=" + ansS[1] + "]";
            logSameLine("the result of the quadtratic equations: ");
            for(String s : ansS){
                logSameLine(s+",");

            }
            log("");
        }

        return answers;
    }

    // will make one of these for all methods so it is easy to call from somewhere else
    public double solveBasicLinearFunction(String s){
        Expression e = new Expression(s);
        return solveBasicLinearFunction(e);
    }
    //like 2x - 1 = 0
    public double solveBasicLinearFunction(Expression e){
        if(e.getHighestDegree() != 1){
            return -1; // normally would throw an exception
        }
        double rightSide = 0; // this is like what the expression is equal to
        // if it is linear then there are only two terms max
        if(e.terms.size() == 2){
            rightSide += e.terms.get(1).getCoeficient();
            log("the right side coeficient: " + rightSide);
        }
        rightSide = rightSide/e.terms.get(0).getCoeficient();
        return rightSide;
    }

    //maybe return like a string[] containg two values like a 1 or 0 if it is a whole number JK DONT NEED TO
    //if the sqrt is negative just want to return the input
    public String[] quadraticEquation(double a, double b, double c){
        String[] roots = new String[3]; // could make this 3 and have the last index be whether it is a whole number or nah
       /* double root1 = -1*((-b + Math.sqrt(Math.pow(b,2)-4*a*c))/(2*a)); // have the -1* because for some reason result comes out the opposite sign
        double root2 = -1*((-b - Math.sqrt(Math.pow(b,2)-4*a*c))/(2*a)); // need to fix
*/
        // im going to separate everything to maybe make it look nicer
        double innersqrt = Math.pow(b,2)-4*a*c;
        double sqrt = Math.sqrt(innersqrt);
        String sqrtPart = "";
        if(sqrt%1 == 0){ // it is a whole number then ill leave it
            sqrtPart = "" + (sqrt);
            double root1 = -1*((-b + Math.sqrt(Math.pow(b,2)-4*a*c))/(2*a)); // have the -1* because for some reason result comes out the opposite sign
            double root2 = -1*((-b - Math.sqrt(Math.pow(b,2)-4*a*c))/(2*a));
            roots[0] = round(root1,2)+"";
            roots[1] = round(root2,2)+"";
            roots[2] = "whole";
        }else if(innersqrt < 0){
            roots[2] = "imagenary";
        }
        else{
            sqrtPart = "sqrt(" + innersqrt +")"; // ? this prints out as a question mark
            String num1 = "("+(-1*b)+"+"+sqrtPart+")/"+(2*a);
            String num2 = "("+(-1*b)+"-"+sqrtPart+")/"+(2*a);
            roots[0] = num1;
            roots[1] = num2;
            log("QUADRATICEQUATION- num1: " + num1);
            roots[2] = "0"; // this is just easier
        }
        log("QUADRATICEQUATION- sqrtPart: " + sqrtPart);


        //log("Quadratic equation-> root1: " + root1 +", root2: " + root2);
        return roots;
    }
    private double round(double ans,int numDecimalPlaces){
        BigDecimal decimal = new BigDecimal(ans);
        double rounded = decimal.setScale(numDecimalPlaces,BigDecimal.ROUND_HALF_UP).doubleValue();

        return rounded;
    }

    public double[] getExpressionCoeficients(ArrayList<Term> terms){
        double[] coefis = new double[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            coefis[i] = terms.get(i).getCoeficient();
        }

        return coefis;
    }
    public int getHighestDegree(ArrayList<Term> terms){
        int max = terms.get(0).getDegree();
        for (int i = 0; i < terms.size(); i++) {
            if(terms.get(i).getDegree() > max){
                max = terms.get(i).getDegree();
            }
        }

        return max;
    }
    public Term getHighestDegreeTerm(ArrayList<Term> terms){
        int max = terms.get(0).getDegree();
        Term currTerm = terms.get(0);
        for (int i = 0; i < terms.size(); i++) {
            if(terms.get(i).getDegree() > max){
                max = terms.get(i).getDegree();
                currTerm = terms.get(i);
            }
        }

        return currTerm;
    }
    public void sortTerms(ArrayList<Term> terms){
        quickSort(0,terms.size()-1,terms);
    }




  /*  public String toString(){
        String s = "{";
        for (int i = 0; i < terms.size(); i++) {
            s += "[ " + terms.get(i) +"]" + " ,";
        }
        s+= "}";
        return s;
    }*/

   /* public ArrayList<Term> getTerms(){
        return terms;
    }*/

    //quick sort
    private int array[];
    private int length;

  /*  public void sort(int[] inputArr) {

        if (inputArr == null || inputArr.length == 0) {
            return;
        }
        this.array = inputArr;
        length = inputArr.length;
        quickSort(0, length - 1,ArrayList<Term> terms);
    }*/

    private void quickSort(int lowerIndex, int higherIndex,ArrayList<Term> terms) {

        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        //int pivot = array[lowerIndex+(higherIndex-lowerIndex)/2];
        int pivotDegree = terms.get(lowerIndex+(higherIndex-lowerIndex)/2).getDegree();
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a number
             * from right side which is less then the pivot value. Once the search
             * is done, then we exchange both numbers.
             */
            while (terms.get(i).getDegree() > pivotDegree) {
                i++;
            }
            while (terms.get(j).getDegree() < pivotDegree) {
                j--;
            }
            if (i <= j) {
                exchangeTerms(i, j,terms);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(lowerIndex, j, terms);
        if (i < higherIndex)
            quickSort(i, higherIndex, terms);
    }
    private void exchangeTerms(int i,int j,ArrayList<Term> terms){
        Term t = terms.get(j);
        terms.set(j, terms.get(i));
        terms.set(i,t);
    }

    public String getFunction(){
        if(hasFunction) {
            return function;
        }else{
            return null;
        }
    }
    public boolean hasFunction(){
        return hasFunction;
    }
    public void setFunction(String s){
        this.function = s;
        hasFunction = true;
        // also need to look at the coeficient
    }

    public String toString(){
        return getStringRepresentation();
    }

    // two expressions are equal when all there terms are the same or like when they both have the same terms
    // so maybe sort them first then compare
    // this assumes that the expressions are simplified first
    // also this just checks if the expressions are equal and it ignores the function associated with it
    public boolean equal(Expression e){
        this.sortTerms(terms);
        e.sortTerms(e.getTerms());
        if(this.terms.size() != e.terms.size()){
            return false;// if they dont have the same number of terms then it is impossible for them to be equal--
        }
        for (int i = 0; i < terms.size(); i++) {
            if(!terms.get(i).equal(e.terms.get(i))){
                return false;
            }
        }
        if(this.hasFunction && e.hasFunction || !this.hasFunction && !e.hasFunction){
            if(this.hasFunction){ // then both have a funciton
                if(this.function.equals(e.function)){
                    return true;
                }
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }


    }

    /*
    so this is for adding two expressions if one of them has like a trig function
    then will return a mathstatement because might not be able to add them

    to add two trig functions the functions must be the same and the term on the inside(or expression) must be the same
     */
    public MathStatement addWithFunciont(Expression e){
        // dont want an if statemtnet checking for if both has a function becuase maybe only one does then have two expressions that cant be combined into one expression

        // actually need three checks- if both have a function, or if just one for each cause that would be different
        if(this.hasFunction && e.hasFunction){ // so like sin(x+3)+sin(x)
            if(this.equal(e)){
                double newCoeficient = this.funcitonCoeficient + e.funcitonCoeficient;
                Expression addedExpression = new Expression(function,newCoeficient,terms);
                return new MathStatement(addedExpression);
            }else{
                ArrayList<Expression> list = new ArrayList<>();
                list.add(this);
                list.add(e);
                return new MathStatement(list);
            }
        }else{
            ArrayList<Expression> list = new ArrayList<>();
            list.add(this);
            list.add(e);
            return new MathStatement(list);
        }

    }
}
