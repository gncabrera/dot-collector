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
  nameEN: FormControl<IMegaSet['nameEN']>;
  nameES: FormControl<IMegaSet['nameES']>;
  nameDE: FormControl<IMegaSet['nameDE']>;
  nameFR: FormControl<IMegaSet['nameFR']>;
  descriptionEN: FormControl<IMegaSet['descriptionEN']>;
  descriptionES: FormControl<IMegaSet['descriptionES']>;
  descriptionDE: FormControl<IMegaSet['descriptionDE']>;
  descriptionFR: FormControl<IMegaSet['descriptionFR']>;
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
      nameEN: new FormControl(megaSetRawValue.nameEN, {
        validators: [Validators.required],
      }),
      nameES: new FormControl(megaSetRawValue.nameES),
      nameDE: new FormControl(megaSetRawValue.nameDE),
      nameFR: new FormControl(megaSetRawValue.nameFR),
      descriptionEN: new FormControl(megaSetRawValue.descriptionEN, {
        validators: [Validators.required],
      }),
      descriptionES: new FormControl(megaSetRawValue.descriptionES),
      descriptionDE: new FormControl(megaSetRawValue.descriptionDE),
      descriptionFR: new FormControl(megaSetRawValue.descriptionFR),
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
