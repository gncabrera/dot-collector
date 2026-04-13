package com.nookx.api.domain;

import static com.nookx.api.domain.MegaAttributeTestSamples.*;
import static com.nookx.api.domain.MegaPartTypeTestSamples.*;
import static com.nookx.api.domain.MegaSetTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MegaAttributeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaAttribute.class);
        MegaAttribute megaAttribute1 = getMegaAttributeSample1();
        MegaAttribute megaAttribute2 = new MegaAttribute();
        assertThat(megaAttribute1).isNotEqualTo(megaAttribute2);

        megaAttribute2.setId(megaAttribute1.getId());
        assertThat(megaAttribute1).isEqualTo(megaAttribute2);

        megaAttribute2 = getMegaAttributeSample2();
        assertThat(megaAttribute1).isNotEqualTo(megaAttribute2);
    }

    @Test
    void setTypeTest() {
        MegaAttribute megaAttribute = getMegaAttributeRandomSampleGenerator();
        MegaSetType megaSetTypeBack = getMegaSetTypeRandomSampleGenerator();

        megaAttribute.addSetType(megaSetTypeBack);
        assertThat(megaAttribute.getSetTypes()).containsOnly(megaSetTypeBack);
        assertThat(megaSetTypeBack.getAttributes()).containsOnly(megaAttribute);

        megaAttribute.removeSetType(megaSetTypeBack);
        assertThat(megaAttribute.getSetTypes()).doesNotContain(megaSetTypeBack);
        assertThat(megaSetTypeBack.getAttributes()).doesNotContain(megaAttribute);

        megaAttribute.setTypes(new HashSet<>(Set.of(megaSetTypeBack)));
        assertThat(megaAttribute.getSetTypes()).containsOnly(megaSetTypeBack);
        assertThat(megaSetTypeBack.getAttributes()).containsOnly(megaAttribute);

        megaAttribute.setSetTypes(new HashSet<>());
        assertThat(megaAttribute.getSetTypes()).doesNotContain(megaSetTypeBack);
        assertThat(megaSetTypeBack.getAttributes()).doesNotContain(megaAttribute);
    }

    @Test
    void partTypeTest() {
        MegaAttribute megaAttribute = getMegaAttributeRandomSampleGenerator();
        MegaPartType megaPartTypeBack = getMegaPartTypeRandomSampleGenerator();

        megaAttribute.addPartType(megaPartTypeBack);
        assertThat(megaAttribute.getPartTypes()).containsOnly(megaPartTypeBack);
        assertThat(megaPartTypeBack.getAttributes()).containsOnly(megaAttribute);

        megaAttribute.removePartType(megaPartTypeBack);
        assertThat(megaAttribute.getPartTypes()).doesNotContain(megaPartTypeBack);
        assertThat(megaPartTypeBack.getAttributes()).doesNotContain(megaAttribute);

        megaAttribute.partTypes(new HashSet<>(Set.of(megaPartTypeBack)));
        assertThat(megaAttribute.getPartTypes()).containsOnly(megaPartTypeBack);
        assertThat(megaPartTypeBack.getAttributes()).containsOnly(megaAttribute);

        megaAttribute.setPartTypes(new HashSet<>());
        assertThat(megaAttribute.getPartTypes()).doesNotContain(megaPartTypeBack);
        assertThat(megaPartTypeBack.getAttributes()).doesNotContain(megaAttribute);
    }
}
