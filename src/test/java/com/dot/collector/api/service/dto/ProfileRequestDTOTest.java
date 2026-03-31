package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileRequestDTO.class);
        ProfileRequestDTO profileRequestDTO1 = new ProfileRequestDTO();
        profileRequestDTO1.setId(1L);
        ProfileRequestDTO profileRequestDTO2 = new ProfileRequestDTO();
        assertThat(profileRequestDTO1).isNotEqualTo(profileRequestDTO2);
        profileRequestDTO2.setId(profileRequestDTO1.getId());
        assertThat(profileRequestDTO1).isEqualTo(profileRequestDTO2);
        profileRequestDTO2.setId(2L);
        assertThat(profileRequestDTO1).isNotEqualTo(profileRequestDTO2);
        profileRequestDTO1.setId(null);
        assertThat(profileRequestDTO1).isNotEqualTo(profileRequestDTO2);
    }
}
