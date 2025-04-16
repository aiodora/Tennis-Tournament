package com.sd.tennis.factory;

import com.sd.tennis.util.CsvExportUtil;
import com.sd.tennis.util.ExportStrategy;
import com.sd.tennis.util.TxtExportUtil;

public class ExportStrategyFactory {
    public static ExportStrategy getStrategy(String format) {
        if ("csv".equalsIgnoreCase(format)) {
            return new CsvExportUtil();
        } else if ("txt".equalsIgnoreCase(format)) {
            return new TxtExportUtil();
        }
        throw new IllegalArgumentException("Invalid format: " + format);
    }
}

