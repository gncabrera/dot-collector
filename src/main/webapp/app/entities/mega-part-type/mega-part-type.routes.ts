import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaPartTypeResolve from './route/mega-part-type-routing-resolve.service';

const megaPartTypeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-part-type').then(m => m.MegaPartType),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-part-type-detail').then(m => m.MegaPartTypeDetail),
    resolve: {
      megaPartType: MegaPartTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-part-type-update').then(m => m.MegaPartTypeUpdate),
    resolve: {
      megaPartType: MegaPartTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-part-type-update').then(m => m.MegaPartTypeUpdate),
    resolve: {
      megaPartType: MegaPartTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaPartTypeRoute;
