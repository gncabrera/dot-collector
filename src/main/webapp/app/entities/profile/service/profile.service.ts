import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProfile, NewProfile } from '../profile.model';

export type PartialUpdateProfile = Partial<IProfile> & Pick<IProfile, 'id'>;

@Injectable()
export class ProfilesService {
  readonly profilesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly profilesResource = httpResource<IProfile[]>(() => {
    const params = this.profilesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of profile that have been fetched. It is updated when the profilesResource emits a new value.
   * In case of error while fetching the profiles, the signal is set to an empty array.
   */
  readonly profiles = computed(() => (this.profilesResource.hasValue() ? this.profilesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/profiles');
}

@Injectable({ providedIn: 'root' })
export class ProfileService extends ProfilesService {
  protected readonly http = inject(HttpClient);

  create(profile: NewProfile): Observable<IProfile> {
    return this.http.post<IProfile>(this.resourceUrl, profile);
  }

  update(profile: IProfile): Observable<IProfile> {
    return this.http.put<IProfile>(`${this.resourceUrl}/${encodeURIComponent(this.getProfileIdentifier(profile))}`, profile);
  }

  partialUpdate(profile: PartialUpdateProfile): Observable<IProfile> {
    return this.http.patch<IProfile>(`${this.resourceUrl}/${encodeURIComponent(this.getProfileIdentifier(profile))}`, profile);
  }

  find(id: number): Observable<IProfile> {
    return this.http.get<IProfile>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProfile[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProfile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfileIdentifier(profile: Pick<IProfile, 'id'>): number {
    return profile.id;
  }

  compareProfile(o1: Pick<IProfile, 'id'> | null, o2: Pick<IProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfileIdentifier(o1) === this.getProfileIdentifier(o2) : o1 === o2;
  }

  addProfileToCollectionIfMissing<Type extends Pick<IProfile, 'id'>>(
    profileCollection: Type[],
    ...profilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profiles: Type[] = profilesToCheck.filter(isPresent);
    if (profiles.length > 0) {
      const profileCollectionIdentifiers = profileCollection.map(profileItem => this.getProfileIdentifier(profileItem));
      const profilesToAdd = profiles.filter(profileItem => {
        const profileIdentifier = this.getProfileIdentifier(profileItem);
        if (profileCollectionIdentifiers.includes(profileIdentifier)) {
          return false;
        }
        profileCollectionIdentifiers.push(profileIdentifier);
        return true;
      });
      return [...profilesToAdd, ...profileCollection];
    }
    return profileCollection;
  }
}
