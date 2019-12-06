package core;

import com.sun.istack.internal.NotNull;

import java.util.Vector;

public class History {
    @NotNull
    public String title;
    public Vector<String[]> data = new Vector<>();
}
