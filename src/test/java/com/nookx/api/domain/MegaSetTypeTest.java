package com.nookx.api.domain;

import static com.nookx.api.domain.MegaAttributeTestSamples.*;
import static com.nookx.api.domain.MegaSetTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MegaSetTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaSetType.class);
        MegaSetType megaSetType1 = getMegaSetTypeSample1();
        MegaSetType megaSetType2 = new MegaSetType();
        assertThat(megaSetType1).isNotEqualTo(megaSetType2);

        megaSetType2.setId(megaSetType1.getId());
        assertThat(megaSetType1).isEqualTo(megaSetType2);

        megaSetType2 = getMegaSetTypeSample2();
        assertThat(megaSetType1).isNotEqualTo(megaSetType2);
    }

    @Test
    void attributeTest() {
        MegaSetType megaSetType = getMegaSetTypeRandomSampleGenerator();
        MegaAttribute megaAttributeBack = getMegaAttributeRandomSampleGenerator();

        megaSetType.addAttribute(megaAttributeBack);
        assertThat(megaSetType.getAttributes()).containsOnly(megaAttributeBack);

        megaSetType.removeAttribute(megaAttributeBack);
        assertThat(megaSetType.getAttributes()).doesNotContain(megaAttributeBack);

        megaSetType.attributes(new HashSet<>(Set.of(megaAttributeBack)));
        assertThat(megaSetType.getAttributes()).containsOnly(megaAttributeBack);

        megaSetType.setAttributes(new HashSet<>());
        assertThat(megaSetType.getAttributes()).doesNotContain(megaAttributeBack);
    }
}
