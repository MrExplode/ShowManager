package me.mrexplode.timecode.gui;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import me.mrexplode.timecode.schedule.ScheduleType;
import me.mrexplode.timecode.schedule.ScheduledEvent;


public class SchedulerTableModel extends AbstractTableModel implements TableModelListener {

    private static final long serialVersionUID = 458498290356140162L;
    
    private String[] columnNames = new String[] {"Time", "Type", "Path", "Data Type", "Value"};
    private int index = 0;
    private ArrayList<ScheduledEvent> data;
    
    public SchedulerTableModel() {
        data = new ArrayList<ScheduledEvent>();
        data.add(new ScheduledEvent(null, null));
    }
    
    public void addEvent()

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        SchedulerTableModel model = (SchedulerTableModel) e.getSource();
        if (row == model.getRowCount() - 1) {
            //model.addRow((Object[]) null);
        }
        
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex > 4)
            throw new IllegalArgumentException("higher column index");
        
        if (rowIndex > data.size() - 1)
            throw new IllegalArgumentException("higher row index");
        
        ScheduledEvent sch = data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return sch.getExecTime();
            case 1:
                return sch.getType();
            case 2:
                return null;
            case 3:
                return null;
            case 4:
                return null;
            default:
                return null;
        }
    }
    
    @Override
    public String getColumnName(int column) {
        if (column > columnNames.length - 1) {
            return super.getColumnName(column);
        } else {
            return columnNames[column];
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

}
