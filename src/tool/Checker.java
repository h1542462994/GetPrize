package tool;

public class Checker {
    public static boolean isPhoneNumber(String number){
        if(number.length() != 11)
            return false;
        return number.matches("[0-9]*");
    }

    public static boolean isName(String name){
        return name.matches("^([\\u4e00-\\u9fa5]{1,20}|[0-9a-zA-Z.\\s]{1,20})$");
    }

}
