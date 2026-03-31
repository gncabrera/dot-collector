package com.dot.collector.api.domain;

import static com.dot.collector.api.domain.MegaPartTestSamples.*;
import static com.dot.collector.api.domain.MegaPartTypeTestSamples.*;
import static com.dot.collector.api.domain.PartCategoryTestSamples.*;
import static com.dot.collector.api.domain.PartSubCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MegaPartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaPart.class);
        MegaPart megaPart1 = getMegaPartSample1();
        MegaPart megaPart2 = new MegaPart();
        assertThat(megaPart1).isNotEqualTo(megaPart2);

        megaPart2.setId(megaPart1.getId());
        assertThat(megaPart1).isEqualTo(megaPart2);

        megaPart2 = getMegaPartSample2();
        assertThat(megaPart1).isNotEqualTo(megaPart2);
    }

    @Test
    void typeTest() {
        MegaPart megaPart = getMegaPartRandomSampleGenerator();
        MegaPartType megaPartTypeBack = getMegaPartTypeRandomSampleGenerator();

        megaPart.setType(megaPartTypeBack);
        assertThat(megaPart.getType()).isEqualTo(megaPartTypeBack);

        megaPart.type(null);
        assertThat(megaPart.getType()).isNull();
    }

    @Test
    void partCategoryTest() {
        MegaPart megaPart = getMegaPartRandomSampleGenerator();
        PartCategory partCategoryBack = getPartCategoryRandomSampleGenerator();

        megaPart.setPartCategory(partCategoryBack);
        assertThat(megaPart.getPartCategory()).isEqualTo(partCategoryBack);

        megaPart.partCategory(null);
        assertThat(megaPart.getPartCategory()).isNull();
    }

    @Test
    void partSubCategoryTest() {
        MegaPart megaPart = getMegaPartRandomSampleGenerator();
        PartSubCategory partSubCategoryBack = getPartSubCategoryRandomSampleGenerator();

        megaPart.addPartSubCategory(partSubCategoryBack);
        assertThat(megaPart.getPartSubCategories()).containsOnly(partSubCategoryBack);

        megaPart.removePartSubCategory(partSubCategoryBack);
        assertThat(megaPart.getPartSubCategories()).doesNotContain(partSubCategoryBack);

        megaPart.partSubCategories(new HashSet<>(Set.of(partSubCategoryBack)));
        assertThat(megaPart.getPartSubCategories()).containsOnly(partSubCategoryBack);

        megaPart.setPartSubCategories(new HashSet<>());
        assertThat(megaPart.getPartSubCategories()).doesNotContain(partSubCategoryBack);
    }
}
