package com.maxximundo.mcp.tools.service;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface MaxximundoTool {

	/** Request para buscar cartera por cédula o RUC. */
	public record ClienteRequest(String cedulaRuc) {
	}

	/** Item de cartera (puedes añadir más campos si quieres). */
	public record ClienteItem(String cliente, String ruc, String ciudad, String vendedor, String empresa, String correo,
			String telefono, BigDecimal cupo, BigDecimal totalCartera, BigDecimal disponible, BigDecimal carteraVencida,
			String comentario) {
	}

	/** Respuesta: lista de registros de cartera para ese RUC. */
	public record ClienteResponse(List<ClienteItem> items) {
	}

	// --------- Registros para Productos ---------

	/** Request de búsqueda de productos (tipo Google) + paginación opcional. */
	@JsonIgnoreProperties(ignoreUnknown = true) // por si el tool manda page/size extra
	public record ProductoRequest(String query) {
	}

	/** Item de producto. */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ProductoItem(String codigoProducto, String nombreProducto, Long stock, BigDecimal precio,
			BigDecimal precioRetail, String grupo, String disenio, String rin, String serie, String ancho,
			String medida, String familiaRin, String marca, String nomenclatura, String velocidad, String urlPagina) {
	}

	/** Respuesta paginada de productos mapeando el Page de Spring. */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ProductoResponse(@JsonProperty("content") List<ProductoItem> productos, // <- content → productos
			@JsonProperty("number") int page, // <- number → page
			@JsonProperty("size") int size, @JsonProperty("totalElements") long totalElements,
			@JsonProperty("totalPages") int totalPages, @JsonProperty("last") boolean last) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record CitaRequest(String ruc, String cliente, @JsonProperty("tipo_cita") String tipoCita, String fecha, // yyyy-MM-dd
			String hora, // HH:mm
			String celular, String sucursal) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record CitaResponse(String status, @JsonProperty("id_citas") Long idCita,
			@JsonProperty("cedula_ruc") String ruc, String fecha,
			String hora, @JsonProperty("motivo_servicio") String tipoCita) {
	}

	public record UpdateStateRequest(String nick, String conversationId, String initialMessage) {
	}

	public record UpdateStateResponse(String statusCode, String rawBody) {
	}

	public record OtpVerifyRequest(String cedulaRuc, String otpCode) {
	}

	public record ClienteProfile(String identification, String fullName, String email, String contacto) {
	}

	public record OtpServiceResponse(String statusCode, String rawBody) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ProductoSinPreciosItem(String codigoProducto, String nombreProducto, String grupo, String disenio,
			String rin, String serie, String ancho, String medida, String familiaRin, String marca, String nomenclatura,
			String velocidad, String urlPagina) {
	}

	/** Respuesta paginada sin precios/stock (Page de Spring). */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ProductoSinPreciosResponse(@JsonProperty("content") List<ProductoSinPreciosItem> productos,
			@JsonProperty("number") int page, @JsonProperty("size") int size,
			@JsonProperty("totalElements") long totalElements, @JsonProperty("totalPages") int totalPages,
			@JsonProperty("last") boolean last) {
	}

	public record TransferTiRequest(String cedulaRuc, String nick, String conversationId) {
	}

	/** Registro para información de Club Shell. */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ClubShellInfo(
			Integer puntosTotales, Integer puntosDisponibles, Integer puntosUsados) {
	}
}
