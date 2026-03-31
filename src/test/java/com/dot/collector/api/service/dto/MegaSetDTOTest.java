package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaSetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaSetDTO.class);
        MegaSetDTO megaSetDTO1 = new MegaSetDTO();
        megaSetDTO1.setId(1L);
        MegaSetDTO megaSetDTO2 = new MegaSetDTO();
        assertThat(megaSetDTO1).isNotEqualTo(megaSetDTO2);
        megaSetDTO2.setId(megaSetDTO1.getId());
        assertThat(megaSetDTO1).isEqualTo(megaSetDTO2);
        megaSetDTO2.setId(2L);
        assertThat(megaSetDTO1).isNotEqualTo(megaSetDTO2);
        megaSetDTO1.setId(null);
        assertThat(megaSetDTO1).isNotEqualTo(megaSetDTO2);
    }
}
