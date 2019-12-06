package core;

import javax.swing.table.AbstractTableModel;

public class ResultTableModel extends AbstractTableModel {

    public ResultTableModel(History history){
        this.history = history;
    }

    public void update(History history){
        this.history = history;
        fireTableDataChanged();
    }

    private History history;
    private String[] columnName = new String[] { "奖项","姓名","手机号码" };

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return columnName[column];
    }

    @Override
    public int getRowCount() {
        return history.data.size();
    }

    @Override
    public int getColumnCount() {
        return columnName.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String value = history.data.elementAt(rowIndex)[columnIndex];
        if(columnIndex == 2){ //对手机号码进行处理
            value = value.substring(0,9) + "**";
        }

        return value;
    }
}
