package codes.biscuit.minecraftofflineaccountadder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MinecraftOfflineAccountAdder {

	private static final String DIALOG_TITLE = "Minecraft Offline Account Adder";

	public static void main(String[] args) {
		try {
			String chosenUsername = JOptionPane.showInputDialog(null, "This will add an offline account to your minecraft launcher and select it!\n" +
							"Please type the name of the account you'd like to add.\n ", DIALOG_TITLE, JOptionPane.QUESTION_MESSAGE);

			if (chosenUsername == null) {
				JOptionPane.showMessageDialog(null, "Invalid username! Exiting...", DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}

			// This will lookup the username typed and correct the username's capitalization.
			// This way there is no issue joining servers that check capitalization.
			try {
				URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + chosenUsername);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setReadTimeout(5000);
				connection.setDoOutput(true);

				int responseCode = connection.getResponseCode();

				if (responseCode == 200) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					JsonObject responseObject = new Gson().fromJson(reader, JsonElement.class).getAsJsonObject();
					chosenUsername = responseObject.get("name").getAsString();
					reader.close();
				}
			} catch (Exception ex) {
				// This isn't necessary for the program's execution.
			}

			// This is the default minecraft location.
			File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\.minecraft\\launcher_profiles.json");

			JsonObject fileObject;

			// Read the existing launcher profiles file OR
			if (file.exists()) {
				fileObject = new Gson().fromJson(new FileReader(file), JsonElement.class).getAsJsonObject();
			} else {
				// Create a new one.
				if (!file.getParentFile().exists()) {
					boolean created = file.getParentFile().mkdirs();

					if (!created) {
						throw new IOException();
					}
				}

				file.createNewFile();

				fileObject = new JsonObject();
			}

			// LAUNCHER PROFILES FORMAT
			//{
			//   "authenticationDatabase":{
			//      "MinecraftName":{ <-- Selected Username
			//         "profiles":{
			//            "abcdefghijklmnopqrstuvwxyz012345":{ <-- Random UUID
			//               "displayName":"MinecraftName" <-- Selected Username
			//            }
			//         },
			//         "properties":[],
			//         "username":"MinecraftName" <-- Selected Username
			//      }
			//   },
			//   "selectedUser":{
			//      "account":"MinecraftName", <-- Selected Username
			//      "profile":"abcdefghijklmnopqrstuvwxyz012345" <-- Random UUID
			//   }
			//}

			// "authenticationDatabase":{
			JsonObject authenticationDatabaseObject;
			if (fileObject.has("authenticationDatabase")) {
				authenticationDatabaseObject = fileObject.get("authenticationDatabase").getAsJsonObject();
			} else {
				authenticationDatabaseObject = new JsonObject();
				fileObject.add("authenticationDatabase", authenticationDatabaseObject);
			}

			    // "MinecraftName":{
				JsonObject usernameObject = new JsonObject();
				authenticationDatabaseObject.add(chosenUsername, usernameObject);

					// "profiles":{
					JsonObject profilesObject = new JsonObject();
					usernameObject.add("profiles", profilesObject);

						// "abcdefghijklmnopqrstuvwxyz012345":{
						String randomAccountUUID = UUID.randomUUID().toString().replaceAll("-", "");
						JsonObject randomUUIDObject = new JsonObject();
						profilesObject.add(randomAccountUUID, randomUUIDObject);

							// "displayName":"MinecraftName"
							randomUUIDObject.addProperty("displayName", chosenUsername);

					// "properties":[],
					usernameObject.add("properties", new JsonArray());
					// "username":"MinecraftName"
					usernameObject.addProperty("username", chosenUsername);

			// "selectedUser":{
			JsonObject selectedUserObject = new JsonObject();
			fileObject.add("selectedUser", selectedUserObject);

				// "account":"MinecraftName",
				selectedUserObject.addProperty("account", chosenUsername);
				// "profile":"abcdefghijklmnopqrstuvwxyz012345"
				selectedUserObject.addProperty("profile", randomAccountUUID);

			String jsonString = fileObject.toString();
			OutputStream output = new FileOutputStream(file);
			output.write(jsonString.getBytes(), 0, jsonString.length());
			output.close();

			JOptionPane.showMessageDialog(null, "Successfully added the account "+chosenUsername+"!", DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "An error occured while trying to add an account!\n"+ex.toString(), DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
		}

		System.exit(0);
	}
}
