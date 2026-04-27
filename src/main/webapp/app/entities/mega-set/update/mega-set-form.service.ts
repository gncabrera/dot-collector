import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaSet, NewMegaSet } from '../mega-set.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaSet for edit and NewMegaSetFormGroupInput for create.
 */
type MegaSetFormGroupInput = IMegaSet | PartialWithRequiredKeyOf<NewMegaSet>;

type MegaSetFormDefaults = Pick<NewMegaSet, 'id' | 'profileCollectionSets'>;

type MegaSetFormGroupContent = {
  id: FormControl<IMegaSet['id'] | NewMegaSet['id']>;
  setNumber: FormControl<IMegaSet['setNumber']>;
  releaseDate: FormControl<IMegaSet['releaseDate']>;
  notes: FormControl<IMegaSet['notes']>;
  name: FormControl<IMegaSet['name']>;
  description: FormControl<IMegaSet['description']>;
  attributes: FormControl<IMegaSet['attributes']>;
  attributesContentType: FormControl<IMegaSet['attributesContentType']>;
  type: FormControl<IMegaSet['type']>;
  profileCollectionSets: FormControl<IMegaSet['profileCollectionSets']>;
};

export type MegaSetFormGroup = FormGroup<MegaSetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaSetFormService {
  createMegaSetFormGroup(megaSet?: MegaSetFormGroupInput): MegaSetFormGroup {
    const megaSetRawValue = {
      ...this.getFormDefaults(),
      ...(megaSet ?? { id: null }),
    };
    return new FormGroup<MegaSetFormGroupContent>({
      id: new FormControl(
        { value: megaSetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      setNumber: new FormControl(megaSetRawValue.setNumber, {
        validators: [Validators.required],
      }),
      releaseDate: new FormControl(megaSetRawValue.releaseDate),
      notes: new FormControl(megaSetRawValue.notes),
      name: new FormControl(megaSetRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(megaSetRawValue.description),
      attributes: new FormControl(megaSetRawValue.attributes),
      attributesContentType: new FormControl(megaSetRawValue.attributesContentType),
      type: new FormControl(megaSetRawValue.type),
      profileCollectionSets: new FormControl(megaSetRawValue.profileCollectionSets ?? []),
    });
  }

  getMegaSet(form: MegaSetFormGroup): IMegaSet | NewMegaSet {
    return form.getRawValue() as IMegaSet | NewMegaSet;
  }

  resetForm(form: MegaSetFormGroup, megaSet: MegaSetFormGroupInput): void {
    const megaSetRawValue = { ...this.getFormDefaults(), ...megaSet };
    form.reset({
      ...megaSetRawValue,
      id: { value: megaSetRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaSetFormDefaults {
    return {
      id: null,
      profileCollectionSets: [],
    };
  }
}
