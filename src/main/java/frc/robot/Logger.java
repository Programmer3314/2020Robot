/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import edu.wpi.first.wpilibj.Timer;

/**
 * Add your docs here.
 */
public class Logger {

    public static boolean Enabled = false;
    private static StringBuilder sb = null;
    private static String fileName;
    private static boolean firstWrite;

    public static void OpenLog(String prefix) {
        if(Enabled) {
            sb = new StringBuilder();
            firstWrite = true;
            sb.append("Time,");
            fileName = "/home/lvuser/"+prefix+"Logging.txt";
        }
    }

    public static void Header(String h) {
        if(sb!=null) {
            sb.append(h);
        }
    }

    public static void StartLine() {
        if(sb!=null) {
            sb.append(Timer.getFPGATimestamp());
            sb.append(",");
        }

    }

    public static void EndLine() {
        if (sb!=null) {
            sb.append("\n");
        }
    }

    public static void WriteLog() {
        try {
            if (sb!=null) {
                Files.writeString(
                    Paths.get(fileName), 
                    sb.toString(), 
                    StandardOpenOption.CREATE, 
                    firstWrite?StandardOpenOption.TRUNCATE_EXISTING:StandardOpenOption.APPEND);
                sb = new StringBuilder();
                firstWrite=false;
            }
        } catch(Exception e) {
        }
    }

    public static void CloseLog() {
        WriteLog();
        sb=null;
    }

    public static void doubles(double... p) {
        if (sb!=null) {
            for(double d : p) {
                sb.append(d);
                sb.append(",");
            }
        }
    }

    public static void booleans(boolean... p) {
        if (sb!=null) {
            for(boolean b : p) {
                sb.append(b?"T":"_");
                sb.append(",");
            }
        }
    }

    public static void strings(String... p) {
        if (sb!=null) {
            for(String s : p) {
                sb.append(s);
                sb.append(",");
            }
        }
    }

    public static <T extends Enum<T>> void singleEnum(Enum<T> e) {
        if (sb!=null) {
                sb.append(e.name());
                sb.append(",");
        }
    }
   
}
