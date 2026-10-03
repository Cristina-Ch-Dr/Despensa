import { GrupoResumen } from "./grupo";
import { ProductoResumen } from "./producto";

export interface ListaDto {
    listaId: number;
    nombre: string;
    descripcion?: string;
    fechaCreacion: string;
    grupo?: GrupoResumen;        // referencia al grupo (resumen)
    productos?: ProductoResumen[]; // productos en la lista
}

export interface ListaResumen {
    listaId: number;
    nombre: string;
}

