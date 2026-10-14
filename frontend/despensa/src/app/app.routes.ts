import { Routes } from '@angular/router';

export const routes: Routes = [
	{
		path: 'app',
		loadComponent: () => import('./core/layout/app-layout/app-layout').then((module) => module.AppLayout),
		children: [
			{
				path: '',
				pathMatch: 'full',
				loadComponent: () => import('./features/home/home').then((module) => module.Home),
			},
			{
				path: 'grupos',
				loadComponent: () => import('./features/grupos/grupos').then((module) => module.Grupos),
			},
			{
				path: 'amigos/solicitudes',
				loadComponent: () => import('./features/amigos/solicitudes/solicitudes').then((module) => module.Solicitudes),
			},
			{
				path: 'amigos',
				loadComponent: () => import('./features/amigos/amigos').then((module) => module.Amigos),
			},
		],
	},
	{
		path: 'login',
		loadComponent: () => import('./features/auth/login/login').then((module) => module.Login),
	},
	{
		path: 'register',
		loadComponent: () => import('./features/auth/register/register').then((module) => module.Register),
	},
	{ path: '', pathMatch: 'full', redirectTo: 'login' },
	{ path: '**', redirectTo: 'login' },
];
