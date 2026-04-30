package com.nookx.api.domain.enumeration;

public enum ReportCategory {
    VISUAL_ISSUE("UI/Visual issue", ReportType.BUG),
    FEATURE_NOT_WORKING("Feature not working", ReportType.BUG),
    DATA_DISPLAY_ERROR("Data display error", ReportType.BUG),
    PERFORMANCE_ISSUE("Performance issue", ReportType.BUG),
    LOGIN_ISSUE("Login/auth issue", ReportType.BUG),
    CRASH_FREEZE("Crash/freeze", ReportType.BUG),
    OTHER_BUG("Other", ReportType.BUG),

    SPAM("Spam", ReportType.COMMENT),
    HARASSMENT("Harassment or abuse", ReportType.COMMENT),
    INAPPROPRIATE_CONTENT("Inappropriate content", ReportType.COMMENT),
    OTHER_COMMENT("Other", ReportType.COMMENT);

    private final String displayName;
    private final ReportType reportType;

    ReportCategory(String displayName, ReportType reportType) {
        this.displayName = displayName;
        this.reportType = reportType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ReportType getReportType() {
        return reportType;
    }
}
