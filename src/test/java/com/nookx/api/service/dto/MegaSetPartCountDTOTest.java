package com.nookx.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaSetPartCountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaSetPartCountDTO.class);
        MegaSetPartCountDTO megaSetPartCountDTO1 = new MegaSetPartCountDTO();
        megaSetPartCountDTO1.setId(1L);
        MegaSetPartCountDTO megaSetPartCountDTO2 = new MegaSetPartCountDTO();
        assertThat(megaSetPartCountDTO1).isNotEqualTo(megaSetPartCountDTO2);
        megaSetPartCountDTO2.setId(megaSetPartCountDTO1.getId());
        assertThat(megaSetPartCountDTO1).isEqualTo(megaSetPartCountDTO2);
        megaSetPartCountDTO2.setId(2L);
        assertThat(megaSetPartCountDTO1).isNotEqualTo(megaSetPartCountDTO2);
        megaSetPartCountDTO1.setId(null);
        assertThat(megaSetPartCountDTO1).isNotEqualTo(megaSetPartCountDTO2);
    }
}
