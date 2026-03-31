import { AssetType } from 'app/entities/enumerations/asset-type.model';
import { IMegaPart } from 'app/entities/mega-part/mega-part.model';
import { IMegaSet } from 'app/entities/mega-set/mega-set.model';

export interface IMegaAsset {
  id: number;
  name?: string | null;
  description?: string | null;
  path?: string | null;
  type?: keyof typeof AssetType | null;
  set?: Pick<IMegaSet, 'id'> | null;
  part?: Pick<IMegaPart, 'id'> | null;
}

export type NewMegaAsset = Omit<IMegaAsset, 'id'> & { id: null };
