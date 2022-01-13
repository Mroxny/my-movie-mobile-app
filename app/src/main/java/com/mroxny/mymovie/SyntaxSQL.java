package com.mroxny.mymovie;

public class SyntaxSQL {

    public static String correctStatement(String statement){
        if(statement.contains("'")){
            int i = 0;
            while(i<statement.length()){
                if(statement.charAt(i) == '\''){
                    statement = statement.substring(0,i) + "'" + statement.substring(i, statement.length());
                    i+=2;
                }
                else i++;
            }
        }
        return statement;
    }

}
