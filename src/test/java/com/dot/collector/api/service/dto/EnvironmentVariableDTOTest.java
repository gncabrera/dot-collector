package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnvironmentVariableDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvironmentVariableDTO.class);
        EnvironmentVariableDTO environmentVariableDTO1 = new EnvironmentVariableDTO();
        environmentVariableDTO1.setId(1L);
        EnvironmentVariableDTO environmentVariableDTO2 = new EnvironmentVariableDTO();
        assertThat(environmentVariableDTO1).isNotEqualTo(environmentVariableDTO2);
        environmentVariableDTO2.setId(environmentVariableDTO1.getId());
        assertThat(environmentVariableDTO1).isEqualTo(environmentVariableDTO2);
        environmentVariableDTO2.setId(2L);
        assertThat(environmentVariableDTO1).isNotEqualTo(environmentVariableDTO2);
        environmentVariableDTO1.setId(null);
        assertThat(environmentVariableDTO1).isNotEqualTo(environmentVariableDTO2);
    }
}
