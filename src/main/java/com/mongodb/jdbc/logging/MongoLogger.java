package com.mongodb.jdbc.logging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public class MongoLogger {
    private static ConcurrentHashMap<Integer, Logger> loggerPerConnection = new ConcurrentHashMap<>();

    public static Logger getLogger(String className, int connection_id)  {
        // get the global logger to configure it
        Logger logger = Logger.getLogger("["+connection_id+"] "+className);


        logger.setLevel(Level.FINER);

        Logger parentConnectionLogger = loggerPerConnection.get(connection_id);
        if (null != parentConnectionLogger) {
            logger.setParent(parentConnectionLogger);
        } else
        {
            try {
                FileHandler fileTxt = new FileHandler("Logging_" + connection_id + ".txt");

                // create a TXT formatter
                SimpleFormatter formatterTxt = new SimpleFormatter();
                fileTxt.setFormatter(formatterTxt);
                logger.addHandler(fileTxt);
                loggerPerConnection.put(connection_id, logger);
            } catch (IOException e) {
                // Can't log since it can't open the log file
                e.printStackTrace();
            }
        }

        return logger;
    }

    public static void logMethodEntry(Logger logger, String methodName)
    {
        logger.log(Level.FINE, ">> " +  methodName + "()");
    }

    static {
        // Suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        String initialString = "java.util.logging.SimpleFormatter.format=[%1$tF %1$tT] [%4$s] %3$s: %5$s %6$s %n";
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        try {
            LogManager.getLogManager().readConfiguration(targetStream);
        } catch (IOException e) {
            // Will use generic SimpleFormatter.format instead of the one define here
            e.printStackTrace();
        }
    }
}
