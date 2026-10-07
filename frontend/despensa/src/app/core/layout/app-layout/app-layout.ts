import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
	imports: [RouterLink, RouterLinkActive, RouterOutlet],
	selector: 'app-layout',
	styleUrl: './app-layout.css',
	templateUrl: './app-layout.html',
})
export class AppLayout {
	protected readonly navigationItems = [
		{ label: 'Inicio', route: '/app', icon: '/assets/icons/inicio.svg', exact: true },
		{ label: 'Grupos', route: '/app/grupos', icon: '/assets/icons/grupos.svg', exact: false },
		{ label: 'Amigos', route: '/app/amigos', icon: '/assets/icons/amigos.svg', exact: false },
	] as const;
}
