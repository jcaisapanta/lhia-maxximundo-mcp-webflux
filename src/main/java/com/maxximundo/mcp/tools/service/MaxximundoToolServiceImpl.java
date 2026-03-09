package com.maxximundo.mcp.tools.service;

import java.util.List;
import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

@Service
@RequiredArgsConstructor
public class MaxximundoToolServiceImpl implements MaxximundoTool {
	private final WebClient lhiaWebClient;
	private final WebClient maxximundoWebClient;

	private static final String META_POINT_URL = "https://ia.maxximundo.com/gt-maxximundo/whatsapp/ms/state/updateStateConversation";

	private static final String ACCESS_KEY = "6a2cbaa5-45f7-462c-b8d7-153d4eb2ff08";

	private static final String CB_TECNI_CENTRO = "TWS2-b9f5dbae-afca-4d5e-8430-5fc65cd7c21c";
	private static final String CB_MARKETING = "TWS2-4f9a4dc7-d69a-4012-ad20-c2fd49f89b25";
	private static final String CB_CARTERA = "TWS2-f109c225-986e-4290-89ca-5963d106c941";
	private static final String CB_COBRANZAS = "TWS2-da44ced0-75a4-43fe-a720-d6eaf42f220b";
	private static final String CB_TI = "TWS2-e1974cb0-2717-402f-b5e8-4562362fe2ea";
	private static final String CB_VENTAS_AUTOLLANTA = "TWS2-4e15ed97-45d9-4843-a007-4aeb5027d483";
	private static final String CB_VENTAS_STOX = "TWS2-9d64f43d-1e82-4880-8d7e-9588ae6380f5";
	private static final String CB_VENTAS_IKONIX = "TWS2-c83e531c-7931-4cd1-b7d2-483919b66c26";
	private static final String CB_VENTAS_MAXXIMUNDO = "TWS2-21edc8d8-763f-4570-b0de-e101bb1be9ae";
	private static final String CB_CLUBSHELLMAXX = "TWS2-528bae72-491a-42dc-9921-5ab34cdb186e";
	// =========================================================
	// 🔐 CONFIGURACIÓN OTP (TODO EN ESTA CLASE)
	// =========================================================
	private static final String OTP_LOGIN_URL = "https://backend-acc-otp.tws2.io/otp-central/v1/login";

	private static final String OTP_BASIC_USER = "admin";
	private static final String OTP_BASIC_PASS = "f/0244T#PPW3"; // usa vault/env en prod

	// Código de cliente (siempre el mismo)
	private static final String CODIGO_CLIENTE = "rpMPDlV3nGhRRXkuPwINuw==";

	// Flags según Postman
	private static final int SEND_OTP = 0;
	private static final int SEND_EMAIL = 1;
	// --------- Helpers ---------

	private UpdateStateResponse postUpdateState(String callbackId, UpdateStateRequest req) {

		String initialMessage = (req != null && req.initialMessage() != null && !req.initialMessage().trim().isEmpty())
				? req.initialMessage().trim()
				: "Hola";

		Map<String, Object> body = Map.of("nick", req.nick(), "conversationId", req.conversationId(), "event",
				"THINKAGENT", "callbackId", callbackId, "initialMessage", initialMessage);

		HttpClient http = HttpClient.create().followRedirect(true);

		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(http))
				.defaultHeader("Content-Type", "application/json").defaultHeader("x-api-key", ACCESS_KEY).build();

		var response = client.post().uri(META_POINT_URL).bodyValue(body).exchangeToMono(
				res -> res.bodyToMono(String.class).defaultIfEmpty("").map(raw -> Map.entry(res.statusCode(), raw)))
				.block();

		if (response == null) {
			return new UpdateStateResponse("NO_RESPONSE", "");
		}

		HttpStatusCode status = response.getKey();
		String rawBody = response.getValue();

		System.out.println("MCP META-POINT => callbackId=" + callbackId + " STATUS=" + status + " BODY=" + rawBody);

