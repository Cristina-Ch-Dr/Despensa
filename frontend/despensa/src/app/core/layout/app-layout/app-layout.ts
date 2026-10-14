import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
	imports: [RouterLink, RouterLinkActive, RouterOutlet],
	selector: 'app-layout',
	styleUrl: './app-layout.css',
	templateUrl: './app-layout.html',
})
export class AppLayout {
	private readonly authService = inject(AuthService);
	private readonly router = inject(Router);

	protected readonly navigationItems = [
		{ label: 'Inicio', route: '/app', icon: '/assets/icons/inicio.svg', exact: true },
		{ label: 'Grupos', route: '/app/grupos', icon: '/assets/icons/grupos.svg', exact: false },
		{ label: 'Amigos', route: '/app/amigos', icon: '/assets/icons/amigos.svg', exact: false },
	] as const;

	protected logout(): void {
		this.authService.logout();
		void this.router.navigateByUrl('/login');
	}
}
