import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaPartSubPartCount, NewMegaPartSubPartCount } from '../mega-part-sub-part-count.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaPartSubPartCount for edit and NewMegaPartSubPartCountFormGroupInput for create.
 */
type MegaPartSubPartCountFormGroupInput = IMegaPartSubPartCount | PartialWithRequiredKeyOf<NewMegaPartSubPartCount>;

type MegaPartSubPartCountFormDefaults = Pick<NewMegaPartSubPartCount, 'id'>;

type MegaPartSubPartCountFormGroupContent = {
  id: FormControl<IMegaPartSubPartCount['id'] | NewMegaPartSubPartCount['id']>;
  count: FormControl<IMegaPartSubPartCount['count']>;
  part: FormControl<IMegaPartSubPartCount['part']>;
  parentPart: FormControl<IMegaPartSubPartCount['parentPart']>;
};

export type MegaPartSubPartCountFormGroup = FormGroup<MegaPartSubPartCountFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaPartSubPartCountFormService {
  createMegaPartSubPartCountFormGroup(megaPartSubPartCount?: MegaPartSubPartCountFormGroupInput): MegaPartSubPartCountFormGroup {
    const megaPartSubPartCountRawValue = {
      ...this.getFormDefaults(),
      ...(megaPartSubPartCount ?? { id: null }),
    };
    return new FormGroup<MegaPartSubPartCountFormGroupContent>({
      id: new FormControl(
        { value: megaPartSubPartCountRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      count: new FormControl(megaPartSubPartCountRawValue.count),
      part: new FormControl(megaPartSubPartCountRawValue.part),
      parentPart: new FormControl(megaPartSubPartCountRawValue.parentPart),
    });
  }

  getMegaPartSubPartCount(form: MegaPartSubPartCountFormGroup): IMegaPartSubPartCount | NewMegaPartSubPartCount {
    return form.getRawValue() as IMegaPartSubPartCount | NewMegaPartSubPartCount;
  }

  resetForm(form: MegaPartSubPartCountFormGroup, megaPartSubPartCount: MegaPartSubPartCountFormGroupInput): void {
    const megaPartSubPartCountRawValue = { ...this.getFormDefaults(), ...megaPartSubPartCount };
    form.reset({
      ...megaPartSubPartCountRawValue,
      id: { value: megaPartSubPartCountRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaPartSubPartCountFormDefaults {
    return {
      id: null,
    };
  }
}