		return new UpdateStateResponse(status.toString(), rawBody);
	}

	// --------- 5 Tools (uno por canal) ---------

	@Tool(description = "Realiza la conexión con el canal de atención servicio tecnico o tecni centro.")
	public UpdateStateResponse updateStateTecniCentro(UpdateStateRequest request) {
		return postUpdateState(CB_TECNI_CENTRO, request);
	}

	@Tool(description = "Realiza la conexión con el canal de atención MARKETING.")
	public UpdateStateResponse updateStateMarketing(UpdateStateRequest request) {
		return postUpdateState(CB_MARKETING, request);
	}

	@Tool(description = "Realiza la conexión con el canal de atención CARTERA.")
	public UpdateStateResponse updateStateCartera(UpdateStateRequest request) {
		return postUpdateState(CB_CARTERA, request);
	}

	@Tool(description = "Realiza la conexión con el canal de atención de tecnologia.")
	public UpdateStateResponse updateStateTI(UpdateStateRequest request) {
		return postUpdateState(CB_TI, request);
	}

	@Tool(description = "Realiza la conexión con el canal de atención VENTAS AUTOLLANTA.")
	public UpdateStateResponse updateStateVentasAutollanta(UpdateStateRequest request) {
		return postUpdateState(CB_VENTAS_AUTOLLANTA, request);
	}

	@Tool(description = "Realiza la conexión con el canal de atención VENTAS STOX.")
	public UpdateStateResponse updateStateVentasStox(UpdateStateRequest request) {
		return postUpdateState(CB_VENTAS_STOX, request);
	}

	@Tool(description = "Realiza la conexión con el canal de atención VENTAS IKONIX.")
	public UpdateStateResponse updateStateVentasIkonix(UpdateStateRequest request) {
		return postUpdateState(CB_VENTAS_IKONIX, request);
	}

	@Tool(description = "Realiza la conexión con el canal de atención VENTAS MAXXIMUNDO.")
	public UpdateStateResponse updateStateVentasMaxximundo(UpdateStateRequest request) {
		return postUpdateState(CB_VENTAS_MAXXIMUNDO, request);
	}

	@Tool(description = "Realiza la conexión con el canal de atención Club Shell Maxx para consultas de puntos de usuarios no verificados.")
	public UpdateStateResponse updateStateClubShellMaxx(UpdateStateRequest request) {
		return postUpdateState(CB_CLUBSHELLMAXX, request);
	}

	@Tool(description = "Obtiene toda la información de la cartera de un cliente a partir de su cédula o RUC.")
	public ClienteResponse getCarteraPorCedula(ClienteRequest request) {
		String cedulaRuc = request.cedulaRuc();
		System.out.println("MCP: Llamando al backend para cartera. cedulaRuc=" + cedulaRuc);

		// Se asume que el backend expone: GET /api/cartera/{cedula}
		List<ClienteItem> items = lhiaWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/cliente/cartera/{cedula}").build(cedulaRuc)).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<ClienteItem>>() {
				}).block(); // está bien bloquear aquí, el MCP es sync

		if (items == null || items.isEmpty()) {
			return new ClienteResponse(List.of());
		}

		// Limpiar el RUC: remover "C" al inicio y dejar solo números
		List<ClienteItem> itemsLimpios = items.stream()
				.map(item -> {
					String rucLimpio = item.ruc();
					if (rucLimpio != null && !rucLimpio.isBlank()) {
						// Remover "C" al inicio y cualquier carácter no numérico
						rucLimpio = rucLimpio.trim().toUpperCase();
						if (rucLimpio.startsWith("C")) {
							rucLimpio = rucLimpio.substring(1);
						}
						// Dejar solo números
						rucLimpio = rucLimpio.replaceAll("[^0-9]", "");
					}

					// Crear nuevo ClienteItem con RUC limpio
					return new ClienteItem(
							item.cliente(),
							rucLimpio,
							item.ciudad(),
							item.vendedor(),
							item.empresa(),
							item.correo(),
							item.telefono(),
							item.cupo(),
							item.totalCartera(),
							item.disponible(),
							item.carteraVencida(),
							item.comentario());
				})
				.toList();

		return new ClienteResponse(itemsLimpios);
	}

	@Tool(description = """
			Valida existencia del cliente por Cédula/RUC y transfiere al canal de TI según estado:
			- NO EXISTE: transfiere con teléfono=conversationId, nick y mensaje 'Usuario no existe' + cédula/RUC.
			- ACTUALIZAR DATOS: consulta cartera y transfiere con nombre, teléfono y correo obtenidos + nick y conversationId.
			- EXISTE: no transfiere.
			""")
	public UpdateStateResponse validarYTransferirTI(TransferTiRequest request) {
		String cedulaRuc = request.cedulaRuc();
		String nick = request.nick();
		String conversationId = request.conversationId();

		if (cedulaRuc == null || cedulaRuc.isBlank()) {
			return new UpdateStateResponse("400", "Cédula/RUC obligatoria");
		}
		if (nick == null || nick.isBlank()) {
			return new UpdateStateResponse("400", "nick es obligatorio");
		}
		if (conversationId == null || conversationId.isBlank()) {
			return new UpdateStateResponse("400", "conversationId es obligatorio");
		}

		System.out.println("MCP: validarYTransferirTI. cedulaRuc=" + cedulaRuc + " conv=" + conversationId);

		// 1) Consultar estado de existencia (EXISTE | NO EXISTE | ACTUALIZAR DATOS)
		String estado = lhiaWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/cliente/existe/{cedula}").build(cedulaRuc))
				.retrieve()
				.bodyToMono(String.class)
				.block();

		if (estado == null || estado.isBlank()) {
			// fallback seguro
			estado = "NO EXISTE";
		}
		estado = estado.trim().toUpperCase();

		// 2) NO EXISTE -> transferir con conversationId como teléfono
		if ("NO EXISTE".equals(estado)) {
			String initialMessage = String.format("""
					Teléfono: %s
					Nick: %s
					Cédula/RUC: %s
					Motivo: Usuario no existe
					""", conversationId, nick, cedulaRuc);

			return updateStateTI(new UpdateStateRequest(nick, conversationId, initialMessage));
		}

		// 3) ACTUALIZAR DATOS -> consultar cartera y transferir con info de cartera
		if ("ACTUALIZAR DATOS".equals(estado)) {
			List<ClienteItem> items = lhiaWebClient.get()
					.uri(uriBuilder -> uriBuilder.path("/cliente/cartera/{cedula}").build(cedulaRuc))
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<ClienteItem>>() {
					})
					.block();

			if (items == null || items.isEmpty()) {
				// Si por alguna razón "ACTUALIZAR DATOS" pero cartera no devuelve, transferimos
				// igual con mínimos
				String initialMessage = String.format("""
						Teléfono: %s
						Nick: %s
						Cédula/RUC: %s
						Motivo: Actualización de datos (sin registro de cartera)
						""", conversationId, nick, cedulaRuc);

				return updateStateTI(new UpdateStateRequest(nick, conversationId, initialMessage));
			}

			ClienteItem item = items.get(0);

			String telefonoCartera = (item.telefono() != null && !item.telefono().isBlank())
					? item.telefono().trim()
					: "Sin teléfono";

			String nombreCartera = (item.cliente() != null && !item.cliente().isBlank())
					? item.cliente().trim()
					: "Sin nombre";

			String correoCartera = (item.correo() != null && !item.correo().isBlank())
					? item.correo().trim()
					: "Sin correo";

			String initialMessage = String.format("""
					Teléfono: %s
					Nick: %s
					Cédula/RUC: %s
					Nombre : %s
					Correo : %s
					Motivo: Actualización de datos
					""", telefonoCartera, nick, cedulaRuc, nombreCartera, correoCartera);

			return updateStateTI(new UpdateStateRequest(nick, conversationId, initialMessage));
		}

		// 4) EXISTE -> no transferir (respuesta normal)
		return new UpdateStateResponse("200", "EXISTE");
	}

	// ================== PRODUCTOS ==================

	@Tool(description = "Busca productos por su nombre")
	public ProductoResponse searchProductos(ProductoRequest request) {
		String q = request != null && request.query() != null ? request.query().trim() : "";

		System.out.println("MCP: Llamando al backend para productos. q=" + q);

		// Primero ver el JSON crudo
		JsonNode raw = lhiaWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/producto/productos/search").queryParam("query", q).build())
				.retrieve().bodyToMono(JsonNode.class).block();

		System.out.println("MCP: respuesta cruda backend = " + raw);

		// Luego mapearlo a tu DTO
		ObjectMapper mapper = new ObjectMapper();
		ProductoResponse response = mapper.convertValue(raw, ProductoResponse.class);

		System.out.println("MCP: mapeado a ProductoResponse = " + response);

		return response;
	}

	@Tool(description = "Crea una cita en la agenda a partir del RUC, nombre, tipo de cita, sucursal, celular, fecha y hora.")
	public CitaResponse crearCita(CitaRequest request) {

		System.out.println("MCP: Llamando al backend para crear cita. ruc=" + request.ruc());

		// Llamada al nuevo backend en back-lhia-maxximundo: POST /citas
		CitaResponse response = maxximundoWebClient.post()
				.uri("/citas")
				.bodyValue(request)
				.retrieve()
				.bodyToMono(CitaResponse.class)
				.block();

		if (response == null) {
			return new CitaResponse("ERROR", null, request.ruc(), request.fecha(), request.hora(), request.tipoCita());
		}

		return response;
	}

	private String obtenerTokenOtpCentral() {

		HttpClient http = HttpClient.create().followRedirect(true);

		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(http)).build();

		Map<String, Object> resp = client.post().uri(OTP_LOGIN_URL)
				.headers(h -> h.setBasicAuth(OTP_BASIC_USER, OTP_BASIC_PASS)).retrieve().bodyToMono(Map.class).block();
		System.out.println("OTP access_token = " + resp.get("access_token"));

		if (resp == null || !resp.containsKey("access_token")) {
			throw new IllegalStateException("No se pudo obtener access_token del servicio OTP");
		}

		return String.valueOf(resp.get("access_token"));
	}

	// =========================================================
	// 🌐 POST OTP CON BEARER
	// =========================================================
	private OtpServiceResponse postOtp(String fullUrl, String token, Object body) {

		HttpClient http = HttpClient.create().followRedirect(true);

		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(http))
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();

		// 🔍 DEBUG tipo curl -v
		System.out.println("OTP REQUEST URL => " + fullUrl);
		System.out.println("OTP REQUEST BODY => " + body);

		var response = client.post().uri(fullUrl) // 👈 URL COMPLETA
				.bodyValue(body).exchangeToMono(res -> res.bodyToMono(String.class).defaultIfEmpty("")
						.map(raw -> Map.entry(res.statusCode(), raw)))
				.block();

		if (response == null) {
			return new OtpServiceResponse("NO_RESPONSE", "");
		}

		HttpStatusCode status = response.getKey();
		String raw = response.getValue();

		System.out.println("OTP RESPONSE STATUS => " + status);
		System.out.println("OTP RESPONSE BODY => " + raw);

		return new OtpServiceResponse(status.toString(), raw);
	}

	private ClienteProfile obtenerPerfilCliente(String cedulaRuc) {
		if ("1726844689".equals(cedulaRuc)) {
			return new ClienteProfile("1726844689", "Juan Andres Caisapanta", "jcaisapanta@tws2.io", "0999288791");
		}
		// Llamada real al backend de cartera
		List<ClienteItem> items = lhiaWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/cliente/cartera/{cedula}").build(cedulaRuc)).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<ClienteItem>>() {
				}).block();

		if (items == null || items.isEmpty()) {
			return null;
		}

		ClienteItem item = items.get(0);

		// Validaciones mínimas
		if (item.ruc() == null || item.cliente() == null) {
			return null;
		}

		// Limpiar el RUC: remover "C" al inicio y dejar solo números
		String rucLimpio = item.ruc().trim().toUpperCase();
		if (rucLimpio.startsWith("C")) {
			rucLimpio = rucLimpio.substring(1);
		}
		rucLimpio = rucLimpio.replaceAll("[^0-9]", "");

		return new ClienteProfile(rucLimpio, // identification (RUC limpio)
				item.cliente(), // fullName
				item.correo(), // email
				item.telefono() // contacto
		);
	}

	// =========================================================
	// 🧩 TOOL 1 – ENVIAR OTP
	// =========================================================
	@Tool(description = "Realiza la conexión con el servicio de OTP y envía un código OTP al usuario usando su cédula o RUC.")
	public OtpServiceResponse enviarOtpPorCedula(ClienteRequest request) {

		String cedulaRuc = request.cedulaRuc();
		if (cedulaRuc == null || cedulaRuc.isBlank()) {
			return new OtpServiceResponse("400", "cedulaRuc es obligatorio");
		}

		// 1️⃣ Obtener datos desde base interna
		ClienteProfile profile = obtenerPerfilCliente(cedulaRuc.trim());

		if (profile == null) {
			return new OtpServiceResponse("404", "Cliente no encontrado en base interna");
		}

		// 2️⃣ Token OTP
		String token = obtenerTokenOtpCentral();
		System.out.println("OTP identification=" + profile.identification() + " fullName=" + profile.fullName()
				+ " email=" + profile.email() + " contacto=" + profile.contacto());
		// 3️⃣ Payload EXACTO según Postman
		ObjectMapper om = new ObjectMapper();
		ObjectNode payload = om.createObjectNode();
		payload.put("identification", profile.identification());
		payload.put("fullName", profile.fullName());
		payload.put("email", profile.email());
		payload.put("codigoCliente", CODIGO_CLIENTE);
		payload.put("contacto", profile.contacto());
		payload.put("sendOTP", SEND_OTP);
		payload.put("sendEmail", SEND_EMAIL);

		return postOtp("https://backend-acc-otp.tws2.io/otp-central/api/otp/request", token, payload);
	}

	// =========================================================
	// 🧩 TOOL 2 – VERIFICAR OTP
	// =========================================================
	@Tool(description = "Realiza la conexión con el servicio de OTP y verifica el código OTP enviado al usuario.")
	public OtpServiceResponse verificarOtpPorCedula(OtpVerifyRequest request) {

		if (request.cedulaRuc() == null || request.cedulaRuc().isBlank()) {
			return new OtpServiceResponse("400", "cedulaRuc es obligatorio");
		}
		if (request.otpCode() == null || request.otpCode().isBlank()) {
			return new OtpServiceResponse("400", "otpCode es obligatorio");
		}

		String token = obtenerTokenOtpCentral();

		Map<String, Object> payload = Map.of("identification", request.cedulaRuc().trim(), "otpCode",
				request.otpCode().trim());

		return postOtp("https://backend-acc-otp.tws2.io/otp-central/api/otp/verify", token, payload);
	}

	@Tool(description = "Busca productos por su nombre (sin devolver stock, precio ni precioRetail).")
	public ProductoSinPreciosResponse searchProductosSinPrecio(ProductoRequest request) {
		String q = request != null && request.query() != null ? request.query().trim() : "";

		System.out.println("MCP: Llamando al backend para productos SIN PRECIO. q=" + q);

		JsonNode raw = lhiaWebClient.get().uri(
				uriBuilder -> uriBuilder.path("/producto/productos/searchsinprecio").queryParam("query", q).build())
				.retrieve().bodyToMono(JsonNode.class).block();

		System.out.println("MCP: respuesta cruda backend (sin precio) = " + raw);

		ObjectMapper mapper = new ObjectMapper();
		ProductoSinPreciosResponse response = mapper.convertValue(raw, ProductoSinPreciosResponse.class);

		System.out.println("MCP: mapeado a ProductoSinPreciosResponse = " + response);

		return response;
	}

	@Tool(description = "Obtiene información del Club Shell por cédula.")
	public ClubShellInfo getClubShellInfo(ClienteRequest request) {
		String cedula = request.cedulaRuc();
		System.out.println("MCP: Llamando al backend para Club Shell. cedula=" + cedula);

		return lhiaWebClient.get()
				.uri(uriBuilder -> uriBuilder.path("/club-shell/{cedula}").build(cedula))
				.retrieve()
				.bodyToMono(ClubShellInfo.class)
				.block();
	}

}
