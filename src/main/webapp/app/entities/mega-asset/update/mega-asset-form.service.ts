import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaAsset, NewMegaAsset } from '../mega-asset.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaAsset for edit and NewMegaAssetFormGroupInput for create.
 */
type MegaAssetFormGroupInput = IMegaAsset | PartialWithRequiredKeyOf<NewMegaAsset>;

type MegaAssetFormDefaults = Pick<NewMegaAsset, 'id'>;

type MegaAssetFormGroupContent = {
  id: FormControl<IMegaAsset['id'] | NewMegaAsset['id']>;
  name: FormControl<IMegaAsset['name']>;
  description: FormControl<IMegaAsset['description']>;
  path: FormControl<IMegaAsset['path']>;
  type: FormControl<IMegaAsset['type']>;
  set: FormControl<IMegaAsset['set']>;
  part: FormControl<IMegaAsset['part']>;
};

export type MegaAssetFormGroup = FormGroup<MegaAssetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaAssetFormService {
  createMegaAssetFormGroup(megaAsset?: MegaAssetFormGroupInput): MegaAssetFormGroup {
    const megaAssetRawValue = {
      ...this.getFormDefaults(),
      ...(megaAsset ?? { id: null }),
    };
    return new FormGroup<MegaAssetFormGroupContent>({
      id: new FormControl(
        { value: megaAssetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(megaAssetRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(megaAssetRawValue.description),
      path: new FormControl(megaAssetRawValue.path, {
        validators: [Validators.required],
      }),
      type: new FormControl(megaAssetRawValue.type),
      set: new FormControl(megaAssetRawValue.set),
      part: new FormControl(megaAssetRawValue.part),
    });
  }

  getMegaAsset(form: MegaAssetFormGroup): IMegaAsset | NewMegaAsset {
    return form.getRawValue() as IMegaAsset | NewMegaAsset;
  }

  resetForm(form: MegaAssetFormGroup, megaAsset: MegaAssetFormGroupInput): void {
    const megaAssetRawValue = { ...this.getFormDefaults(), ...megaAsset };
    form.reset({
      ...megaAssetRawValue,
      id: { value: megaAssetRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaAssetFormDefaults {
    return {
      id: null,
    };
  }
}
