import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfileCollectionSet, NewProfileCollectionSet } from '../profile-collection-set.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfileCollectionSet for edit and NewProfileCollectionSetFormGroupInput for create.
 */
type ProfileCollectionSetFormGroupInput = IProfileCollectionSet | PartialWithRequiredKeyOf<NewProfileCollectionSet>;

type ProfileCollectionSetFormDefaults = Pick<NewProfileCollectionSet, 'id' | 'owned' | 'wanted' | 'sets'>;

type ProfileCollectionSetFormGroupContent = {
  id: FormControl<IProfileCollectionSet['id'] | NewProfileCollectionSet['id']>;
  owned: FormControl<IProfileCollectionSet['owned']>;
  wanted: FormControl<IProfileCollectionSet['wanted']>;
  dateAdded: FormControl<IProfileCollectionSet['dateAdded']>;
  collection: FormControl<IProfileCollectionSet['collection']>;
  sets: FormControl<IProfileCollectionSet['sets']>;
};

export type ProfileCollectionSetFormGroup = FormGroup<ProfileCollectionSetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfileCollectionSetFormService {
  createProfileCollectionSetFormGroup(profileCollectionSet?: ProfileCollectionSetFormGroupInput): ProfileCollectionSetFormGroup {
    const profileCollectionSetRawValue = {
      ...this.getFormDefaults(),
      ...(profileCollectionSet ?? { id: null }),
    };
    return new FormGroup<ProfileCollectionSetFormGroupContent>({
      id: new FormControl(
        { value: profileCollectionSetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      owned: new FormControl(profileCollectionSetRawValue.owned),
      wanted: new FormControl(profileCollectionSetRawValue.wanted),
      dateAdded: new FormControl(profileCollectionSetRawValue.dateAdded),
      collection: new FormControl(profileCollectionSetRawValue.collection),
      sets: new FormControl(profileCollectionSetRawValue.sets ?? []),
    });
  }

  getProfileCollectionSet(form: ProfileCollectionSetFormGroup): IProfileCollectionSet | NewProfileCollectionSet {
    return form.getRawValue() as IProfileCollectionSet | NewProfileCollectionSet;
  }

  resetForm(form: ProfileCollectionSetFormGroup, profileCollectionSet: ProfileCollectionSetFormGroupInput): void {
    const profileCollectionSetRawValue = { ...this.getFormDefaults(), ...profileCollectionSet };
    form.reset({
      ...profileCollectionSetRawValue,
      id: { value: profileCollectionSetRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProfileCollectionSetFormDefaults {
    return {
      id: null,
      owned: false,
      wanted: false,
      sets: [],
    };
  }
}
