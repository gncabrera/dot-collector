package com.nookx.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaPartSubPartCountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaPartSubPartCountDTO.class);
        MegaPartSubPartCountDTO megaPartSubPartCountDTO1 = new MegaPartSubPartCountDTO();
        megaPartSubPartCountDTO1.setId(1L);
        MegaPartSubPartCountDTO megaPartSubPartCountDTO2 = new MegaPartSubPartCountDTO();
        assertThat(megaPartSubPartCountDTO1).isNotEqualTo(megaPartSubPartCountDTO2);
        megaPartSubPartCountDTO2.setId(megaPartSubPartCountDTO1.getId());
        assertThat(megaPartSubPartCountDTO1).isEqualTo(megaPartSubPartCountDTO2);
        megaPartSubPartCountDTO2.setId(2L);
        assertThat(megaPartSubPartCountDTO1).isNotEqualTo(megaPartSubPartCountDTO2);
        megaPartSubPartCountDTO1.setId(null);
        assertThat(megaPartSubPartCountDTO1).isNotEqualTo(megaPartSubPartCountDTO2);
    }
}
