import { IEnvironmentVariable, NewEnvironmentVariable } from './environment-variable.model';

export const sampleWithRequiredData: IEnvironmentVariable = {
  id: 30195,
};

export const sampleWithPartialData: IEnvironmentVariable = {
  id: 255,
  value: 'brightly',
  description: 'fireplace roadway',
  type: 'aw',
};

export const sampleWithFullData: IEnvironmentVariable = {
  id: 17828,
  key: 'unnecessarily',
  value: 'qua foodstuffs',
  description: 'chilly while unless',
  type: 'ugh bookcase',
};

export const sampleWithNewData: NewEnvironmentVariable = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
