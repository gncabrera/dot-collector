package com.nookx.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaSetFileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaSetFileDTO.class);
        MegaSetFileDTO megaSetFileDTO1 = new MegaSetFileDTO();
        megaSetFileDTO1.setId(1L);
        MegaSetFileDTO megaSetFileDTO2 = new MegaSetFileDTO();
        assertThat(megaSetFileDTO1).isNotEqualTo(megaSetFileDTO2);
        megaSetFileDTO2.setId(megaSetFileDTO1.getId());
        assertThat(megaSetFileDTO1).isEqualTo(megaSetFileDTO2);
        megaSetFileDTO2.setId(2L);
        assertThat(megaSetFileDTO1).isNotEqualTo(megaSetFileDTO2);
        megaSetFileDTO1.setId(null);
        assertThat(megaSetFileDTO1).isNotEqualTo(megaSetFileDTO2);
    }
}
