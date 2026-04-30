package com.nookx.api.client.cursor;

import java.time.LocalDate;

public record DashboardLatestReleasedCursorPosition(Integer priority, LocalDate releaseDate, Long id) {}
