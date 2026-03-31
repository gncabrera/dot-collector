import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IMegaPartType } from '../mega-part-type.model';
import { MegaPartTypeService } from '../service/mega-part-type.service';

const megaPartTypeResolve = (route: ActivatedRouteSnapshot): Observable<null | IMegaPartType> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(MegaPartTypeService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default megaPartTypeResolve;
