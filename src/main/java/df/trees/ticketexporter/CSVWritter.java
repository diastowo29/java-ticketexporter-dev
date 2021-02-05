package df.trees.ticketexporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.vdurmont.emoji.EmojiParser;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;

public class CSVWritter {

	SnapSnap snap = new SnapSnap();

	String folderPath = "C:\\Users\\USER\\Documents\\Eclipse Output";

	public void doWriteTicket(JSONArray ticketList, int ticketPageCounter, JSONArray customFieldsList,
			String zdDomain) {
		try {
			System.out.println("Write tickets..");
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}
			File file = new File(path + "\\JAVA_JENDEK\\" + zdDomain + "_ticketList_" + ticketPageCounter + ".csv");
			File cFile = new File(path + "\\JAVA_JENDEK\\" + zdDomain + "_commentsList_" + ticketPageCounter + ".csv");

			if (!file.exists()) {
				file.createNewFile();
			}

			if (!cFile.exists()) {
				cFile.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter ticketBw = new BufferedWriter(fw);

			FileWriter cfw = new FileWriter(cFile.getAbsoluteFile());
			BufferedWriter cbw = new BufferedWriter(cfw);

			for (int i = 0; i < ticketList.length(); i++) {
				if (i == 0) {
					for (int j = 0; j < ticketList.getJSONObject(i).names().length(); j++) {
						if (!ticketList.getJSONObject(i).names().get(j).toString().equals("fields")
								&& !ticketList.getJSONObject(i).names().get(j).toString().equals("custom_fields")
								&& !ticketList.getJSONObject(i).names().get(j).toString().equals("comments")
								&& !ticketList.getJSONObject(i).names().get(j).toString()
										.equals("deleted_ticket_form_id")) {
							ticketBw.write("\"" + ticketList.getJSONObject(i).names().get(j).toString() + "\"" + "|");
						}
					}
					for (int c = 0; c < customFieldsList.length(); c++) {
						if (customFieldsList.getJSONObject(c).getBoolean("removable")) {
//							if (!(customFieldsList.getJSONObject(c).get("title").toString().equals("Type"))
//									&& !(customFieldsList.getJSONObject(c).get("title").toString()
//											.equals("Priority"))) {
							ticketBw.write(customFieldsList.getJSONObject(c).get("title").toString() + "|");
//							}
						}
					}
					ticketBw.newLine();
					if (snap.INC_COMMENTS) {
						cbw.write("ticket_id|author_id|created_at|body");
						cbw.newLine();
					}
				}

				for (int j = 0; j < ticketList.getJSONObject(i).names().length(); j++) {
					if (!(ticketList.getJSONObject(i).get("id").toString().equals("326")
							|| ticketList.getJSONObject(i).get("id").toString().equals("539"))) {
						if (!ticketList.getJSONObject(i).names().get(j).toString().equals("fields")
								&& !ticketList.getJSONObject(i).names().get(j).toString().equals("custom_fields")
								&& !ticketList.getJSONObject(i).names().get(j).toString().equals("comments")
								&& !ticketList.getJSONObject(i).names().get(j).toString()
										.equals("deleted_ticket_form_id")) {

							try {
								if (ticketList.getJSONObject(i).names().get(j).toString().equals("description")
										|| ticketList.getJSONObject(i).names().get(j).toString().equals("subject")
										|| ticketList.getJSONObject(i).names().get(j).toString().equals("raw_subject")
										|| ticketList.getJSONObject(i).names().get(j).toString().equals("via")) {
									ticketBw.write(EmojiParser.parseToAliases("\"" + ticketList.getJSONObject(i)
											.get(ticketList.getJSONObject(i).names().get(j).toString()).toString()
											.replaceAll("[\\t\\n\\r]+", " ").replace(" |", " ").replace("| ", " ")
											.replace("|", " ").replace("\"", "'").replace("\n", " ") + "\" |"));
									// ticketBw.write("\"desc, subject, via NULL\" | ");
								} else {
									ticketBw.write(EmojiParser.parseToAliases("\"" + ticketList.getJSONObject(i)
											.get(ticketList.getJSONObject(i).names().get(j).toString()).toString()
											+ "\"|"));

								}
							} catch (NullPointerException e) {
//								e.printStackTrace();
//								System.out.println(ticketList.getJSONObject(i).names().get(j).toString() + " is NULL");
								ticketBw.write(
										"\"" + ticketList.getJSONObject(i).names().get(j).toString() + " null\"|");
							}
						}
					}
				}
//				
				if (!(ticketList.getJSONObject(i).get("id").toString().equals("326")
						|| ticketList.getJSONObject(i).get("id").toString().equals("539"))) {
					for (int c = 0; c < customFieldsList.length(); c++) {
						if (customFieldsList.getJSONObject(c).getBoolean("removable")) {
							boolean fieldFound = false;
							for (int j = 0; j < ticketList.getJSONObject(i).getJSONArray("custom_fields")
									.length(); j++) {
								JSONObject ticketCustFields = ticketList.getJSONObject(i).getJSONArray("custom_fields")
										.getJSONObject(j);
								if (customFieldsList.getJSONObject(c).get("id").toString()
										.equals(ticketCustFields.get("id").toString())) {
									fieldFound = true;
									try {
										ticketBw.write("\"" + ticketCustFields.get("value").toString() + "\"|");
									} catch (NullPointerException e) {
//										e.printStackTrace();
										ticketBw.write("\"null\"|");
									}
								}
							}
							if (!fieldFound) {
								ticketBw.write("\"null\"|");
							}
						}
					}
				}

//				for (int c = 0; c < customFieldsList.length(); c++) {
//					if (!(ticketList.getJSONObject(i).get("id").toString().equals("326")
//							|| ticketList.getJSONObject(i).get("id").toString().equals("539"))) {
//						if (customFieldsList.getJSONObject(c).getBoolean("removable")) {
//							for (int j = 0; j < ticketList.getJSONObject(i).getJSONArray("custom_fields")
//									.length(); j++) {
//								if (customFieldsList.getJSONObject(c).get("id").toString()
//										.equals(ticketList.getJSONObject(i).getJSONArray("custom_fields")
//												.getJSONObject(j).get("id").toString())) {
//									try {
//										ticketBw.write("\"" + ticketList.getJSONObject(i).getJSONArray("custom_fields")
//												.getJSONObject(j).get("value").toString() + "\"|");
//									} catch (NullPointerException e) {
//										ticketBw.write("\"null\"|");
//									}
//								}
//							}
//						}
//					}
//				}
				ticketBw.newLine();

				boolean gotAgentReply = false;
				// String requester =
				// ticketList.getJSONObject(i).get("requester_id").toString();
				if (snap.INC_COMMENTS) {
					if (ticketList.getJSONObject(i).has("comments")) {
						for (int j = ticketList.getJSONObject(i).getJSONArray("comments").length() - 1; j > -1; j--) {
							if (!gotAgentReply) {
								if (ticketList.getJSONObject(i).getJSONArray("comments").getJSONObject(j)
										.getString("body").contains("#StatusSolved")
										|| ticketList.getJSONObject(i).getJSONArray("comments").getJSONObject(j)
												.getString("body").contains("#StatusPending")) {
									try {
										cbw.write(ticketList.getJSONObject(i).get("id").toString() + "|"
												+ ticketList.getJSONObject(i).getJSONArray("comments").getJSONObject(j)
														.get("author_id").toString()
												+ "|"
												+ ticketList.getJSONObject(i).getJSONArray("comments").getJSONObject(j)
														.get("created_at").toString()
												+ "|\""
												+ ticketList.getJSONObject(i).getJSONArray("comments").getJSONObject(j)
														.get("body").toString().replace("\"", "\'")
														.replaceAll("[\\t\\n\\r]+", " ").replace(" |", " ")
														.replace("| ", " ").replace("|", " ").replace("\"", "'")
														.replace("\n", " ")
												+ "\"");
									} catch (NullPointerException e) {
										cbw.write("\"null\" | \"null\" | \"null\" | \"null\"");
									}
									cbw.newLine();
									gotAgentReply = true;
								}
							}
						}
					}
				}
			}
			ticketBw.close();
			cbw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void doWriteUsers(JSONArray usersList, int ticketPageCounter, String zdDomain) {
		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}

			File file = new File(path + "\\JAVA_JENDEK\\" + zdDomain + "_UsersList_part_" + ticketPageCounter + ".csv");

			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			// System.out.println(file);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < usersList.length(); i++) {
				if (i == 0) {
					for (int j = 0; j < usersList.getJSONObject(i).names().length(); j++) {
						/* Exclude Photo */
						if (!usersList.getJSONObject(i).names().get(j).toString().equals("photo")
								&& !usersList.getJSONObject(i).names().get(j).toString().equals("user_fields")) {
							bw.write(usersList.getJSONObject(i).names().get(j).toString() + "|");
						}

						if (usersList.getJSONObject(i).names().get(j).toString().equals("user_fields")) {
							if (usersList.getJSONObject(i).getJSONObject("user_fields").names() != null) {
								if (usersList.getJSONObject(i).getJSONObject("user_fields").names().length() > 0) {
									for (int k = 0; k < usersList.getJSONObject(i).getJSONObject("user_fields").names()
											.length(); k++) {
										bw.write(usersList.getJSONObject(i).getJSONObject("user_fields").names().get(k)
												.toString() + "|");
									}
								}
							}
						}
					}
					// bw.write("facebook/twitter");
					bw.newLine();
				}
				for (int j = 0; j < usersList.getJSONObject(i).names().length(); j++) {

					/* EXCLUDE PHOTO */
					if (!usersList.getJSONObject(i).names().get(j).toString().equals("photo")
							&& !usersList.getJSONObject(i).names().get(j).toString().equals("user_fields")) {
						try {
							bw.write(
									usersList.getJSONObject(i).get(usersList.getJSONObject(i).names().get(j).toString())
											.toString().replace(" |", " ").replace("| ", " ").replace("|", " ") + "|");
						} catch (NullPointerException e) {

							bw.write("\"null\"|");
						}
					}
					if (usersList.getJSONObject(i).names().get(j).equals("user_fields")) {
						if (usersList.getJSONObject(i).getJSONObject("user_fields").names() != null) {
							for (int k = 0; k < usersList.getJSONObject(i).getJSONObject("user_fields").names()
									.length(); k++) {
								try {
									bw.write(
											usersList.getJSONObject(i).getJSONObject("user_fields")
													.get(usersList.getJSONObject(i).getJSONObject("user_fields").names()
															.getString(k))
													.toString().replaceAll("[\\t\\n\\r]+", " ") + "|");
								} catch (NullPointerException e) {
									bw.write("null | ");
								}
							}
						}
					}
				}

				bw.newLine();
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doWriteTicketFields(kong.unirest.json.JSONObject ticketFields, String ticketFieldName,
			String zdDomain) {
		System.out.println("Write ticket fields: " + ticketFieldName);
		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}
			File file = new File(path + "\\JAVA_JENDEK\\" + zdDomain + "_ticket-field_"
					+ ticketFieldName.replace("/", "_") + ".csv");

			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < ticketFields.getJSONArray("custom_field_options").length(); i++) {
				if (i == 0) {
					bw.write("value,tag,default");
					bw.newLine();
				}
				bw.write("\"" + ticketFields.getJSONArray("custom_field_options").getJSONObject(i).getString("name")
						+ "\"");
				bw.write(",");
				bw.write("\"" + ticketFields.getJSONArray("custom_field_options").getJSONObject(i).getString("value")
						+ "\"");
				bw.write(",");
				bw.write(ticketFields.getJSONArray("custom_field_options").getJSONObject(i).get("default").toString());
				bw.newLine();
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void doWriteBrands(JSONArray brandsList, int brandPageCounter, String zdDomain) {

		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}
			File file = new File(path + "\\JAVA_JENDEK\\" + zdDomain + "_brands_" + brandPageCounter + ".csv");

			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < brandsList.length(); i++) {
				if (i == 0) {
					bw.write("id | name | subdomain");
					bw.newLine();
				}
				bw.write(brandsList.getJSONObject(i).get("id").toString() + " | "
						+ brandsList.getJSONObject(i).get("name").toString() + " | "
						+ brandsList.getJSONObject(i).get("subdomain").toString());
				bw.newLine();

			}
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void doWriteOrganizations(JSONArray organizationsList, int organizationPageCounter, String zdDomain) {

		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}
			File file = new File(
					path + "\\JAVA_JENDEK\\" + zdDomain + "_organizations_" + organizationPageCounter + ".csv");

			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < organizationsList.length(); i++) {
				if (i == 0) {
					bw.write("id | name | domain_names");
					bw.newLine();
				}
				bw.write("\"" + organizationsList.getJSONObject(i).get("id").toString() + "\"" + " | " + "\""
						+ organizationsList.getJSONObject(i).get("name").toString() + "\"" + " | " + "\""
						+ organizationsList.getJSONObject(i).get("domain_names") + "\"");
				bw.newLine();

			}
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doWriteTicketMetrics(JSONArray ticketMetrics, int metricsPageCounter, String zdDomain) {

		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}
			File file = new File(
					path + "\\JAVA_JENDEK\\" + zdDomain + "_ticket-metrics_" + metricsPageCounter + ".csv");

			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < ticketMetrics.length(); i++) {
				if (i == 0) {
					for (int j = 0; j < ticketMetrics.getJSONObject(i).names().length(); j++) {
						if (ticketMetrics.getJSONObject(i).names().get(j).equals("reply_time_in_minutes")) {
							bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.getJSONObject(i).names().get(j)
								.equals("first_resolution_time_in_minutes")) {
							bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.getJSONObject(i).names().get(j)
								.equals("full_resolution_time_in_minutes")) {
							bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.getJSONObject(i).names().get(j).equals("agent_wait_time_in_minutes")) {
							bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.getJSONObject(i).names().get(j)
								.equals("requester_wait_time_in_minutes")) {
							bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.getJSONObject(i).names().get(j).equals("on_hold_time_in_minutes")) {
							bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
						} else {
							bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() + "|");
						}
					}
					bw.newLine();
					doGenerateRowMetric(ticketMetrics, i, bw);
					bw.newLine();
				} else {
					doGenerateRowMetric(ticketMetrics, i, bw);
					bw.newLine();
				}
			}
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doWriteTicketMetricsArrayList(ArrayList<JSONObject> ticketMetrics, int metricsPageCounter,
			String zdDomain) {

		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}
			File file = new File(
					path + "\\JAVA_JENDEK\\" + zdDomain + "_ticket-metrics_" + metricsPageCounter + ".csv");

			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < ticketMetrics.size(); i++) {
				if (i == 0) {
					for (int j = 0; j < ticketMetrics.get(i).names().length(); j++) {
						if (ticketMetrics.get(i).names().get(j).equals("reply_time_in_minutes")) {
							bw.write(ticketMetrics.get(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.get(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.get(i).names().get(j).equals("first_resolution_time_in_minutes")) {
							bw.write(ticketMetrics.get(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.get(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.get(i).names().get(j).equals("full_resolution_time_in_minutes")) {
							bw.write(ticketMetrics.get(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.get(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.get(i).names().get(j).equals("agent_wait_time_in_minutes")) {
							bw.write(ticketMetrics.get(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.get(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.get(i).names().get(j).equals("requester_wait_time_in_minutes")) {
							bw.write(ticketMetrics.get(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.get(i).names().get(j).toString() + ".business|");
						} else if (ticketMetrics.get(i).names().get(j).equals("on_hold_time_in_minutes")) {
							bw.write(ticketMetrics.get(i).names().get(j).toString() + ".calendar| "
									+ ticketMetrics.get(i).names().get(j).toString() + ".business|");
						} else {
							bw.write(ticketMetrics.get(i).names().get(j).toString() + "|");
						}
					}
					bw.newLine();
					doGenerateRowMetricArrayList(ticketMetrics, i, bw);
					bw.newLine();
				} else {
					doGenerateRowMetricArrayList(ticketMetrics, i, bw);
					bw.newLine();
				}
			}
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public void doWriteTicketMetricsById(JSONObject ticketMetrics, String
	// ticketId, String zdDomain) {
	//
	// try {
	// File f = new File(System.getProperty("java.class.path"));
	// File dir = f.getAbsoluteFile().getParentFile();
	// String path = "";
	// if (snap.JAR_RUN) {
	// path = dir.toString();
	// } else {
	// path = folderPath;
	// }
	// File file = new File(
	// path + "\\JAVA_JENDEK\\" + zdDomain + "_ticket-metrics_" + metricsPageCounter
	// + ".csv");
	//
	// Date currentDate = new Date();
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(currentDate);
	// if (!file.exists()) {
	// file.createNewFile();
	// }
	//
	// FileWriter fw = new FileWriter(file.getAbsoluteFile());
	// BufferedWriter bw = new BufferedWriter(fw);
	// for (int i = 0; i < ticketMetrics.length(); i++) {
	// if (i == 0) {
	// for (int j = 0; j < ticketMetrics.getJSONObject(i).names().length(); j++) {
	// if
	// (ticketMetrics.getJSONObject(i).names().get(j).equals("reply_time_in_minutes"))
	// {
	// bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() +
	// ".calendar| "
	// + ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
	// } else if (ticketMetrics.getJSONObject(i).names().get(j)
	// .equals("first_resolution_time_in_minutes")) {
	// bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() +
	// ".calendar| "
	// + ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
	// } else if (ticketMetrics.getJSONObject(i).names().get(j)
	// .equals("full_resolution_time_in_minutes")) {
	// bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() +
	// ".calendar| "
	// + ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
	// } else if
	// (ticketMetrics.getJSONObject(i).names().get(j).equals("agent_wait_time_in_minutes"))
	// {
	// bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() +
	// ".calendar| "
	// + ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
	// } else if (ticketMetrics.getJSONObject(i).names().get(j)
	// .equals("requester_wait_time_in_minutes")) {
	// bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() +
	// ".calendar| "
	// + ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
	// } else if
	// (ticketMetrics.getJSONObject(i).names().get(j).equals("on_hold_time_in_minutes"))
	// {
	// bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() +
	// ".calendar| "
	// + ticketMetrics.getJSONObject(i).names().get(j).toString() + ".business|");
	// } else {
	// bw.write(ticketMetrics.getJSONObject(i).names().get(j).toString() + "|");
	// }
	// }
	// bw.newLine();
	// doGenerateRowMetric(ticketMetrics, i, bw);
	// bw.newLine();
	// } else {
	// doGenerateRowMetric(ticketMetrics, i, bw);
	// bw.newLine();
	// }
	// }
	// bw.close();
	//
	// System.out.println("Done");
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	private void doGenerateRowMetric(JSONArray ticketMetrics, int i, BufferedWriter bw)
			throws JSONException, IOException {
		for (int j = 0; j < ticketMetrics.getJSONObject(i).names().length(); j++) {
			if (ticketMetrics.getJSONObject(i).names().get(j).equals("reply_time_in_minutes")) {
				bw.write(ticketMetrics.getJSONObject(i).getJSONObject("reply_time_in_minutes").get("calendar") + "|"
						+ ticketMetrics.getJSONObject(i).getJSONObject("reply_time_in_minutes").get("business") + "|");
			} else if (ticketMetrics.getJSONObject(i).names().get(j).equals("first_resolution_time_in_minutes")) {
				bw.write(
						ticketMetrics.getJSONObject(i).getJSONObject("first_resolution_time_in_minutes").get("calendar")
								+ "|" + ticketMetrics.getJSONObject(i).getJSONObject("first_resolution_time_in_minutes")
										.get("business")
								+ "|");
			} else if (ticketMetrics.getJSONObject(i).names().get(j).equals("full_resolution_time_in_minutes")) {
				bw.write(ticketMetrics.getJSONObject(i).getJSONObject("full_resolution_time_in_minutes").get("calendar")
						+ "|" + ticketMetrics.getJSONObject(i).getJSONObject("full_resolution_time_in_minutes")
								.get("business")
						+ "|");
			} else if (ticketMetrics.getJSONObject(i).names().get(j).equals("agent_wait_time_in_minutes")) {
				bw.write(ticketMetrics.getJSONObject(i).getJSONObject("agent_wait_time_in_minutes").get("calendar")
						+ "|"
						+ ticketMetrics.getJSONObject(i).getJSONObject("agent_wait_time_in_minutes").get("business")
						+ "|");
			} else if (ticketMetrics.getJSONObject(i).names().get(j).equals("requester_wait_time_in_minutes")) {
				bw.write(ticketMetrics.getJSONObject(i).getJSONObject("requester_wait_time_in_minutes").get("calendar")
						+ "|"
						+ ticketMetrics.getJSONObject(i).getJSONObject("requester_wait_time_in_minutes").get("business")
						+ "|");
			} else if (ticketMetrics.getJSONObject(i).names().get(j).equals("on_hold_time_in_minutes")) {
				bw.write(ticketMetrics.getJSONObject(i).getJSONObject("on_hold_time_in_minutes").get("calendar") + "|"
						+ ticketMetrics.getJSONObject(i).getJSONObject("on_hold_time_in_minutes").get("business")
						+ "|");
			} else {
				// System.out.println(ticketMetrics.getJSONObject(i).names().get(j).toString());
				try {
					bw.write(ticketMetrics.getJSONObject(i)
							.get(ticketMetrics.getJSONObject(i).names().get(j).toString()).toString() + "|");
				} catch (NullPointerException e) {
					bw.write("\"null\"|");
				}
			}
		}
	}

	private void doGenerateRowMetricArrayList(ArrayList<JSONObject> ticketMetrics, int i, BufferedWriter bw)
			throws JSONException, IOException {
		for (int j = 0; j < ticketMetrics.get(i).names().length(); j++) {
			if (ticketMetrics.get(i).names().get(j).equals("reply_time_in_minutes")) {
				bw.write(ticketMetrics.get(i).getJSONObject("reply_time_in_minutes").get("calendar") + "|"
						+ ticketMetrics.get(i).getJSONObject("reply_time_in_minutes").get("business") + "|");
			} else if (ticketMetrics.get(i).names().get(j).equals("first_resolution_time_in_minutes")) {
				bw.write(ticketMetrics.get(i).getJSONObject("first_resolution_time_in_minutes").get("calendar") + "|"
						+ ticketMetrics.get(i).getJSONObject("first_resolution_time_in_minutes").get("business") + "|");
			} else if (ticketMetrics.get(i).names().get(j).equals("full_resolution_time_in_minutes")) {
				bw.write(ticketMetrics.get(i).getJSONObject("full_resolution_time_in_minutes").get("calendar") + "|"
						+ ticketMetrics.get(i).getJSONObject("full_resolution_time_in_minutes").get("business") + "|");
			} else if (ticketMetrics.get(i).names().get(j).equals("agent_wait_time_in_minutes")) {
				bw.write(ticketMetrics.get(i).getJSONObject("agent_wait_time_in_minutes").get("calendar") + "|"
						+ ticketMetrics.get(i).getJSONObject("agent_wait_time_in_minutes").get("business") + "|");
			} else if (ticketMetrics.get(i).names().get(j).equals("requester_wait_time_in_minutes")) {
				bw.write(ticketMetrics.get(i).getJSONObject("requester_wait_time_in_minutes").get("calendar") + "|"
						+ ticketMetrics.get(i).getJSONObject("requester_wait_time_in_minutes").get("business") + "|");
			} else if (ticketMetrics.get(i).names().get(j).equals("on_hold_time_in_minutes")) {
				bw.write(ticketMetrics.get(i).getJSONObject("on_hold_time_in_minutes").get("calendar") + "|"
						+ ticketMetrics.get(i).getJSONObject("on_hold_time_in_minutes").get("business") + "|");
			} else {
				// System.out.println(ticketMetrics.get(i).names().get(j).toString());
				try {
					bw.write(ticketMetrics.get(i).get(ticketMetrics.get(i).names().get(j).toString()).toString() + "|");
				} catch (NullPointerException e) {
					bw.write("\"null\"|");
				}
			}
		}
	}

	public void doWriteTicketMetricEvents(JSONArray metricsList, int metricsPageCounter, String zdDomain) {
		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}
			File file = new File(
					path + "\\JAVA_JENDEK\\" + zdDomain + "_ticket-metrics-event_" + metricsPageCounter + ".csv");

			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < metricsList.length(); i++) {
				if (i == 0) {
					bw.write("ticket_id | type | metric | time");
					bw.newLine();
				}
				try {

					bw.write(metricsList.getJSONObject(i).get("ticket_id").toString() + " | "
							+ metricsList.getJSONObject(i).get("type").toString() + " | "
							+ metricsList.getJSONObject(i).get("metric").toString() + " | "
							+ metricsList.getJSONObject(i).get("time").toString());
				} catch (NullPointerException e) {
					bw.write("\"null\" | " + metricsList.getJSONObject(i).get("type").toString() + " | "
							+ metricsList.getJSONObject(i).get("metric").toString() + " | "
							+ metricsList.getJSONObject(i).get("time").toString());
				}
				bw.newLine();
			}
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doWriteSchedule(JSONArray scheduleList, String zdDomain, String alias) {
		try {
			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			String path = "";
			if (snap.JAR_RUN) {
				path = dir.toString();
			} else {
				path = folderPath;
			}
			File file = new File(path + "\\JAVA_JENDEK\\" + zdDomain + "_" + alias + ".csv");

			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			JSONArray keys = scheduleList.getJSONObject(0).names();
			int keyCounter = 0;
			for (Object object : keys) {
				keyCounter++;
				String key = object.toString();
				bw.write(key);
				if (keyCounter != keys.length()) {
					bw.write(snap.delimiter);
				} else {
					bw.newLine();
				}
			}

			keys = new JSONArray();
			for (int i = 0; i < scheduleList.length(); i++) {
				keys = scheduleList.getJSONObject(i).names();
				for (Object object : keys) {
					keyCounter++;
					String key = object.toString();
					bw.write(scheduleList.getJSONObject(i).get(key).toString());
					if (keyCounter != keys.length()) {
						bw.write(snap.delimiter);
					}
				}
				bw.newLine();
			}
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
