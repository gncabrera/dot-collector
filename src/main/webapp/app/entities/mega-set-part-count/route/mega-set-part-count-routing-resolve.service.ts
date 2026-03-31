import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IMegaSetPartCount } from '../mega-set-part-count.model';
import { MegaSetPartCountService } from '../service/mega-set-part-count.service';

const megaSetPartCountResolve = (route: ActivatedRouteSnapshot): Observable<null | IMegaSetPartCount> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(MegaSetPartCountService);
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

export default megaSetPartCountResolve;
