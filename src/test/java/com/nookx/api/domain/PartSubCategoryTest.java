package com.nookx.api.domain;

import static com.nookx.api.domain.MegaPartTestSamples.*;
import static com.nookx.api.domain.PartSubCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PartSubCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartSubCategory.class);
        PartSubCategory partSubCategory1 = getPartSubCategorySample1();
        PartSubCategory partSubCategory2 = new PartSubCategory();
        assertThat(partSubCategory1).isNotEqualTo(partSubCategory2);

        partSubCategory2.setId(partSubCategory1.getId());
        assertThat(partSubCategory1).isEqualTo(partSubCategory2);

        partSubCategory2 = getPartSubCategorySample2();
        assertThat(partSubCategory1).isNotEqualTo(partSubCategory2);
    }

    @Test
    void megaPartTest() {
        PartSubCategory partSubCategory = getPartSubCategoryRandomSampleGenerator();
        MegaPart megaPartBack = getMegaPartRandomSampleGenerator();

        partSubCategory.addMegaPart(megaPartBack);
        assertThat(partSubCategory.getMegaParts()).containsOnly(megaPartBack);
        assertThat(megaPartBack.getPartSubCategories()).containsOnly(partSubCategory);

        partSubCategory.removeMegaPart(megaPartBack);
        assertThat(partSubCategory.getMegaParts()).doesNotContain(megaPartBack);
        assertThat(megaPartBack.getPartSubCategories()).doesNotContain(partSubCategory);

        partSubCategory.megaParts(new HashSet<>(Set.of(megaPartBack)));
        assertThat(partSubCategory.getMegaParts()).containsOnly(megaPartBack);
        assertThat(megaPartBack.getPartSubCategories()).containsOnly(partSubCategory);

        partSubCategory.setMegaParts(new HashSet<>());
        assertThat(partSubCategory.getMegaParts()).doesNotContain(megaPartBack);
        assertThat(megaPartBack.getPartSubCategories()).doesNotContain(partSubCategory);
    }
}
