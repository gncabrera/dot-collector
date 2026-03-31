package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaSetTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaSetTypeDTO.class);
        MegaSetTypeDTO megaSetTypeDTO1 = new MegaSetTypeDTO();
        megaSetTypeDTO1.setId(1L);
        MegaSetTypeDTO megaSetTypeDTO2 = new MegaSetTypeDTO();
        assertThat(megaSetTypeDTO1).isNotEqualTo(megaSetTypeDTO2);
        megaSetTypeDTO2.setId(megaSetTypeDTO1.getId());
        assertThat(megaSetTypeDTO1).isEqualTo(megaSetTypeDTO2);
        megaSetTypeDTO2.setId(2L);
        assertThat(megaSetTypeDTO1).isNotEqualTo(megaSetTypeDTO2);
        megaSetTypeDTO1.setId(null);
        assertThat(megaSetTypeDTO1).isNotEqualTo(megaSetTypeDTO2);
    }
}
