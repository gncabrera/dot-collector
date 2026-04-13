package com.nookx.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartSubCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartSubCategoryDTO.class);
        PartSubCategoryDTO partSubCategoryDTO1 = new PartSubCategoryDTO();
        partSubCategoryDTO1.setId(1L);
        PartSubCategoryDTO partSubCategoryDTO2 = new PartSubCategoryDTO();
        assertThat(partSubCategoryDTO1).isNotEqualTo(partSubCategoryDTO2);
        partSubCategoryDTO2.setId(partSubCategoryDTO1.getId());
        assertThat(partSubCategoryDTO1).isEqualTo(partSubCategoryDTO2);
        partSubCategoryDTO2.setId(2L);
        assertThat(partSubCategoryDTO1).isNotEqualTo(partSubCategoryDTO2);
        partSubCategoryDTO1.setId(null);
        assertThat(partSubCategoryDTO1).isNotEqualTo(partSubCategoryDTO2);
    }
}
