import { IMegaAttributeOption, NewMegaAttributeOption } from './mega-attribute-option.model';

export const sampleWithRequiredData: IMegaAttributeOption = {
  id: 27642,
};

export const sampleWithPartialData: IMegaAttributeOption = {
  id: 11131,
  label: 'boo incidentally',
  value: 'concerning',
};

export const sampleWithFullData: IMegaAttributeOption = {
  id: 18124,
  label: 'task',
  value: 'stranger',
  description: 'word',
};

export const sampleWithNewData: NewMegaAttributeOption = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
