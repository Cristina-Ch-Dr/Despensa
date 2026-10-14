import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

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
  solicitanteId: number;
  destinatarioId: number;
  estado: EstadoSolicitudAmistad;
}

@Injectable({ providedIn: 'root' })
export class AmigosService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/amigos/solicitudes`;

  enviarSolicitud(
    request: EnviarSolicitudAmistadRequest,
  ): Observable<SolicitudAmistadResponse> {
    return this.http.post<SolicitudAmistadResponse>(this.apiUrl, request);
  }

  aceptarSolicitud(
    solicitudId: number,
    request: ResponderSolicitudAmistadRequest,
  ): Observable<SolicitudAmistadResponse> {
    return this.http.post<SolicitudAmistadResponse>(
      `${this.apiUrl}/${solicitudId}/aceptar`,
      request,
    );
  }

  rechazarSolicitud(
    solicitudId: number,
    request: ResponderSolicitudAmistadRequest,
  ): Observable<SolicitudAmistadResponse> {
    return this.http.post<SolicitudAmistadResponse>(
      `${this.apiUrl}/${solicitudId}/rechazar`,
      request,
    );
  }
}
