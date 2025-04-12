package org.example.BusinessLogic;

import org.example.GUI.SimulationFrame;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogWriter {
    private static final Object logLock=new Object();
    private static final String TXTFile = "simulationLog.txt";
    private static SimulationFrame frame;
    private static PrintWriter logWriter;

    public static void start() {
        try{
            logWriter = new PrintWriter(new FileWriter(TXTFile, false));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static synchronized void log(String msg){
        synchronized(logLock){
            logWriter.println(msg);
            logWriter.flush();

            if (frame != null)
            {
                frame.log(msg);
            }
        }
    }

    public static void close() {
        if(logWriter != null){
            logWriter.close();
        }
    }

    //Logs for the simulation
    public static void setFrame(SimulationFrame frame) {
        LogWriter.frame = frame;
    }
}
