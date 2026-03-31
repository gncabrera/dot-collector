import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaSet, NewMegaSet } from '../mega-set.model';

export type PartialUpdateMegaSet = Partial<IMegaSet> & Pick<IMegaSet, 'id'>;

type RestOf<T extends IMegaSet | NewMegaSet> = Omit<T, 'releaseDate'> & {
  releaseDate?: string | null;
};

export type RestMegaSet = RestOf<IMegaSet>;

export type NewRestMegaSet = RestOf<NewMegaSet>;

export type PartialUpdateRestMegaSet = RestOf<PartialUpdateMegaSet>;

@Injectable()
export class MegaSetsService {
  readonly megaSetsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly megaSetsResource = httpResource<RestMegaSet[]>(() => {
    const params = this.megaSetsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaSet that have been fetched. It is updated when the megaSetsResource emits a new value.
   * In case of error while fetching the megaSets, the signal is set to an empty array.
   */
  readonly megaSets = computed(() =>
    (this.megaSetsResource.hasValue() ? this.megaSetsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-sets');

  protected convertValueFromServer(restMegaSet: RestMegaSet): IMegaSet {
    return {
      ...restMegaSet,
      releaseDate: restMegaSet.releaseDate ? dayjs(restMegaSet.releaseDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class MegaSetService extends MegaSetsService {
  protected readonly http = inject(HttpClient);

  create(megaSet: NewMegaSet): Observable<IMegaSet> {
    const copy = this.convertValueFromClient(megaSet);
    return this.http.post<RestMegaSet>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(megaSet: IMegaSet): Observable<IMegaSet> {
    const copy = this.convertValueFromClient(megaSet);
    return this.http
      .put<RestMegaSet>(`${this.resourceUrl}/${encodeURIComponent(this.getMegaSetIdentifier(megaSet))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(megaSet: PartialUpdateMegaSet): Observable<IMegaSet> {
    const copy = this.convertValueFromClient(megaSet);
    return this.http
      .patch<RestMegaSet>(`${this.resourceUrl}/${encodeURIComponent(this.getMegaSetIdentifier(megaSet))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IMegaSet> {
    return this.http
      .get<RestMegaSet>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IMegaSet[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMegaSet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaSetIdentifier(megaSet: Pick<IMegaSet, 'id'>): number {
    return megaSet.id;
  }

  compareMegaSet(o1: Pick<IMegaSet, 'id'> | null, o2: Pick<IMegaSet, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaSetIdentifier(o1) === this.getMegaSetIdentifier(o2) : o1 === o2;
  }

  addMegaSetToCollectionIfMissing<Type extends Pick<IMegaSet, 'id'>>(
    megaSetCollection: Type[],
    ...megaSetsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaSets: Type[] = megaSetsToCheck.filter(isPresent);
    if (megaSets.length > 0) {
      const megaSetCollectionIdentifiers = megaSetCollection.map(megaSetItem => this.getMegaSetIdentifier(megaSetItem));
      const megaSetsToAdd = megaSets.filter(megaSetItem => {
        const megaSetIdentifier = this.getMegaSetIdentifier(megaSetItem);
        if (megaSetCollectionIdentifiers.includes(megaSetIdentifier)) {
          return false;
        }
        megaSetCollectionIdentifiers.push(megaSetIdentifier);
        return true;
      });
      return [...megaSetsToAdd, ...megaSetCollection];
    }
    return megaSetCollection;
  }

  protected convertValueFromClient<T extends IMegaSet | NewMegaSet | PartialUpdateMegaSet>(megaSet: T): RestOf<T> {
    return {
      ...megaSet,
      releaseDate: megaSet.releaseDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestMegaSet): IMegaSet {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestMegaSet[]): IMegaSet[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
