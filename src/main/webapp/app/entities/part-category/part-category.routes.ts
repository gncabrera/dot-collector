import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PartCategoryResolve from './route/part-category-routing-resolve.service';

const partCategoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/part-category').then(m => m.PartCategory),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/part-category-detail').then(m => m.PartCategoryDetail),
    resolve: {
      partCategory: PartCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/part-category-update').then(m => m.PartCategoryUpdate),
    resolve: {
      partCategory: PartCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/part-category-update').then(m => m.PartCategoryUpdate),
    resolve: {
      partCategory: PartCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default partCategoryRoute;
