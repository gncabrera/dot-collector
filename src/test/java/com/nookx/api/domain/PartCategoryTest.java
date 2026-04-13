package com.nookx.api.domain;

import static com.nookx.api.domain.PartCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartCategory.class);
        PartCategory partCategory1 = getPartCategorySample1();
        PartCategory partCategory2 = new PartCategory();
        assertThat(partCategory1).isNotEqualTo(partCategory2);

        partCategory2.setId(partCategory1.getId());
        assertThat(partCategory1).isEqualTo(partCategory2);

        partCategory2 = getPartCategorySample2();
        assertThat(partCategory1).isNotEqualTo(partCategory2);
    }
}
