package com.nookx.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaAttributeOptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaAttributeOptionDTO.class);
        MegaAttributeOptionDTO megaAttributeOptionDTO1 = new MegaAttributeOptionDTO();
        megaAttributeOptionDTO1.setId(1L);
        MegaAttributeOptionDTO megaAttributeOptionDTO2 = new MegaAttributeOptionDTO();
        assertThat(megaAttributeOptionDTO1).isNotEqualTo(megaAttributeOptionDTO2);
        megaAttributeOptionDTO2.setId(megaAttributeOptionDTO1.getId());
        assertThat(megaAttributeOptionDTO1).isEqualTo(megaAttributeOptionDTO2);
        megaAttributeOptionDTO2.setId(2L);
        assertThat(megaAttributeOptionDTO1).isNotEqualTo(megaAttributeOptionDTO2);
        megaAttributeOptionDTO1.setId(null);
        assertThat(megaAttributeOptionDTO1).isNotEqualTo(megaAttributeOptionDTO2);
    }
}
