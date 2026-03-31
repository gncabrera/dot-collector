import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IBlockedProfile, NewBlockedProfile } from '../blocked-profile.model';

export type PartialUpdateBlockedProfile = Partial<IBlockedProfile> & Pick<IBlockedProfile, 'id'>;

type RestOf<T extends IBlockedProfile | NewBlockedProfile> = Omit<T, 'dateBlocked'> & {
  dateBlocked?: string | null;
};

export type RestBlockedProfile = RestOf<IBlockedProfile>;

export type NewRestBlockedProfile = RestOf<NewBlockedProfile>;

export type PartialUpdateRestBlockedProfile = RestOf<PartialUpdateBlockedProfile>;

@Injectable()
export class BlockedProfilesService {
  readonly blockedProfilesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly blockedProfilesResource = httpResource<RestBlockedProfile[]>(() => {
    const params = this.blockedProfilesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of blockedProfile that have been fetched. It is updated when the blockedProfilesResource emits a new value.
   * In case of error while fetching the blockedProfiles, the signal is set to an empty array.
   */
  readonly blockedProfiles = computed(() =>
    (this.blockedProfilesResource.hasValue() ? this.blockedProfilesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/blocked-profiles');

  protected convertValueFromServer(restBlockedProfile: RestBlockedProfile): IBlockedProfile {
    return {
      ...restBlockedProfile,
      dateBlocked: restBlockedProfile.dateBlocked ? dayjs(restBlockedProfile.dateBlocked) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class BlockedProfileService extends BlockedProfilesService {
  protected readonly http = inject(HttpClient);

  create(blockedProfile: NewBlockedProfile): Observable<IBlockedProfile> {
    const copy = this.convertValueFromClient(blockedProfile);
    return this.http.post<RestBlockedProfile>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(blockedProfile: IBlockedProfile): Observable<IBlockedProfile> {
    const copy = this.convertValueFromClient(blockedProfile);
    return this.http
      .put<RestBlockedProfile>(`${this.resourceUrl}/${encodeURIComponent(this.getBlockedProfileIdentifier(blockedProfile))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(blockedProfile: PartialUpdateBlockedProfile): Observable<IBlockedProfile> {
    const copy = this.convertValueFromClient(blockedProfile);
    return this.http
      .patch<RestBlockedProfile>(`${this.resourceUrl}/${encodeURIComponent(this.getBlockedProfileIdentifier(blockedProfile))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IBlockedProfile> {
    return this.http
      .get<RestBlockedProfile>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IBlockedProfile[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBlockedProfile[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getBlockedProfileIdentifier(blockedProfile: Pick<IBlockedProfile, 'id'>): number {
    return blockedProfile.id;
  }

  compareBlockedProfile(o1: Pick<IBlockedProfile, 'id'> | null, o2: Pick<IBlockedProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getBlockedProfileIdentifier(o1) === this.getBlockedProfileIdentifier(o2) : o1 === o2;
  }

  addBlockedProfileToCollectionIfMissing<Type extends Pick<IBlockedProfile, 'id'>>(
    blockedProfileCollection: Type[],
    ...blockedProfilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const blockedProfiles: Type[] = blockedProfilesToCheck.filter(isPresent);
    if (blockedProfiles.length > 0) {
      const blockedProfileCollectionIdentifiers = blockedProfileCollection.map(blockedProfileItem =>
        this.getBlockedProfileIdentifier(blockedProfileItem),
      );
      const blockedProfilesToAdd = blockedProfiles.filter(blockedProfileItem => {
        const blockedProfileIdentifier = this.getBlockedProfileIdentifier(blockedProfileItem);
        if (blockedProfileCollectionIdentifiers.includes(blockedProfileIdentifier)) {
          return false;
        }
        blockedProfileCollectionIdentifiers.push(blockedProfileIdentifier);
        return true;
      });
      return [...blockedProfilesToAdd, ...blockedProfileCollection];
    }
    return blockedProfileCollection;
  }

  protected convertValueFromClient<T extends IBlockedProfile | NewBlockedProfile | PartialUpdateBlockedProfile>(
    blockedProfile: T,
  ): RestOf<T> {
    return {
      ...blockedProfile,
      dateBlocked: blockedProfile.dateBlocked?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestBlockedProfile): IBlockedProfile {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestBlockedProfile[]): IBlockedProfile[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
