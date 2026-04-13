package com.nookx.api.domain;

import static com.nookx.api.domain.MegaAttributeTestSamples.*;
import static com.nookx.api.domain.MegaPartTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MegaPartTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaPartType.class);
        MegaPartType megaPartType1 = getMegaPartTypeSample1();
        MegaPartType megaPartType2 = new MegaPartType();
        assertThat(megaPartType1).isNotEqualTo(megaPartType2);

        megaPartType2.setId(megaPartType1.getId());
        assertThat(megaPartType1).isEqualTo(megaPartType2);

        megaPartType2 = getMegaPartTypeSample2();
        assertThat(megaPartType1).isNotEqualTo(megaPartType2);
    }

    @Test
    void attributeTest() {
        MegaPartType megaPartType = getMegaPartTypeRandomSampleGenerator();
        MegaAttribute megaAttributeBack = getMegaAttributeRandomSampleGenerator();

        megaPartType.addAttribute(megaAttributeBack);
        assertThat(megaPartType.getAttributes()).containsOnly(megaAttributeBack);

        megaPartType.removeAttribute(megaAttributeBack);
        assertThat(megaPartType.getAttributes()).doesNotContain(megaAttributeBack);

        megaPartType.attributes(new HashSet<>(Set.of(megaAttributeBack)));
        assertThat(megaPartType.getAttributes()).containsOnly(megaAttributeBack);

        megaPartType.setAttributes(new HashSet<>());
        assertThat(megaPartType.getAttributes()).doesNotContain(megaAttributeBack);
    }
}
