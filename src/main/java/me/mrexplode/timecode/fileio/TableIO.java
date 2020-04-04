package me.mrexplode.timecode.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import me.mrexplode.timecode.Timecode;
import me.mrexplode.timecode.gui.SchedulerTableModel;
import me.mrexplode.timecode.schedule.OSCDataType;
import me.mrexplode.timecode.schedule.ScheduleType;
import me.mrexplode.timecode.schedule.ScheduledEvent;
import me.mrexplode.timecode.schedule.ScheduledOSC;

public class TableIO {
    
    private static final String CSV_HEADER = "Time,Type,Path,DataType,Value";
    
    private Gson gson;
    private SchedulerTableModel model;
    
    public TableIO(SchedulerTableModel model) {
        this.gson = new Gson();
        this.model = model;
        
    }
    
    public boolean importData(File file, DataStructure structure) {
        if (file.exists() && file.isDirectory() || !file.exists())
            return false;
        switch (structure) {
            case CSV: {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    ArrayList<ScheduledEvent> events = new ArrayList<ScheduledEvent>();
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.equals(CSV_HEADER)) {
                            continue;
                        }
                        String[] values = line.split(",");
                        String[] time = values[0].split(":");
                        Timecode timecode = new Timecode(Integer.valueOf(time[0]), Integer.valueOf(time[1]), Integer.valueOf(time[2]), Integer.valueOf(time[3]));
                        ScheduleType scType = ScheduleType.valueOf(values[1]);
                        
                        ScheduledEvent event = new ScheduledEvent(null, null);
                        switch (scType) {
                            case OSC:
                                event = new ScheduledOSC(timecode, values[2], OSCDataType.valueOf(values[3]), values[4]);
                            break;
                            case INTERNAL:
                                event = new ScheduledEvent(scType, timecode);
                            break;
                            default:
                            break;
                        }
                        events.add(event);
                    }
                    model.setData(events);
                    model.sort();
                    reader.close();
                } catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            case JSON: {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    SerializedTableData serialized = gson.fromJson(reader, SerializedTableData.class);
                    reader.close();
                    ArrayList<ScheduledEvent> events = new ArrayList<ScheduledEvent>();
                    events.addAll(Arrays.asList(serialized.generalEvents));
                    events.addAll(Arrays.asList(serialized.oscEvents));
                    model.setData(events);
                    model.sort();
                } catch (IOException | JsonIOException | JsonSyntaxException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            case REAPER_MARKER: {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    ArrayList<ScheduledOSC> oscEvents = new ArrayList<ScheduledOSC>();
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("#,Name,Start"))
                            continue;
                        String[] var = line.split(",");
                        String[] time = var[2].split(":");
                        Timecode timecode = new Timecode(Integer.valueOf(time[0]), Integer.valueOf(time[1]), Integer.valueOf(time[2]), Integer.valueOf(time[3]));
                        oscEvents.add(new ScheduledOSC(timecode, var[1], null, null));
                    }
                    reader.close();
                    model.setData(new ArrayList<ScheduledEvent>(oscEvents));
                    model.sort();
                    
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            default:
                break;
            
        }
        return true;
    }
    
    public boolean exportData(File file, DataStructure structure) {
        if (file.exists() && file.isDirectory())
            return false;
        switch (structure) {
            case CSV: {
                try {
                    PrintWriter writer = new PrintWriter(new FileOutputStream(file));
                    ArrayList<ScheduledEvent> events = model.getData();
                    writer.println(CSV_HEADER);
                    for (int i = 0; i < events.size(); i++) {
                        ScheduledEvent event = events.get(i);
                        Timecode t = event.getExecTime();
                        String timeString = t.getHour() + ":" + t.getMin() + ":" + t.getSec() + ":" + t.getFrame();
                        writer.println(timeString + "," + event.getType() + "," + event.getThirdColumn() + "," + event.getFourthColumn() + "," + event.getFifthColumn());
                    }
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            case JSON: {
                SerializedTableData serialized = new SerializedTableData();
                ArrayList<ScheduledEvent> genericList = model.getData();
                ArrayList<ScheduledOSC> oscList = new ArrayList<ScheduledOSC>();
                for (int i = 0; i < genericList.size(); i++) {
                    if (genericList.get(i) instanceof ScheduledOSC) {
                        oscList.add((ScheduledOSC) genericList.get(i));
                    }
                }
                genericList.removeAll(oscList);
                serialized.generalEvents = genericList.toArray(new ScheduledEvent[genericList.size()]);
                serialized.oscEvents = oscList.toArray(new ScheduledOSC[oscList.size()]);
                try {
                    PrintWriter writer = new PrintWriter(new FileOutputStream(file), false);
                    gson.toJson(serialized, writer);
                    writer.flush();
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            } 
            case REAPER_MARKER:
                break;
            default:
                break;
            
        }
        return true;
    }
}
