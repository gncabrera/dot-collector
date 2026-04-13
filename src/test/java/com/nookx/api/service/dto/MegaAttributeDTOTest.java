package com.nookx.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaAttributeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaAttributeDTO.class);
        MegaAttributeDTO megaAttributeDTO1 = new MegaAttributeDTO();
        megaAttributeDTO1.setId(1L);
        MegaAttributeDTO megaAttributeDTO2 = new MegaAttributeDTO();
        assertThat(megaAttributeDTO1).isNotEqualTo(megaAttributeDTO2);
        megaAttributeDTO2.setId(megaAttributeDTO1.getId());
        assertThat(megaAttributeDTO1).isEqualTo(megaAttributeDTO2);
        megaAttributeDTO2.setId(2L);
        assertThat(megaAttributeDTO1).isNotEqualTo(megaAttributeDTO2);
        megaAttributeDTO1.setId(null);
        assertThat(megaAttributeDTO1).isNotEqualTo(megaAttributeDTO2);
    }
}
