import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IFollowingProfile, NewFollowingProfile } from '../following-profile.model';

export type PartialUpdateFollowingProfile = Partial<IFollowingProfile> & Pick<IFollowingProfile, 'id'>;

type RestOf<T extends IFollowingProfile | NewFollowingProfile> = Omit<T, 'dateFollowing'> & {
  dateFollowing?: string | null;
};

export type RestFollowingProfile = RestOf<IFollowingProfile>;

export type NewRestFollowingProfile = RestOf<NewFollowingProfile>;

export type PartialUpdateRestFollowingProfile = RestOf<PartialUpdateFollowingProfile>;

@Injectable()
export class FollowingProfilesService {
  readonly followingProfilesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly followingProfilesResource = httpResource<RestFollowingProfile[]>(() => {
    const params = this.followingProfilesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of followingProfile that have been fetched. It is updated when the followingProfilesResource emits a new value.
   * In case of error while fetching the followingProfiles, the signal is set to an empty array.
   */
  readonly followingProfiles = computed(() =>
    (this.followingProfilesResource.hasValue() ? this.followingProfilesResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/following-profiles');

  protected convertValueFromServer(restFollowingProfile: RestFollowingProfile): IFollowingProfile {
    return {
      ...restFollowingProfile,
      dateFollowing: restFollowingProfile.dateFollowing ? dayjs(restFollowingProfile.dateFollowing) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class FollowingProfileService extends FollowingProfilesService {
  protected readonly http = inject(HttpClient);

  create(followingProfile: NewFollowingProfile): Observable<IFollowingProfile> {
    const copy = this.convertValueFromClient(followingProfile);
    return this.http.post<RestFollowingProfile>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(followingProfile: IFollowingProfile): Observable<IFollowingProfile> {
    const copy = this.convertValueFromClient(followingProfile);
    return this.http
      .put<RestFollowingProfile>(`${this.resourceUrl}/${encodeURIComponent(this.getFollowingProfileIdentifier(followingProfile))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(followingProfile: PartialUpdateFollowingProfile): Observable<IFollowingProfile> {
    const copy = this.convertValueFromClient(followingProfile);
    return this.http
      .patch<RestFollowingProfile>(`${this.resourceUrl}/${encodeURIComponent(this.getFollowingProfileIdentifier(followingProfile))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IFollowingProfile> {
    return this.http
      .get<RestFollowingProfile>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IFollowingProfile[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFollowingProfile[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getFollowingProfileIdentifier(followingProfile: Pick<IFollowingProfile, 'id'>): number {
    return followingProfile.id;
  }

  compareFollowingProfile(o1: Pick<IFollowingProfile, 'id'> | null, o2: Pick<IFollowingProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getFollowingProfileIdentifier(o1) === this.getFollowingProfileIdentifier(o2) : o1 === o2;
  }

  addFollowingProfileToCollectionIfMissing<Type extends Pick<IFollowingProfile, 'id'>>(
    followingProfileCollection: Type[],
    ...followingProfilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const followingProfiles: Type[] = followingProfilesToCheck.filter(isPresent);
    if (followingProfiles.length > 0) {
      const followingProfileCollectionIdentifiers = followingProfileCollection.map(followingProfileItem =>
        this.getFollowingProfileIdentifier(followingProfileItem),
      );
      const followingProfilesToAdd = followingProfiles.filter(followingProfileItem => {
        const followingProfileIdentifier = this.getFollowingProfileIdentifier(followingProfileItem);
        if (followingProfileCollectionIdentifiers.includes(followingProfileIdentifier)) {
          return false;
        }
        followingProfileCollectionIdentifiers.push(followingProfileIdentifier);
        return true;
      });
      return [...followingProfilesToAdd, ...followingProfileCollection];
    }
    return followingProfileCollection;
  }

  protected convertValueFromClient<T extends IFollowingProfile | NewFollowingProfile | PartialUpdateFollowingProfile>(
    followingProfile: T,
  ): RestOf<T> {
    return {
      ...followingProfile,
      dateFollowing: followingProfile.dateFollowing?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestFollowingProfile): IFollowingProfile {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestFollowingProfile[]): IFollowingProfile[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
