import { IProfileRequestType, NewProfileRequestType } from './profile-request-type.model';

export const sampleWithRequiredData: IProfileRequestType = {
  id: 24427,
};

export const sampleWithPartialData: IProfileRequestType = {
  id: 28580,
  key: 'even a yum',
};

export const sampleWithFullData: IProfileRequestType = {
  id: 12742,
  key: 'hmph',
  name: 'phooey nor apropos',
  description: 'surprise lighthearted gadzooks',
};

export const sampleWithNewData: NewProfileRequestType = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
