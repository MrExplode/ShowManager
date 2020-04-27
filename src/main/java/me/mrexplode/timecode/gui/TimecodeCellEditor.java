package me.mrexplode.timecode.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import me.mrexplode.timecode.Timecode;
import me.mrexplode.timecode.WorkerThread;


public class TimecodeCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private static final long serialVersionUID = -4616510380884099210L;
    
    private JTextField hour = new JTextField(3);
    private JTextField min = new JTextField(3);
    private JTextField sec = new JTextField(3);
    private JTextField frame = new JTextField(3);
    private JPanel panel = new JPanel();
    private JButton button = new JButton();
    private static final String EDIT = "edit";
    
    private Timecode oldValue;
    private Timecode newValue;
    
    public TimecodeCellEditor() {
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setBackground(Color.WHITE);
    }

    @Override
    public Object getCellEditorValue() {
        return newValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        oldValue = (Timecode) value;
        newValue = (Timecode) value;
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            hour.setText("" + (oldValue == null ? "" : oldValue.getHour()));
            min.setText("" + (oldValue == null ? "" : oldValue.getMin()));
            sec.setText("" + (oldValue == null ? "" : oldValue.getSec()));
            frame.setText("" + (oldValue == null ? "" : oldValue.getFrame()));
            panel.add(hour);
            panel.add(min);
            panel.add(sec);
            panel.add(frame);
            int result = JOptionPane.showConfirmDialog(null, panel, "Specify time", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
            if (result == JOptionPane.CLOSED_OPTION || result == JOptionPane.CANCEL_OPTION) {
                newValue = oldValue;
                fireEditingCanceled();
                return;
            }
            
            try {
                int hourValue = Integer.valueOf(hour.getText());
                int minValue = Integer.valueOf(min.getText());
                int secValue = Integer.valueOf(sec.getText());
                int frameValue = Integer.valueOf(frame.getText());
                
                newValue = new Timecode(hourValue, minValue, secValue, frameValue, WorkerThread.getFramerate());
                fireEditingStopped();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "You fucking dumbass!", "Number format exception", JOptionPane.ERROR_MESSAGE, null);
                fireEditingCanceled();
            }
            
        }
    }

}
