package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileCollectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileCollectionDTO.class);
        ProfileCollectionDTO profileCollectionDTO1 = new ProfileCollectionDTO();
        profileCollectionDTO1.setId(1L);
        ProfileCollectionDTO profileCollectionDTO2 = new ProfileCollectionDTO();
        assertThat(profileCollectionDTO1).isNotEqualTo(profileCollectionDTO2);
        profileCollectionDTO2.setId(profileCollectionDTO1.getId());
        assertThat(profileCollectionDTO1).isEqualTo(profileCollectionDTO2);
        profileCollectionDTO2.setId(2L);
        assertThat(profileCollectionDTO1).isNotEqualTo(profileCollectionDTO2);
        profileCollectionDTO1.setId(null);
        assertThat(profileCollectionDTO1).isNotEqualTo(profileCollectionDTO2);
    }
}
