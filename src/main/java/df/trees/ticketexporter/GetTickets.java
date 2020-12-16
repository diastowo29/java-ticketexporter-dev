package df.trees.ticketexporter;

import java.util.ArrayList;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class GetTickets {

	SnapSnap snap = new SnapSnap();
	CSVWritter write = new CSVWritter();

	JSONArray commentsList = new JSONArray();
//	JSONArray ticketMetrics = new JSONArray();
	ArrayList<JSONObject> ticketMetricsList = new ArrayList<>();
	JSONArray ticketFieldsList = new JSONArray();
	Boolean commentSuccess = false;

	int ticketPageCounter = 0;

	public void doGetTicketFields(String ticketFieldsApi, String ticketApi, String zdDomain, String username,
			String password) {
		System.out.println("=== Getting all ticket fields ===");
		Unirest.get(ticketFieldsApi).basicAuth(username, password).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode ticketListObj = response.getBody();

			this.ticketFieldsList = ticketListObj.getObject().getJSONArray("ticket_fields");
			doGetTickets(ticketApi, zdDomain, username, password);

		}).ifFailure(response -> {
			System.out.println("=== GET TICKET FIELDS - FAIL ===");
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}

	public void doGetTickets(String ticketApi, String zdDomain, String username, String password) {
		ticketPageCounter++;
		System.out.print("Calling: " + ticketApi + " ");
		Unirest.get(ticketApi).basicAuth(username, password).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode ticketObj = response.getBody();

			JSONArray ticketList = ticketObj.getObject().getJSONArray("tickets");

			for (int i = 0; i < ticketList.length(); i++) {
				JSONObject ticket = ticketList.getJSONObject(i);
				String ticketId = ticket.get("id").toString();

				if (snap.INC_COMMENTS) {
					if (!ticketList.getJSONObject(i).getString("status").equals("deleted")) {
						if (!(ticketList.getJSONObject(i).get("id").toString().equals("326")
								|| ticketList.getJSONObject(i).get("id").toString().equals("539"))) {
							doGetTicketCommments(ticketId, zdDomain, username, password);
							if (commentSuccess) {
								ticketList.getJSONObject(i).put("comments", commentsList);
//								ticketList.getJSONObject(i).put("user_involved", usersList);
							}
						}
					} else {
						System.out.println("Ticket ID: " + ticketList.getJSONObject(i).get("id")
								+ " already deleted, skip comments");
					}
				}

				if (snap.INC_METRICS) {
					doGetTicketMetrics(ticketId, zdDomain, username, password);
				}
			}

			write.doWriteTicket(ticketList, ticketPageCounter, ticketFieldsList, zdDomain);

			if (snap.INC_METRICS) {
				write.doWriteTicketMetricsArrayList(ticketMetricsList, ticketPageCounter, zdDomain);
			}

			if (!snap.FIRST_PAGE_ONLY) {
				if (ticketObj.getObject().get("next_page") != null) {
					if (ticketObj.getObject().has("end_of_stream")) {
						if (!ticketObj.getObject().getBoolean("end_of_stream")) {
							doGetTickets(ticketObj.getObject().get("next_page").toString(), zdDomain, username,
									password);
						}
					} else {
						doGetTickets(ticketObj.getObject().get("next_page").toString(), zdDomain, username, password);
					}
				}
			}
		}).ifFailure(response -> {
			System.out.println("=== GET TICKET - FAIL ===");
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}

	public void doGetTicketMetrics(String ticketId, String zdDomain, String username, String password) {
		System.out.print("Getting ticket metrics ID: " + ticketId + " - ");

		Unirest.get(snap.ZD_TICKET_METRICS_BYID_API(ticketId, zdDomain)).basicAuth(username, password).asJson()
				.ifSuccess(metricsResponse -> {
					System.out.println(metricsResponse.getStatus());
					JsonNode ticketMetricsObj = metricsResponse.getBody();

					this.ticketMetricsList.add(ticketMetricsObj.getObject().getJSONObject("ticket_metric"));

				}).ifFailure(metricsResponse -> {
					System.out.print(metricsResponse.getStatus());
					System.out.println(metricsResponse.getStatusText());
					System.out.println("=== GET COMMENT - FAIL ===");
				});

	}

	public void doGetTicketCommments(String ticketId, String zdDomain, String username, String password) {

		System.out.print("Getting ticket comment ID: " + ticketId + " - ");

		commentsList = new JSONArray();
		commentSuccess = false;

		try {
			Unirest.get(snap.ZD_TICKET_COMMENT_API(ticketId, zdDomain)).basicAuth(username, password).asJson()
					.ifSuccess(commentResponse -> {
						System.out.println(commentResponse.getStatus());
						JsonNode ticketCommentObj = commentResponse.getBody();

						this.commentsList = ticketCommentObj.getObject().getJSONArray("comments");

						this.commentSuccess = true;

					}).ifFailure(commentResponse -> {
						System.out.print(commentResponse.getStatus());
						System.out.println(commentResponse.getStatusText());
						System.out.println("=== GET COMMENT - FAIL ===");
					});
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("retrying...");
			doGetTicketCommments(ticketId, zdDomain, username, password);
		}

	}
}
