package tool;

import java.util.Random;
import java.util.Vector;

public class Tool {
    public static <T> Vector<T> random(Vector<T> data, int num){
        Vector<T> result = new Vector<>();
        int i = 0;
        while(i < num){
            Random random = new Random();
            int next = random.nextInt(data.size());
            result.add(data.get(next));
            data.remove(next);
            i++;
        }

        return result;
    }

    public static String toDisplayed(int value){
        if(value >= 0){
            return String.valueOf(value);
        } else {
            return "";
        }
    }
}
