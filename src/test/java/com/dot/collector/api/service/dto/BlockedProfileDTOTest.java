package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BlockedProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlockedProfileDTO.class);
        BlockedProfileDTO blockedProfileDTO1 = new BlockedProfileDTO();
        blockedProfileDTO1.setId(1L);
        BlockedProfileDTO blockedProfileDTO2 = new BlockedProfileDTO();
        assertThat(blockedProfileDTO1).isNotEqualTo(blockedProfileDTO2);
        blockedProfileDTO2.setId(blockedProfileDTO1.getId());
        assertThat(blockedProfileDTO1).isEqualTo(blockedProfileDTO2);
        blockedProfileDTO2.setId(2L);
        assertThat(blockedProfileDTO1).isNotEqualTo(blockedProfileDTO2);
        blockedProfileDTO1.setId(null);
        assertThat(blockedProfileDTO1).isNotEqualTo(blockedProfileDTO2);
    }
}
