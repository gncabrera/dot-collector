import { IProfileRequest, NewProfileRequest } from './profile-request.model';

export const sampleWithRequiredData: IProfileRequest = {
  id: 29023,
};

export const sampleWithPartialData: IProfileRequest = {
  id: 2638,
  message: 'unusual hm writ',
};

export const sampleWithFullData: IProfileRequest = {
  id: 19992,
  message: 'eek downshift failing',
};

export const sampleWithNewData: NewProfileRequest = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
