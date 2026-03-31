import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaAsset, NewMegaAsset } from '../mega-asset.model';

export type PartialUpdateMegaAsset = Partial<IMegaAsset> & Pick<IMegaAsset, 'id'>;

@Injectable()
export class MegaAssetsService {
  readonly megaAssetsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly megaAssetsResource = httpResource<IMegaAsset[]>(() => {
    const params = this.megaAssetsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaAsset that have been fetched. It is updated when the megaAssetsResource emits a new value.
   * In case of error while fetching the megaAssets, the signal is set to an empty array.
   */
  readonly megaAssets = computed(() => (this.megaAssetsResource.hasValue() ? this.megaAssetsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-assets');
}

@Injectable({ providedIn: 'root' })
export class MegaAssetService extends MegaAssetsService {
  protected readonly http = inject(HttpClient);

  create(megaAsset: NewMegaAsset): Observable<IMegaAsset> {
    return this.http.post<IMegaAsset>(this.resourceUrl, megaAsset);
  }

  update(megaAsset: IMegaAsset): Observable<IMegaAsset> {
    return this.http.put<IMegaAsset>(`${this.resourceUrl}/${encodeURIComponent(this.getMegaAssetIdentifier(megaAsset))}`, megaAsset);
  }

  partialUpdate(megaAsset: PartialUpdateMegaAsset): Observable<IMegaAsset> {
    return this.http.patch<IMegaAsset>(`${this.resourceUrl}/${encodeURIComponent(this.getMegaAssetIdentifier(megaAsset))}`, megaAsset);
  }

  find(id: number): Observable<IMegaAsset> {
    return this.http.get<IMegaAsset>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IMegaAsset[]>> {
    const options = createRequestOption(req);
    return this.http.get<IMegaAsset[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaAssetIdentifier(megaAsset: Pick<IMegaAsset, 'id'>): number {
    return megaAsset.id;
  }

  compareMegaAsset(o1: Pick<IMegaAsset, 'id'> | null, o2: Pick<IMegaAsset, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaAssetIdentifier(o1) === this.getMegaAssetIdentifier(o2) : o1 === o2;
  }

  addMegaAssetToCollectionIfMissing<Type extends Pick<IMegaAsset, 'id'>>(
    megaAssetCollection: Type[],
    ...megaAssetsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaAssets: Type[] = megaAssetsToCheck.filter(isPresent);
    if (megaAssets.length > 0) {
      const megaAssetCollectionIdentifiers = megaAssetCollection.map(megaAssetItem => this.getMegaAssetIdentifier(megaAssetItem));
      const megaAssetsToAdd = megaAssets.filter(megaAssetItem => {
        const megaAssetIdentifier = this.getMegaAssetIdentifier(megaAssetItem);
        if (megaAssetCollectionIdentifiers.includes(megaAssetIdentifier)) {
          return false;
        }
        megaAssetCollectionIdentifiers.push(megaAssetIdentifier);
        return true;
      });
      return [...megaAssetsToAdd, ...megaAssetCollection];
    }
    return megaAssetCollection;
  }
}
