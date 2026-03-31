import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import EnvironmentVariableResolve from './route/environment-variable-routing-resolve.service';

const environmentVariableRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/environment-variable').then(m => m.EnvironmentVariable),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/environment-variable-detail').then(m => m.EnvironmentVariableDetail),
    resolve: {
      environmentVariable: EnvironmentVariableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/environment-variable-update').then(m => m.EnvironmentVariableUpdate),
    resolve: {
      environmentVariable: EnvironmentVariableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/environment-variable-update').then(m => m.EnvironmentVariableUpdate),
    resolve: {
      environmentVariable: EnvironmentVariableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default environmentVariableRoute;
