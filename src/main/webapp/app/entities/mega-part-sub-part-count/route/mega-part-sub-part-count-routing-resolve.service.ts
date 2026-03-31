import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IMegaPartSubPartCount } from '../mega-part-sub-part-count.model';
import { MegaPartSubPartCountService } from '../service/mega-part-sub-part-count.service';

const megaPartSubPartCountResolve = (route: ActivatedRouteSnapshot): Observable<null | IMegaPartSubPartCount> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(MegaPartSubPartCountService);
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

export default megaPartSubPartCountResolve;
