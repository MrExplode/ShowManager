package me.mrexplode.timecode.gui.editors;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import me.mrexplode.timecode.schedule.OSCDataType;
import me.mrexplode.timecode.schedule.ScheduleType;


public class CustomCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = -4022413413794081501L;
    
    private TableCellEditor editor;

    @Override
    public Object getCellEditorValue() {
        if (editor != null) {
            editor.getCellEditorValue();
        }
        return null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof ScheduleType || (value == null && column == 1)) {
            JComboBox<ScheduleType> scType = new JComboBox<ScheduleType>();
            for (ScheduleType t : ScheduleType.values()) {
                scType.addItem(t);
            }
            
            editor = new SelectionCellEditor(scType, table);
        //data type
        } else if (value instanceof OSCDataType || (value == null && column == 3)) {
            JComboBox<OSCDataType> dataTypes = new JComboBox<OSCDataType>();
            for (OSCDataType t : OSCDataType.values()) {
                dataTypes.addItem(t);
            }
            
            editor = new SelectionCellEditor(dataTypes, table);
        //strings
        } else if (value instanceof String) {
            editor = new StringCellEditor(new JTextField(), table);
        //boolean
        } else if (value instanceof Boolean) {
            editor = new DefaultCellEditor(new JCheckBox());
        } else {
            //null, or anything else
            editor = new StringCellEditor(new JTextField(), table);
        }
        return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

}
