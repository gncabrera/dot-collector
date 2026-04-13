package com.nookx.api.domain;

import static com.nookx.api.domain.EnvironmentVariableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnvironmentVariableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvironmentVariable.class);
        EnvironmentVariable environmentVariable1 = getEnvironmentVariableSample1();
        EnvironmentVariable environmentVariable2 = new EnvironmentVariable();
        assertThat(environmentVariable1).isNotEqualTo(environmentVariable2);

        environmentVariable2.setId(environmentVariable1.getId());
        assertThat(environmentVariable1).isEqualTo(environmentVariable2);

        environmentVariable2 = getEnvironmentVariableSample2();
        assertThat(environmentVariable1).isNotEqualTo(environmentVariable2);
    }
}
