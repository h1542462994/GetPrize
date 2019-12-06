package core;

import tool.Checker;

import javax.swing.table.AbstractTableModel;
import java.io.*;
import java.util.Iterator;
import java.util.Vector;

public class PeopleTableModel extends AbstractTableModel implements Iterable<String[]> {
    private String[] columnName = new String[] {"姓名", "手机号"};
    private Vector<String[]> data;

    public PeopleTableModel(){
        data = new Vector<>();
        data.add(new String[] {"",""});
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public String getColumnName(int column) {
        return columnName[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnName.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.elementAt(rowIndex)[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String value = (String)aValue;
        if(columnIndex == 1){ //手机号判断，如判断为非法手机号，自动置为空
            if(!Checker.isPhoneNumber(value))
                value = "";
        } else {
            if(!Checker.isName(value))
                value = "";
        }

        String[] element = data.get(rowIndex);
        element[columnIndex] = value;

        if(rowIndex == data.size() - 1){ //为最后一项
            if(!isElementEmpty(element)){ //不为空
                data.setElementAt(element, rowIndex);
                data.add(new String[]{"",""});
                fireTableCellUpdated(rowIndex, columnIndex);
                fireTableRowsInserted(rowIndex + 1, rowIndex + 1);
            }
        } else{
            if(isElementEmpty(element)){
                data.remove(rowIndex);
                fireTableRowsDeleted(rowIndex, rowIndex);
            } else {
                data.setElementAt(element, rowIndex);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }



    }

    @Override
    public Iterator<String[]> iterator() {
        return data.iterator();
    }

    public Vector<String[]> available(){
        Vector<String[]> result = new Vector<>();
        for (String[] element: data) {
            if(!element[0].isEmpty() && !element[1].isEmpty()){
                result.add(element);
            }
        }
        return result;
    }

    public void exportToFile(File file){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String[] element: data) {
                if(!isElementEmpty(element)){
                    String line = String.format("%s;%s",element[0],element[1]);
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void importFromFile(File file){
        Vector<String[]> data= new Vector<>();
        try( BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            do {
                line = reader.readLine();
                if(line == null || line.isEmpty())
                    break;
                data.add(line.split(";"));
            } while (true);


            data.add(new String[]{"",""});
            this.data = data;
            fireTableDataChanged();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    private boolean isElementEmpty(String[] element){
        return element[0].isEmpty() && element[1].isEmpty();
    }
}
