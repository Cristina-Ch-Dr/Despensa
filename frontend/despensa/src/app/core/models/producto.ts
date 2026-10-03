import { ListaResumen } from "./lista";

export interface ProductoDto {
    productoId: number;
    nombre: string;
    descripcion?: string;
    precio?: number;
    categoria?: string;
    fechaCreacion: string;
    listas?: ListaResumen[]; // resumen de listas que contienen el producto
}

export interface ProductoResumen {
    productoId: number;
    nombre: string;
    precio?: number;
}
