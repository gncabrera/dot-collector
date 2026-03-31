package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaAssetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaAssetDTO.class);
        MegaAssetDTO megaAssetDTO1 = new MegaAssetDTO();
        megaAssetDTO1.setId(1L);
        MegaAssetDTO megaAssetDTO2 = new MegaAssetDTO();
        assertThat(megaAssetDTO1).isNotEqualTo(megaAssetDTO2);
        megaAssetDTO2.setId(megaAssetDTO1.getId());
        assertThat(megaAssetDTO1).isEqualTo(megaAssetDTO2);
        megaAssetDTO2.setId(2L);
        assertThat(megaAssetDTO1).isNotEqualTo(megaAssetDTO2);
        megaAssetDTO1.setId(null);
        assertThat(megaAssetDTO1).isNotEqualTo(megaAssetDTO2);
    }
}
