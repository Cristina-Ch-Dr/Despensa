import { GrupoResumen } from "./grupo";

export interface UsuarioDto {
    usuarioId: number;
    nombre: string;
    apellidos: string;
    email: string;
    fechaCreacion: string; // ISO datetime
    amigos?: UsuarioResumen[]; // lista reducida para evitar ciclos
    grupos?: GrupoResumen[];   // lista reducida
}

export interface UsuarioResumen {
    usuarioId: number;
    nombre: string;
    apellidos?: string;
}

export class Usuario {
    usuarioId: number | undefined;
    nombre: string | undefined;
    apellidos: string | undefined;
    email: string | undefined;
    fechaCreacion: Date;
    amigos: UsuarioResumen[] = [];
    grupos: GrupoResumen[] = [];

    constructor(data: Partial<Usuario>) {
        Object.assign(this, data);
        this.fechaCreacion = data?.fechaCreacion ? new Date(data.fechaCreacion) : new Date();
        this.amigos = data?.amigos ?? [];
        this.grupos = data?.grupos ?? [];
    }

    getFullName(): string {
        return `${this.nombre} ${this.apellidos ?? ''}`.trim();
    }
}