package com.nookx.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaPartTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaPartTypeDTO.class);
        MegaPartTypeDTO megaPartTypeDTO1 = new MegaPartTypeDTO();
        megaPartTypeDTO1.setId(1L);
        MegaPartTypeDTO megaPartTypeDTO2 = new MegaPartTypeDTO();
        assertThat(megaPartTypeDTO1).isNotEqualTo(megaPartTypeDTO2);
        megaPartTypeDTO2.setId(megaPartTypeDTO1.getId());
        assertThat(megaPartTypeDTO1).isEqualTo(megaPartTypeDTO2);
        megaPartTypeDTO2.setId(2L);
        assertThat(megaPartTypeDTO1).isNotEqualTo(megaPartTypeDTO2);
        megaPartTypeDTO1.setId(null);
        assertThat(megaPartTypeDTO1).isNotEqualTo(megaPartTypeDTO2);
    }
}
