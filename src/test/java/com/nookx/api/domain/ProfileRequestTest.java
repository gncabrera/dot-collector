package com.nookx.api.domain;

import static com.nookx.api.domain.ProfileRequestTestSamples.*;
import static com.nookx.api.domain.ProfileRequestTypeTestSamples.*;
import static com.nookx.api.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileRequest.class);
        ProfileRequest profileRequest1 = getProfileRequestSample1();
        ProfileRequest profileRequest2 = new ProfileRequest();
        assertThat(profileRequest1).isNotEqualTo(profileRequest2);

        profileRequest2.setId(profileRequest1.getId());
        assertThat(profileRequest1).isEqualTo(profileRequest2);

        profileRequest2 = getProfileRequestSample2();
        assertThat(profileRequest1).isNotEqualTo(profileRequest2);
    }

    @Test
    void typeTest() {
        ProfileRequest profileRequest = getProfileRequestRandomSampleGenerator();
        ProfileRequestType profileRequestTypeBack = getProfileRequestTypeRandomSampleGenerator();

        profileRequest.setType(profileRequestTypeBack);
        assertThat(profileRequest.getType()).isEqualTo(profileRequestTypeBack);

        profileRequest.type(null);
        assertThat(profileRequest.getType()).isNull();
    }

    @Test
    void profileTest() {
        ProfileRequest profileRequest = getProfileRequestRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        profileRequest.setProfile(profileBack);
        assertThat(profileRequest.getProfile()).isEqualTo(profileBack);

        profileRequest.profile(null);
        assertThat(profileRequest.getProfile()).isNull();
    }
}
