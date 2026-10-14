import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { AmigosService } from './amigos.service';

@Component({
  imports: [],
  selector: 'app-amigos',
  styleUrl: './amigos.css',
  templateUrl: './amigos.html',
})
export class Amigos {
  private readonly authService = inject(AuthService);
  private readonly amigosService = inject(AmigosService);

  protected readonly usuarioId = this.authService.usuarioId;
  protected readonly isSending = signal(false);
  protected readonly isResponding = signal(false);
  protected readonly sendError = signal('');
  protected readonly sendSuccess = signal('');
  protected readonly responseError = signal('');
  protected readonly responseSuccess = signal('');

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
      next: (response) => {
        this.isSending.set(false);
        this.sendSuccess.set(`Solicitud #${response.solicitudId} enviada a ${emailDestinatario}.`);
        form.reset();
      },
      error: (error: HttpErrorResponse) => {
        this.isSending.set(false);
        this.sendError.set(this.mensajeErrorEnvio(error));
      },
    });
  }

  protected responderSolicitud(event: SubmitEvent): void {
    event.preventDefault();
    const form = event.currentTarget as HTMLFormElement;
    const formData = new FormData(form);
    const solicitudId = Number(formData.get('solicitudId'));
    const accion =
      event.submitter instanceof HTMLButtonElement ? event.submitter.value : '';
    const destinatarioId = this.usuarioId();

    this.responseError.set('');
    this.responseSuccess.set('');

    if (accion !== 'aceptar' && accion !== 'rechazar') {
      this.responseError.set('Selecciona si quieres aceptar o rechazar la solicitud.');
      return;
    }

    if (destinatarioId === null) {
      this.responseError.set('No hemos podido identificar tu cuenta. Inicia sesión de nuevo.');
      return;
    }

    this.isResponding.set(true);
    const responder =
      accion === 'aceptar'
        ? this.amigosService.aceptarSolicitud(solicitudId, { destinatarioId })
        : this.amigosService.rechazarSolicitud(solicitudId, { destinatarioId });

    responder.subscribe({
      next: (response) => {
        this.isResponding.set(false);
        const estado = response.estado === 'ACEPTADA' ? 'aceptada' : 'rechazada';
        this.responseSuccess.set(`Solicitud #${response.solicitudId} ${estado} correctamente.`);
        form.reset();
      },
      error: (error: HttpErrorResponse) => {
        this.isResponding.set(false);
        this.responseError.set(this.mensajeErrorRespuesta(error));
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
      return 'No se ha encontrado una solicitud pendiente con ese ID.';
    }
    if (error.status === 409) {
      return 'Esta solicitud ya ha sido respondida.';
    }
    if (error.status === 400) {
      return 'Solo la persona destinataria puede responder a esta solicitud.';
    }
    return 'No hemos podido responder a la solicitud. Inténtalo de nuevo más tarde.';
  }
}
