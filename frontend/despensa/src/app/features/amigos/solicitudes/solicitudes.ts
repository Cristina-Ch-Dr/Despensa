import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import {
  AmigosService,
  DireccionSolicitudAmistad,
  SolicitudAmistadResponse,
} from '../../../core/services/amigos.service';

@Component({
  imports: [RouterLink],
  selector: 'app-solicitudes',
  styleUrl: './solicitudes.css',
  templateUrl: './solicitudes.html',
})
export class Solicitudes implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly amigosService = inject(AmigosService);

  protected readonly usuarioId = this.authService.usuarioId;
  protected readonly solicitudesRecibidas = signal<SolicitudAmistadResponse[]>([]);
  protected readonly solicitudesEnviadas = signal<SolicitudAmistadResponse[]>([]);
  protected readonly isLoadingRecibidas = signal(false);
  protected readonly isLoadingEnviadas = signal(false);
  protected readonly isSending = signal(false);
  protected readonly updatingSolicitudId = signal<number | null>(null);
  protected readonly recibidasError = signal('');
  protected readonly enviadasError = signal('');
  protected readonly sendError = signal('');
  protected readonly sendSuccess = signal('');
  protected readonly gestionError = signal('');
  protected readonly gestionSuccess = signal('');

  ngOnInit(): void {
    if (this.usuarioId() === null) {
      return;
    }
    this.cargarSolicitudes('RECIBIDAS');
    this.cargarSolicitudes('ENVIADAS');
  }

  protected cargarSolicitudes(direccion: DireccionSolicitudAmistad): void {
    const usuarioId = this.usuarioId();
    if (usuarioId === null) {
      return;
    }

    const loading = direccion === 'RECIBIDAS' ? this.isLoadingRecibidas : this.isLoadingEnviadas;
    const errorMessage = direccion === 'RECIBIDAS' ? this.recibidasError : this.enviadasError;
    loading.set(true);
    errorMessage.set('');

    this.amigosService.listarSolicitudes(usuarioId, direccion).subscribe({
      next: (solicitudes) => {
        if (direccion === 'RECIBIDAS') {
          this.solicitudesRecibidas.set(solicitudes);
        } else {
          this.solicitudesEnviadas.set(solicitudes);
        }
        loading.set(false);
      },
      error: () => {
        loading.set(false);
        errorMessage.set('No hemos podido cargar las solicitudes. Inténtalo de nuevo.');
      },
    });
  }

  protected enviarSolicitud(event: SubmitEvent): void {
    event.preventDefault();
    const form = event.currentTarget as HTMLFormElement;
    const emailDestinatario = String(new FormData(form).get('emailDestinatario') ?? '')
      .trim()
      .toLowerCase();
    const solicitanteId = this.usuarioId();

    this.sendError.set('');
    this.sendSuccess.set('');
    if (solicitanteId === null) {
      this.sendError.set('No hemos podido identificar tu cuenta. Inicia sesión de nuevo.');
      return;
    }

    this.isSending.set(true);
    this.amigosService.enviarSolicitud({ solicitanteId, emailDestinatario }).subscribe({
      next: () => {
        this.isSending.set(false);
        this.sendSuccess.set(`Solicitud enviada a ${emailDestinatario}.`);
        form.reset();
        this.cargarSolicitudes('ENVIADAS');
      },
      error: (error: HttpErrorResponse) => {
        this.isSending.set(false);
        this.sendError.set(this.mensajeErrorEnvio(error));
      },
    });
  }

  protected responderSolicitud(solicitudId: number, accion: 'aceptar' | 'rechazar'): void {
    const destinatarioId = this.usuarioId();
    this.gestionError.set('');
    this.gestionSuccess.set('');

    if (destinatarioId === null) {
      this.gestionError.set('No hemos podido identificar tu cuenta. Inicia sesión de nuevo.');
      return;
    }

    this.updatingSolicitudId.set(solicitudId);
    const responder =
      accion === 'aceptar'
        ? this.amigosService.aceptarSolicitud(solicitudId, { destinatarioId })
        : this.amigosService.rechazarSolicitud(solicitudId, { destinatarioId });

    responder.subscribe({
      next: (response) => {
        this.updatingSolicitudId.set(null);
        const estado = response.estado === 'ACEPTADA' ? 'aceptada' : 'rechazada';
        this.gestionSuccess.set(`Solicitud ${estado} correctamente.`);
        this.cargarSolicitudes('RECIBIDAS');
        this.cargarSolicitudes('ENVIADAS');
      },
      error: (error: HttpErrorResponse) => {
        this.updatingSolicitudId.set(null);
        this.gestionError.set(this.mensajeErrorRespuesta(error));
      },
    });
  }

  protected cancelarSolicitud(solicitudId: number): void {
    const solicitanteId = this.usuarioId();
    this.gestionError.set('');
    this.gestionSuccess.set('');

    if (solicitanteId === null) {
      this.gestionError.set('No hemos podido identificar tu cuenta. Inicia sesión de nuevo.');
      return;
    }

    this.updatingSolicitudId.set(solicitudId);
    this.amigosService.cancelarSolicitud(solicitudId, solicitanteId).subscribe({
      next: () => {
        this.updatingSolicitudId.set(null);
        this.gestionSuccess.set('Solicitud cancelada.');
        this.cargarSolicitudes('ENVIADAS');
      },
      error: (error: HttpErrorResponse) => {
        this.updatingSolicitudId.set(null);
        this.gestionError.set(this.mensajeErrorCancelacion(error));
      },
    });
  }

  private mensajeErrorEnvio(error: HttpErrorResponse): string {
    if (error.status === 404) {
      return 'No existe una cuenta con ese correo electrónico.';
    }
    if (error.status === 409) {
      return 'Ya existe una solicitud pendiente o ya sois amigos.';
    }
    if (error.status === 400) {
      return 'El correo electrónico o la solicitud no son válidos.';
    }
    return 'No hemos podido enviar la solicitud. Inténtalo de nuevo más tarde.';
  }

  private mensajeErrorRespuesta(error: HttpErrorResponse): string {
    if (error.status === 404) {
      return 'No se ha encontrado una solicitud pendiente.';
    }
    if (error.status === 409) {
      return 'Esta solicitud ya ha sido respondida.';
    }
    if (error.status === 400) {
      return 'Solo la persona destinataria puede responder a esta solicitud.';
    }
    return 'No hemos podido responder a la solicitud. Inténtalo de nuevo más tarde.';
  }

  private mensajeErrorCancelacion(error: HttpErrorResponse): string {
    if (error.status === 404) {
      return 'No se ha encontrado la solicitud que quieres cancelar.';
    }
    if (error.status === 409) {
      return 'Solo se pueden cancelar solicitudes que sigan pendientes.';
    }
    if (error.status === 400) {
      return 'Solo puedes cancelar las solicitudes que tú has enviado.';
    }
    return 'No hemos podido cancelar la solicitud. Inténtalo de nuevo más tarde.';
  }
}
