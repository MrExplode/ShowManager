package me.mrexplode.timecode.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import me.mrexplode.timecode.Timecode;
import me.mrexplode.timecode.schedule.OSCDataType;
import me.mrexplode.timecode.schedule.ScheduleType;
import me.mrexplode.timecode.schedule.ScheduledEvent;
import me.mrexplode.timecode.schedule.ScheduledOSC;


public class SchedulerTableModel extends AbstractTableModel implements TableModelListener {

    private static final long serialVersionUID = 458498290356140162L;
    
    private String[] columnNames = new String[] {"Time", "Type", "Path", "Data Type", "Value"};
    private ArrayList<ScheduledEvent> data;
    
    private boolean editable = true;
    
    public SchedulerTableModel() {
        data = new ArrayList<ScheduledEvent>();
        data.add(new ScheduledEvent(null, null));
    }
    
    public void insertEmptyRow(int index) {
        data.add(index, new ScheduledEvent(null, null));
        fireTableRowsInserted(index, index);
    }
    
    public void inserRow(int index, ScheduledEvent event) {
        data.add(index, event);
        fireTableRowsInserted(index, index);
    }
    
    public ScheduledEvent getEvent(int index) {
        return data.get(index);
    }
    
    public void sort() {
        Collections.sort((List<ScheduledEvent>) data);
        fireTableDataChanged();
    }
    
    public List<ScheduledEvent> getCurrentFor(Timecode current) {
        ArrayList<ScheduledEvent> curr = new ArrayList<ScheduledEvent>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getExecTime() != null && data.get(i).getExecTime().equals(current)) {
                curr.add(data.get(i));
            }
        }
        if (curr.isEmpty()) {
            return null;
        }
        return curr;
    }
    
    public void setEditable(boolean value) {
        this.editable = value;
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
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex > 4)
            throw new IllegalArgumentException("higher column index");
        
        if (rowIndex > data.size() - 1)
            throw new IllegalArgumentException("higher row index");
        
        ScheduledEvent sch = data.get(rowIndex);
        switch (columnIndex) {
            //time
            case 0:
                if (aValue instanceof Timecode) {
                    sch.setExecTime((Timecode) aValue);
                    fireTableCellUpdated(rowIndex, columnIndex);
                }
            break;
            //type
            case 1:
                if (aValue instanceof ScheduleType) {
                    ScheduleType newType = (ScheduleType) aValue;
                    if (sch.getType() != newType) {
                        switch (newType) {
                            case INTERNAL:
                                break;
                            case OSC:
                                Timecode time = sch.getExecTime();
                                //sch = new ScheduledOSC(time, null, null, null);
                                data.set(rowIndex, new ScheduledOSC(time, null, null, null));
                                fireTableRowsUpdated(rowIndex, rowIndex);
                                break;
                            default:
                                break;
                        }
                    }
                }
            break;
            //path
            case 2:
                if (sch.getType() == ScheduleType.OSC) {
                    if (aValue instanceof String) {
                        ((ScheduledOSC) sch).setPath((String) aValue);
                        fireTableCellUpdated(rowIndex, columnIndex);
                    }
                }
            break;
            //datatype
            case 3:
                if (sch.getType() == ScheduleType.OSC) {
                    if (aValue instanceof OSCDataType) {
                        ((ScheduledOSC) sch).setDataType((OSCDataType) aValue);
                        fireTableRowsUpdated(rowIndex, rowIndex);
                    }
                }
            break;
            //value
            case 4:
                if (sch.getType() == ScheduleType.OSC) {
                    if (aValue instanceof String) {
                        ((ScheduledOSC) sch).setValue((String) aValue);
                        fireTableRowsUpdated(rowIndex, rowIndex);
                    }
                }
            break;
            default:
                break;
        }
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
                return sch.getFirstColumn();
            case 1:
                return sch.getSecondColumn();
            case 2:
                return sch.getThirdColumn();
            case 3:
                return sch.getFourthColumn();
            case 4:
                return sch.getFifthColumn();
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
        return editable;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
    }

}
