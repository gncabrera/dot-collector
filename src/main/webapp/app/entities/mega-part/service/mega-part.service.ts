import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaPart, NewMegaPart } from '../mega-part.model';

export type PartialUpdateMegaPart = Partial<IMegaPart> & Pick<IMegaPart, 'id'>;

type RestOf<T extends IMegaPart | NewMegaPart> = Omit<T, 'releaseDate'> & {
  releaseDate?: string | null;
};

export type RestMegaPart = RestOf<IMegaPart>;

export type NewRestMegaPart = RestOf<NewMegaPart>;

export type PartialUpdateRestMegaPart = RestOf<PartialUpdateMegaPart>;

@Injectable()
export class MegaPartsService {
  readonly megaPartsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly megaPartsResource = httpResource<RestMegaPart[]>(() => {
    const params = this.megaPartsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaPart that have been fetched. It is updated when the megaPartsResource emits a new value.
   * In case of error while fetching the megaParts, the signal is set to an empty array.
   */
  readonly megaParts = computed(() =>
    (this.megaPartsResource.hasValue() ? this.megaPartsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-parts');

  protected convertValueFromServer(restMegaPart: RestMegaPart): IMegaPart {
    return {
      ...restMegaPart,
      releaseDate: restMegaPart.releaseDate ? dayjs(restMegaPart.releaseDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class MegaPartService extends MegaPartsService {
  protected readonly http = inject(HttpClient);

  create(megaPart: NewMegaPart): Observable<IMegaPart> {
    const copy = this.convertValueFromClient(megaPart);
    return this.http.post<RestMegaPart>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(megaPart: IMegaPart): Observable<IMegaPart> {
    const copy = this.convertValueFromClient(megaPart);
    return this.http
      .put<RestMegaPart>(`${this.resourceUrl}/${encodeURIComponent(this.getMegaPartIdentifier(megaPart))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(megaPart: PartialUpdateMegaPart): Observable<IMegaPart> {
    const copy = this.convertValueFromClient(megaPart);
    return this.http
      .patch<RestMegaPart>(`${this.resourceUrl}/${encodeURIComponent(this.getMegaPartIdentifier(megaPart))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IMegaPart> {
    return this.http
      .get<RestMegaPart>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IMegaPart[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMegaPart[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaPartIdentifier(megaPart: Pick<IMegaPart, 'id'>): number {
    return megaPart.id;
  }

  compareMegaPart(o1: Pick<IMegaPart, 'id'> | null, o2: Pick<IMegaPart, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaPartIdentifier(o1) === this.getMegaPartIdentifier(o2) : o1 === o2;
  }

  addMegaPartToCollectionIfMissing<Type extends Pick<IMegaPart, 'id'>>(
    megaPartCollection: Type[],
    ...megaPartsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaParts: Type[] = megaPartsToCheck.filter(isPresent);
    if (megaParts.length > 0) {
      const megaPartCollectionIdentifiers = megaPartCollection.map(megaPartItem => this.getMegaPartIdentifier(megaPartItem));
      const megaPartsToAdd = megaParts.filter(megaPartItem => {
        const megaPartIdentifier = this.getMegaPartIdentifier(megaPartItem);
        if (megaPartCollectionIdentifiers.includes(megaPartIdentifier)) {
          return false;
        }
        megaPartCollectionIdentifiers.push(megaPartIdentifier);
        return true;
      });
      return [...megaPartsToAdd, ...megaPartCollection];
    }
    return megaPartCollection;
  }

  protected convertValueFromClient<T extends IMegaPart | NewMegaPart | PartialUpdateMegaPart>(megaPart: T): RestOf<T> {
    return {
      ...megaPart,
      releaseDate: megaPart.releaseDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestMegaPart): IMegaPart {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestMegaPart[]): IMegaPart[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
