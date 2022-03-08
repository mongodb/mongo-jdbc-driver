package com.mongodb.jdbc.logging;

import com.mongodb.MongoException;
import com.mongodb.jdbc.MongoConnection;
import com.mongodb.jdbc.MongoDriver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;

public class MongoLoggerUtils {
    private static ConcurrentHashMap<Integer, Logger> loggerPerConnection =
            new ConcurrentHashMap<>();

    private static final MongoLogger DRIVER_LOGGER;

    public static MongoLogger getDriverLogger() {
        return DRIVER_LOGGER;
    }

    public static Logger initConnectionLogger(Integer connection_id, Level logLevel, File logDir) {
        Logger parentConnectionLogger = loggerPerConnection.get(connection_id);
        if (null != parentConnectionLogger) {
            // There is already a parent connection logger, there is a problem
            throw new MongoException("Parent connection logger already initialed. Ignored.");
        } else {
            Logger logger =
                    Logger.getLogger(
                            connection_id + "_" + MongoConnection.class.getCanonicalName());
            try {
                if (logLevel != null) {
                    if (logLevel != Level.OFF) {
                        // If log level is not OFF, create a new file handler for it
                        // Otherwise, don't bother
                        String logFileName = "connection_" + connection_id + ".log";
                        String logPath =
                                logDir == null
                                        ? logFileName
                                        : logDir.getAbsolutePath()
                                                + File.pathSeparator
                                                + logFileName;
                        FileHandler fileHandler = new FileHandler(logPath);
                        fileHandler.setFormatter(new SimpleFormatter());
                        logger.addHandler(fileHandler);
                    }

                    // Set the logger level
                    logger.setLevel(logLevel);
                }
                loggerPerConnection.put(connection_id, logger);
            } catch (IOException e) {
                // Can't log the error since it can't open the log file
                e.printStackTrace();
            }
            return logger;
        }
    }

    /**
     * Get a logger and tie it to the parent connection logger. If no parent connection exist,
     * initialize one at the same time with default log level and log dir will be the current
     * directory.
     *
     * @param className The classname to use for generating the logger name.
     * @param connection_id The connection id to retrieve the parent connection logger and generate
     *     the logger name.
     * @return the logger.
     */
    public static Logger getLogger(String className, Integer connection_id) {
        String loggername = connection_id == null ? className : connection_id + "_" + className;
        Logger logger = Logger.getLogger(loggername);
        Logger parentConnectionLogger = loggerPerConnection.get(connection_id);
        if (null == parentConnectionLogger) {
            parentConnectionLogger = initConnectionLogger(connection_id, null, null);
        }
        logger.setParent(parentConnectionLogger);

        return logger;
    }

    static {
        InputStream stream =
                MongoLoggerUtils.class.getClassLoader().getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().reset();
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger logger = Logger.getLogger(MongoDriver.class.getCanonicalName());
        try {

            String logFileName = "driver.log";
            FileHandler fileHandler = new FileHandler(logFileName);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            System.out.println(logger);
            System.out.println(logger.getHandlers()[0]);
            logger.setLevel(Level.FINER);

        } catch (IOException e) {
            // Can't log the error since it can't open the log file
            e.printStackTrace();
        }

        DRIVER_LOGGER = new MongoLogger(logger);
    }
}
