import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaPartResolve from './route/mega-part-routing-resolve.service';

const megaPartRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-part').then(m => m.MegaPart),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-part-detail').then(m => m.MegaPartDetail),
    resolve: {
      megaPart: MegaPartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-part-update').then(m => m.MegaPartUpdate),
    resolve: {
      megaPart: MegaPartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-part-update').then(m => m.MegaPartUpdate),
    resolve: {
      megaPart: MegaPartResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaPartRoute;
