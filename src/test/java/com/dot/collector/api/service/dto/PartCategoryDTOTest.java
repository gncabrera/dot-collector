package com.dot.collector.api.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartCategoryDTO.class);
        PartCategoryDTO partCategoryDTO1 = new PartCategoryDTO();
        partCategoryDTO1.setId(1L);
        PartCategoryDTO partCategoryDTO2 = new PartCategoryDTO();
        assertThat(partCategoryDTO1).isNotEqualTo(partCategoryDTO2);
        partCategoryDTO2.setId(partCategoryDTO1.getId());
        assertThat(partCategoryDTO1).isEqualTo(partCategoryDTO2);
        partCategoryDTO2.setId(2L);
        assertThat(partCategoryDTO1).isNotEqualTo(partCategoryDTO2);
        partCategoryDTO1.setId(null);
        assertThat(partCategoryDTO1).isNotEqualTo(partCategoryDTO2);
    }
}
