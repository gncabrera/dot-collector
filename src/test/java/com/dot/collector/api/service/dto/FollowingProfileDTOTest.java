package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FollowingProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FollowingProfileDTO.class);
        FollowingProfileDTO followingProfileDTO1 = new FollowingProfileDTO();
        followingProfileDTO1.setId(1L);
        FollowingProfileDTO followingProfileDTO2 = new FollowingProfileDTO();
        assertThat(followingProfileDTO1).isNotEqualTo(followingProfileDTO2);
        followingProfileDTO2.setId(followingProfileDTO1.getId());
        assertThat(followingProfileDTO1).isEqualTo(followingProfileDTO2);
        followingProfileDTO2.setId(2L);
        assertThat(followingProfileDTO1).isNotEqualTo(followingProfileDTO2);
        followingProfileDTO1.setId(null);
        assertThat(followingProfileDTO1).isNotEqualTo(followingProfileDTO2);
    }
}
