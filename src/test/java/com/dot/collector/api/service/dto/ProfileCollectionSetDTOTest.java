package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileCollectionSetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileCollectionSetDTO.class);
        ProfileCollectionSetDTO profileCollectionSetDTO1 = new ProfileCollectionSetDTO();
        profileCollectionSetDTO1.setId(1L);
        ProfileCollectionSetDTO profileCollectionSetDTO2 = new ProfileCollectionSetDTO();
        assertThat(profileCollectionSetDTO1).isNotEqualTo(profileCollectionSetDTO2);
        profileCollectionSetDTO2.setId(profileCollectionSetDTO1.getId());
        assertThat(profileCollectionSetDTO1).isEqualTo(profileCollectionSetDTO2);
        profileCollectionSetDTO2.setId(2L);
        assertThat(profileCollectionSetDTO1).isNotEqualTo(profileCollectionSetDTO2);
        profileCollectionSetDTO1.setId(null);
        assertThat(profileCollectionSetDTO1).isNotEqualTo(profileCollectionSetDTO2);
    }
}
