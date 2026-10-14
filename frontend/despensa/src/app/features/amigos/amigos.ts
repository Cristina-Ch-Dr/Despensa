import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import {
  AmigosService,
  AmigoResponse,
  SolicitudAmistadResponse,
} from '../../core/services/amigos.service';

@Component({
  imports: [RouterLink],
  selector: 'app-amigos',
  styleUrl: './amigos.css',
  templateUrl: './amigos.html',
})
export class Amigos implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly amigosService = inject(AmigosService);

  protected readonly usuarioId = this.authService.usuarioId;
  protected readonly amigos = signal<AmigoResponse[]>([]);
  protected readonly solicitudesRecibidas = signal<SolicitudAmistadResponse[]>([]);
  protected readonly isLoadingAmigos = signal(false);
  protected readonly isLoadingSolicitudes = signal(false);
  protected readonly amigosError = signal('');
  protected readonly solicitudesError = signal('');

  ngOnInit(): void {
    if (this.usuarioId() === null) {
      return;
    }
    this.cargarAmigos();
    this.cargarSolicitudesPendientes();
  }

  protected cargarAmigos(): void {
    const usuarioId = this.usuarioId();
    if (usuarioId === null) {
      return;
    }

    this.isLoadingAmigos.set(true);
    this.amigosError.set('');
    this.amigosService.listarAmigos(usuarioId).subscribe({
      next: (amigos) => {
        this.amigos.set(amigos);
        this.isLoadingAmigos.set(false);
      },
      error: () => {
        this.isLoadingAmigos.set(false);
        this.amigosError.set('No hemos podido cargar tus amigos. Inténtalo de nuevo.');
      },
    });
  }

  private cargarSolicitudesPendientes(): void {
    const usuarioId = this.usuarioId();
    if (usuarioId === null) {
      return;
    }

    this.isLoadingSolicitudes.set(true);
    this.solicitudesError.set('');
    this.amigosService.listarSolicitudes(usuarioId, 'RECIBIDAS').subscribe({
      next: (solicitudes) => {
        this.solicitudesRecibidas.set(solicitudes);
        this.isLoadingSolicitudes.set(false);
      },
      error: () => {
        this.isLoadingSolicitudes.set(false);
        this.solicitudesError.set(
          'No hemos podido comprobar si tienes solicitudes pendientes.',
        );
      },
    });
  }
}
