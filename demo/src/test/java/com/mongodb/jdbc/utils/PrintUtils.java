package com.mongodb.jdbc.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrintUtils {
    private static final String NULL_STR = "null";
    private static int MAX_RS_META_COL_WIDTH = 12;
    public static int MAX_COL_WIDTH = 20;

    public static void printResultSetMetadata(ResultSetMetaData rsMeta)  throws SQLException
    {
        try {
            int columnCount = rsMeta.getColumnCount();
            StringBuilder sb = new StringBuilder();
            List<String> colNames = new ArrayList<String>();
            colNames.add("TABLE_CAT");
            colNames.add("TABLE_SCHEMA");
            colNames.add("TABLE_NAME");
            colNames.add("COLUMN_NAME");
            colNames.add("DATA_TYPE");
            colNames.add("TYPE_NAME");
            colNames.add("DISPLAY_SIZE");
            colNames.add("PRECISION");
            colNames.add("SCALE");
            colNames.add("CLASS_NAME");
            colNames.add("LABEL");

            printRowSeparator(sb, colNames.size(), MAX_RS_META_COL_WIDTH);
            for (int i = 0; i < colNames.size(); i++)
            {
                sb.append(String.format(getFormat(MAX_RS_META_COL_WIDTH), colNames.get(i)));
            }
            sb.append("|\n");
            printRowSeparator(sb, colNames.size(), MAX_RS_META_COL_WIDTH);

            for (int i = 1; i <= columnCount; i++) {
                boolean hasMoreData = true;
                String[] row =  new String[11];
                row[0] = rsMeta.getCatalogName(i);
                row[1] = rsMeta.getSchemaName(i);
                row[2] = rsMeta.getTableName(i);
                row[3] = rsMeta.getColumnName(i);
                row[4] = String.valueOf(rsMeta.getColumnType(i));
                row[5] = rsMeta.getColumnTypeName(i);
                row[6] = String.valueOf(rsMeta.getColumnDisplaySize(i));
                row[7] = String.valueOf(rsMeta.getPrecision(i));
                row[8] = String.valueOf(rsMeta.getScale(i));
                row[9] = rsMeta.getColumnClassName(i);
                row[10] = rsMeta.getColumnLabel(i);

                while (hasMoreData) {
                    hasMoreData = false;
                    for (int j = 0; j < row.length; j++) {
                        String data = row[j];
                        int cutIndex = Math.min(data.length(), MAX_RS_META_COL_WIDTH);
                        sb.append(String.format(getFormat(MAX_RS_META_COL_WIDTH), data.substring(0, cutIndex)));
                        if (cutIndex == MAX_RS_META_COL_WIDTH) {
                            row[j] = data.substring(cutIndex);
                            hasMoreData = true;
                        }
                        else
                        {
                            row[j] = "";
                        }
                    }
                    sb.append("|\n");
                }
            }
            printRowSeparator(sb, colNames.size(), MAX_RS_META_COL_WIDTH);

            System.out.println(sb.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public static void printResultSet(ResultSet rs) throws SQLException
    {
        try {
            // Get the metadata of this rs.
            ResultSetMetaData rsMeta;
            rsMeta = rs.getMetaData();

            int columnCount = rsMeta.getColumnCount();

            StringBuilder sb = new StringBuilder();

            int[] maxColsWidth = printRsHeader(sb, columnCount, rsMeta);
            printRsContents(sb, columnCount, rs, maxColsWidth);

            System.out.println(sb.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    private static String getFormat(int maxColWidth) throws SQLException
    {
        return "| %-" + maxColWidth + "s ";
    }

    private static void printRsContents(StringBuilder sb, int columnCount, ResultSet rs, int[] maxColsWidth) throws SQLException
    {
        String data = null;
        String cellValue;
        while (rs.next()) {
            String[] row = new String[columnCount];

            boolean hasMoreData;
            do {
                hasMoreData = false;
                for (int i = 0; i < columnCount; i++) {
                    if (row[i] == null) {
                        cellValue = rs.getString(i + 1);
                        row[i] = (cellValue == null ? NULL_STR : cellValue);
                    }
                    int cutIndex = Math.min(row[i].length(), maxColsWidth[i]);
                    data = row[i].substring(0, cutIndex);
                    if (cutIndex == maxColsWidth[i])
                    {
                        row[i] = row[i].substring(cutIndex);
                        hasMoreData = true;
                    }
                    else
                    {
                        row[i] = "";
                    }
                    sb.append(String.format(getFormat(maxColsWidth[i]), data));
                }
                sb.append("|\n");
            } while (hasMoreData);
        }
        printRowSeparator(sb, columnCount, maxColsWidth);
    }

    private static int[] printRsHeader(StringBuilder sb, int columnCount, ResultSetMetaData rsMeta) throws SQLException
    {
        String[] col_names = new String[columnCount];
        int[] maxColWidth = new int[columnCount];
        for (int i = 0; i < columnCount; i++)
        {
            maxColWidth[i] = MAX_COL_WIDTH;
            col_names[i] =  rsMeta.getColumnName(i+1);
            if (col_names[i].length() > maxColWidth[i])
            {
                maxColWidth[i] = col_names[i].length();
            }
        }

        printRowSeparator(sb, columnCount, maxColWidth);
        for (int i = 0; i < columnCount; i++)
        {
            sb.append(String.format(getFormat(maxColWidth[i]), col_names[i]));
        }
        sb.append("|\n");
        printRowSeparator(sb, columnCount, maxColWidth);

        return maxColWidth;
    }

    private static void printRowSeparator(StringBuilder sb, int columnCount, int[] maxColsWidth)
    {
        sb.append("+");
        for (int i = 0; i < columnCount; i++) {
            sb.append(new String(new char[maxColsWidth[i] + 2]).replace("\0", "-"));
            sb.append("|");
        }
        sb.replace(sb.length() - 1, sb.length(), "+\n");
    }

    private static void printRowSeparator(StringBuilder sb, int columnCount, int maxColsWidth)
    {
        sb.append("+");
        for (int i = 0; i < columnCount; i++) {
            sb.append(new String(new char[maxColsWidth + 2]).replace("\0", "-"));
            sb.append("|");
        }
        sb.replace(sb.length() - 1, sb.length(), "+\n");
    }
}
