package net.cloud.autosell.files.logs;

import lombok.Getter;
import lombok.Setter;
import net.cloud.autosell.AutoSell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Debugger {

    private AutoSell plugin;
    public Debugger(AutoSell autoSell, String filePath) {
        this.plugin = autoSell;
        this.filePath = filePath;
        this.simpleDateFormat = new SimpleDateFormat("MM:dd kk:mm:ss");

        setLogs(new ArrayList<>());
    }

    @Getter private String filePath;
    @Getter private File file;
    @Getter @Setter private PrintWriter writer;
    @Getter @Setter private ArrayList<String> logs;

    @Getter private SimpleDateFormat simpleDateFormat;

    public void setFile(File fileObject) {
        file = fileObject;
        if(!(file.exists())) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            if(getFile() == null) setFile(new File(filePath));
            if(getWriter() == null) setWriter(new PrintWriter(new FileOutputStream(getFilePath(), true)));
            if(exists()) {
                logs.forEach(args -> getWriter().write(args + System.lineSeparator()));
                getWriter().flush();
                getWriter().close();;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        try {
            if(getFile() == null) setFile(new File(filePath));
            if(getWriter() == null) setWriter(new PrintWriter(new FileOutputStream(getFilePath(), true)));
            if(exists()) {
                logs.forEach(args -> getWriter().write(args + System.lineSeparator()));
                setLogs(new ArrayList<>());
                getWriter().flush();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean exists() {
        try {
            if(file == null) return false;
            if(!file.exists()) {
                file.createNewFile();
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearFile() {
        try {
            if(getFile() == null) setFile(new File(filePath));
            if(getWriter() == null) setWriter(new PrintWriter(new FileOutputStream(getFilePath(), false)));

            getWriter().write("");
            getWriter().flush();
            getWriter().close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addLog(String args) {
        logs.add(getFormat() + " " + args);
    }

    public String getFormat() {
        return "[" + simpleDateFormat.format(new Date()) + "]";
    }

}
