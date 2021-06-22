package me.sunstorm.showmanager.fileio;

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

import me.sunstorm.showmanager.util.Timecode;
import me.sunstorm.showmanager.WorkerThread;
import me.sunstorm.showmanager.gui.general.SchedulerTableModel;
import me.sunstorm.showmanager.schedule.OSCDataType;
import me.sunstorm.showmanager.schedule.ScheduleType;
import me.sunstorm.showmanager.schedule.ScheduledEvent;
import me.sunstorm.showmanager.schedule.ScheduledOSC;

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
                    ArrayList<ScheduledEvent> events = new ArrayList<>();
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.equals(CSV_HEADER)) {
                            String[] values = line.split(",");
                            String[] time = values[0].split(":");
                            Timecode timecode = new Timecode(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]), Integer.parseInt(time[3]), WorkerThread.getInstance().getFramerate());
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
                    ArrayList<ScheduledEvent> events = new ArrayList<>();
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
                    ArrayList<ScheduledOSC> oscEvents = new ArrayList<>();
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if ("#,Name,Start".equals(line))
                            continue;
                        String[] var = line.split(",");
                        Timecode timecode = null;
                        if (!var[0].equals("")) {
                            String[] time = var[2].split(":");
                            timecode = new Timecode(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]), Integer.parseInt(time[3]), WorkerThread.getInstance().getFramerate());
                        }
                        oscEvents.add(new ScheduledOSC(timecode, var[1], null, null));
                    }
                    reader.close();
                    model.setData(new ArrayList<>(oscEvents));
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
                    for (ScheduledEvent event : events) {
                        Timecode t = event.getExecTime();
                        String timeString = null;
                        if (t != null) {
                            timeString = t.getHour() + ":" + t.getMin() + ":" + t.getSec() + ":" + t.getFrame();
                        }
                        writer.println(nullable(timeString) + "," + nullable(event.getType()) + "," + nullable(event.getThirdColumn()) + "," + nullable(event.getFourthColumn()) + "," + nullable(event.getFifthColumn()));
                    }
                    writer.close();
                } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            }
            case JSON: {
                SerializedTableData serialized = new SerializedTableData();
                ArrayList<ScheduledEvent> genericList = model.getData();
                ArrayList<ScheduledOSC> oscList = new ArrayList<>();
                for (ScheduledEvent scheduledEvent : genericList) {
                    if (scheduledEvent instanceof ScheduledOSC) {
                        oscList.add((ScheduledOSC) scheduledEvent);
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
    
    public String nullable(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
