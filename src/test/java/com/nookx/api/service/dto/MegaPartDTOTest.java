package com.nookx.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaPartDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaPartDTO.class);
        MegaPartDTO megaPartDTO1 = new MegaPartDTO();
        megaPartDTO1.setId(1L);
        MegaPartDTO megaPartDTO2 = new MegaPartDTO();
        assertThat(megaPartDTO1).isNotEqualTo(megaPartDTO2);
        megaPartDTO2.setId(megaPartDTO1.getId());
        assertThat(megaPartDTO1).isEqualTo(megaPartDTO2);
        megaPartDTO2.setId(2L);
        assertThat(megaPartDTO1).isNotEqualTo(megaPartDTO2);
        megaPartDTO1.setId(null);
        assertThat(megaPartDTO1).isNotEqualTo(megaPartDTO2);
    }
}
