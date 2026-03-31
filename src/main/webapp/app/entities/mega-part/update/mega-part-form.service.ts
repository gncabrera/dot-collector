import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaPart, NewMegaPart } from '../mega-part.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaPart for edit and NewMegaPartFormGroupInput for create.
 */
type MegaPartFormGroupInput = IMegaPart | PartialWithRequiredKeyOf<NewMegaPart>;

type MegaPartFormDefaults = Pick<NewMegaPart, 'id' | 'partSubCategories'>;

type MegaPartFormGroupContent = {
  id: FormControl<IMegaPart['id'] | NewMegaPart['id']>;
  releaseDate: FormControl<IMegaPart['releaseDate']>;
  partNumber: FormControl<IMegaPart['partNumber']>;
  nameEN: FormControl<IMegaPart['nameEN']>;
  nameES: FormControl<IMegaPart['nameES']>;
  nameDE: FormControl<IMegaPart['nameDE']>;
  nameFR: FormControl<IMegaPart['nameFR']>;
  description: FormControl<IMegaPart['description']>;
  notes: FormControl<IMegaPart['notes']>;
  attributes: FormControl<IMegaPart['attributes']>;
  attributesContentType: FormControl<IMegaPart['attributesContentType']>;
  type: FormControl<IMegaPart['type']>;
  partCategory: FormControl<IMegaPart['partCategory']>;
  partSubCategories: FormControl<IMegaPart['partSubCategories']>;
};

export type MegaPartFormGroup = FormGroup<MegaPartFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaPartFormService {
  createMegaPartFormGroup(megaPart?: MegaPartFormGroupInput): MegaPartFormGroup {
    const megaPartRawValue = {
      ...this.getFormDefaults(),
      ...(megaPart ?? { id: null }),
    };
    return new FormGroup<MegaPartFormGroupContent>({
      id: new FormControl(
        { value: megaPartRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      releaseDate: new FormControl(megaPartRawValue.releaseDate),
      partNumber: new FormControl(megaPartRawValue.partNumber, {
        validators: [Validators.required],
      }),
      nameEN: new FormControl(megaPartRawValue.nameEN, {
        validators: [Validators.required],
      }),
      nameES: new FormControl(megaPartRawValue.nameES),
      nameDE: new FormControl(megaPartRawValue.nameDE),
      nameFR: new FormControl(megaPartRawValue.nameFR),
      description: new FormControl(megaPartRawValue.description),
      notes: new FormControl(megaPartRawValue.notes),
      attributes: new FormControl(megaPartRawValue.attributes),
      attributesContentType: new FormControl(megaPartRawValue.attributesContentType),
      type: new FormControl(megaPartRawValue.type),
      partCategory: new FormControl(megaPartRawValue.partCategory),
      partSubCategories: new FormControl(megaPartRawValue.partSubCategories ?? []),
    });
  }

  getMegaPart(form: MegaPartFormGroup): IMegaPart | NewMegaPart {
    return form.getRawValue() as IMegaPart | NewMegaPart;
  }

  resetForm(form: MegaPartFormGroup, megaPart: MegaPartFormGroupInput): void {
    const megaPartRawValue = { ...this.getFormDefaults(), ...megaPart };
    form.reset({
      ...megaPartRawValue,
      id: { value: megaPartRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaPartFormDefaults {
    return {
      id: null,
      partSubCategories: [],
    };
  }
}
