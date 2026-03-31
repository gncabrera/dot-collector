import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IFollowingProfile, NewFollowingProfile } from '../following-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFollowingProfile for edit and NewFollowingProfileFormGroupInput for create.
 */
type FollowingProfileFormGroupInput = IFollowingProfile | PartialWithRequiredKeyOf<NewFollowingProfile>;

type FollowingProfileFormDefaults = Pick<NewFollowingProfile, 'id'>;

type FollowingProfileFormGroupContent = {
  id: FormControl<IFollowingProfile['id'] | NewFollowingProfile['id']>;
  dateFollowing: FormControl<IFollowingProfile['dateFollowing']>;
  profile: FormControl<IFollowingProfile['profile']>;
  followedProfile: FormControl<IFollowingProfile['followedProfile']>;
};

export type FollowingProfileFormGroup = FormGroup<FollowingProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FollowingProfileFormService {
  createFollowingProfileFormGroup(followingProfile?: FollowingProfileFormGroupInput): FollowingProfileFormGroup {
    const followingProfileRawValue = {
      ...this.getFormDefaults(),
      ...(followingProfile ?? { id: null }),
    };
    return new FormGroup<FollowingProfileFormGroupContent>({
      id: new FormControl(
        { value: followingProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dateFollowing: new FormControl(followingProfileRawValue.dateFollowing),
      profile: new FormControl(followingProfileRawValue.profile),
      followedProfile: new FormControl(followingProfileRawValue.followedProfile),
    });
  }

  getFollowingProfile(form: FollowingProfileFormGroup): IFollowingProfile | NewFollowingProfile {
    return form.getRawValue() as IFollowingProfile | NewFollowingProfile;
  }

  resetForm(form: FollowingProfileFormGroup, followingProfile: FollowingProfileFormGroupInput): void {
    const followingProfileRawValue = { ...this.getFormDefaults(), ...followingProfile };
    form.reset({
      ...followingProfileRawValue,
      id: { value: followingProfileRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): FollowingProfileFormDefaults {
    return {
      id: null,
    };
  }
}
