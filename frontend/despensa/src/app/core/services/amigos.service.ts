import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface EnviarSolicitudAmistadRequest {
  solicitanteId: number;
  emailDestinatario: string;
}

export interface ResponderSolicitudAmistadRequest {
  destinatarioId: number;
}

export type EstadoSolicitudAmistad = 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';

export interface SolicitudAmistadResponse {
  solicitudId: number;
  solicitante: AmigoResponse;
  destinatario: AmigoResponse;
  estado: EstadoSolicitudAmistad;
}

export type DireccionSolicitudAmistad = 'RECIBIDAS' | 'ENVIADAS';

export interface AmigoResponse {
  usuarioId: number;
  nombre: string;
  apellidos: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class AmigosService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/amigos`;

  listarAmigos(usuarioId: number): Observable<AmigoResponse[]> {
    return this.http.get<AmigoResponse[]>(this.apiUrl, {
      params: { usuarioId },
    });
  }

  listarSolicitudes(
    usuarioId: number,
    direccion: DireccionSolicitudAmistad,
  ): Observable<SolicitudAmistadResponse[]> {
    return this.http.get<SolicitudAmistadResponse[]>(`${this.apiUrl}/solicitudes`, {
      params: { usuarioId, estado: 'PENDIENTE', direccion },
    });
  }

  enviarSolicitud(
    request: EnviarSolicitudAmistadRequest,
  ): Observable<SolicitudAmistadResponse> {
    return this.http.post<SolicitudAmistadResponse>(`${this.apiUrl}/solicitudes`, request);
  }

  aceptarSolicitud(
    solicitudId: number,
    request: ResponderSolicitudAmistadRequest,
  ): Observable<SolicitudAmistadResponse> {
    return this.http.post<SolicitudAmistadResponse>(
      `${this.apiUrl}/solicitudes/${solicitudId}/aceptar`,
      request,
    );
  }

  rechazarSolicitud(
    solicitudId: number,
    request: ResponderSolicitudAmistadRequest,
  ): Observable<SolicitudAmistadResponse> {
    return this.http.post<SolicitudAmistadResponse>(
      `${this.apiUrl}/solicitudes/${solicitudId}/rechazar`,
      request,
    );
  }

  cancelarSolicitud(solicitudId: number, solicitanteId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/solicitudes/${solicitudId}`, {
      params: { solicitanteId },
    });
  }
}
