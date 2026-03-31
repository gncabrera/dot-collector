package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileRequestTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileRequestTypeDTO.class);
        ProfileRequestTypeDTO profileRequestTypeDTO1 = new ProfileRequestTypeDTO();
        profileRequestTypeDTO1.setId(1L);
        ProfileRequestTypeDTO profileRequestTypeDTO2 = new ProfileRequestTypeDTO();
        assertThat(profileRequestTypeDTO1).isNotEqualTo(profileRequestTypeDTO2);
        profileRequestTypeDTO2.setId(profileRequestTypeDTO1.getId());
        assertThat(profileRequestTypeDTO1).isEqualTo(profileRequestTypeDTO2);
        profileRequestTypeDTO2.setId(2L);
        assertThat(profileRequestTypeDTO1).isNotEqualTo(profileRequestTypeDTO2);
        profileRequestTypeDTO1.setId(null);
        assertThat(profileRequestTypeDTO1).isNotEqualTo(profileRequestTypeDTO2);
    }
}
