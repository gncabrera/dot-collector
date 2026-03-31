package com.dot.collector.api.domain;

import static com.dot.collector.api.domain.MegaAttributeOptionTestSamples.*;
import static com.dot.collector.api.domain.MegaAttributeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaAttributeOptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaAttributeOption.class);
        MegaAttributeOption megaAttributeOption1 = getMegaAttributeOptionSample1();
        MegaAttributeOption megaAttributeOption2 = new MegaAttributeOption();
        assertThat(megaAttributeOption1).isNotEqualTo(megaAttributeOption2);

        megaAttributeOption2.setId(megaAttributeOption1.getId());
        assertThat(megaAttributeOption1).isEqualTo(megaAttributeOption2);

        megaAttributeOption2 = getMegaAttributeOptionSample2();
        assertThat(megaAttributeOption1).isNotEqualTo(megaAttributeOption2);
    }

    @Test
    void attributeTest() {
        MegaAttributeOption megaAttributeOption = getMegaAttributeOptionRandomSampleGenerator();
        MegaAttribute megaAttributeBack = getMegaAttributeRandomSampleGenerator();

        megaAttributeOption.setAttribute(megaAttributeBack);
        assertThat(megaAttributeOption.getAttribute()).isEqualTo(megaAttributeBack);

        megaAttributeOption.attribute(null);
        assertThat(megaAttributeOption.getAttribute()).isNull();
    }
}
