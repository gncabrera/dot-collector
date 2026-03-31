import { IProfile } from 'app/entities/profile/profile.model';
import { IProfileRequestType } from 'app/entities/profile-request-type/profile-request-type.model';

export interface IProfileRequest {
  id: number;
  message?: string | null;
  type?: Pick<IProfileRequestType, 'id'> | null;
  profile?: Pick<IProfile, 'id'> | null;
}

export type NewProfileRequest = Omit<IProfileRequest, 'id'> & { id: null };
