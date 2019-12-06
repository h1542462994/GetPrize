package core;

import javax.swing.table.AbstractTableModel;

public class HistoryTableModel extends AbstractTableModel {
    private Cache cache;
    public HistoryTableModel(Cache cache){
        this.cache = cache;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return "记录";
    }

    @Override
    public int getRowCount() {
        return cache.data.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return cache.data.get(rowIndex).title;
    }
}
