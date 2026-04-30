package com.nookx.api.repository.projection;

import java.time.LocalDate;

public interface MegaSetNewsHitProjection {
    Long getId();
    LocalDate getReleaseDate();
    Integer getPriority();
}
