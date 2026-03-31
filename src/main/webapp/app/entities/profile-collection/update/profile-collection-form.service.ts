import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfileCollection, NewProfileCollection } from '../profile-collection.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfileCollection for edit and NewProfileCollectionFormGroupInput for create.
 */
type ProfileCollectionFormGroupInput = IProfileCollection | PartialWithRequiredKeyOf<NewProfileCollection>;

type ProfileCollectionFormDefaults = Pick<NewProfileCollection, 'id' | 'isPublic'>;

type ProfileCollectionFormGroupContent = {
  id: FormControl<IProfileCollection['id'] | NewProfileCollection['id']>;
  title: FormControl<IProfileCollection['title']>;
  description: FormControl<IProfileCollection['description']>;
  isPublic: FormControl<IProfileCollection['isPublic']>;
  profile: FormControl<IProfileCollection['profile']>;
};

export type ProfileCollectionFormGroup = FormGroup<ProfileCollectionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfileCollectionFormService {
  createProfileCollectionFormGroup(profileCollection?: ProfileCollectionFormGroupInput): ProfileCollectionFormGroup {
    const profileCollectionRawValue = {
      ...this.getFormDefaults(),
      ...(profileCollection ?? { id: null }),
    };
    return new FormGroup<ProfileCollectionFormGroupContent>({
      id: new FormControl(
        { value: profileCollectionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(profileCollectionRawValue.title),
      description: new FormControl(profileCollectionRawValue.description),
      isPublic: new FormControl(profileCollectionRawValue.isPublic),
      profile: new FormControl(profileCollectionRawValue.profile),
    });
  }

  getProfileCollection(form: ProfileCollectionFormGroup): IProfileCollection | NewProfileCollection {
    return form.getRawValue() as IProfileCollection | NewProfileCollection;
  }

  resetForm(form: ProfileCollectionFormGroup, profileCollection: ProfileCollectionFormGroupInput): void {
    const profileCollectionRawValue = { ...this.getFormDefaults(), ...profileCollection };
    form.reset({
      ...profileCollectionRawValue,
      id: { value: profileCollectionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProfileCollectionFormDefaults {
    return {
      id: null,
      isPublic: false,
    };
  }
}
