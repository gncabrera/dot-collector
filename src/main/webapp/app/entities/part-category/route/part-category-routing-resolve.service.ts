import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IPartCategory } from '../part-category.model';
import { PartCategoryService } from '../service/part-category.service';

const partCategoryResolve = (route: ActivatedRouteSnapshot): Observable<null | IPartCategory> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(PartCategoryService);
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

export default partCategoryResolve;
