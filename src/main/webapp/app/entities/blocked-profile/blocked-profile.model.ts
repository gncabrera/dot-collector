import dayjs from 'dayjs/esm';

import { IProfile } from 'app/entities/profile/profile.model';

export interface IBlockedProfile {
  id: number;
  reason?: string | null;
  dateBlocked?: dayjs.Dayjs | null;
  profile?: Pick<IProfile, 'id'> | null;
  blockedProfile?: Pick<IProfile, 'id'> | null;
}

export type NewBlockedProfile = Omit<IBlockedProfile, 'id'> & { id: null };
