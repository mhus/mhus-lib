/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.vault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.UUID;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.MValidator;
import de.mhus.lib.core.crypt.MCrypt;
import de.mhus.lib.core.util.SecureString;
import de.mhus.lib.errors.MException;

public class FolderVaultSource extends MutableVaultSource {

	private SecureString passphrase;
	private File folder;

	public FolderVaultSource(File folder, String passphrase, String name) throws IOException {
		this(folder,passphrase);
		this.name = name;
	}
	public FolderVaultSource(File folder, String passphrase) throws IOException {
		this.passphrase = new SecureString(passphrase);
		this.folder = folder;
		if (folder.exists())
			doLoad();
		else
			folder.mkdirs();
	}
	
	@Override
	public void doLoad() throws IOException {
		{
			File file = new File(folder, "info.txt");
			if (file.exists())
				name = MFile.readFile(file).trim();
		}
		entries.clear();
		for (File file : folder.listFiles()) {
			if (!file.getName().startsWith(".") && file.isFile() && MValidator.isUUID(file.getName())) {
				loadEntry(file);
			}
		}
	}

	protected void loadEntry(File file) throws IOException {
		FileInputStream parent = new FileInputStream(file);
		InputStream is = MCrypt.createCipherInputStream(parent, passphrase.value());
		ObjectInputStream ois = new ObjectInputStream(is);
		VaultEntry entry = new FileEntry(ois);
		try {
			addEntry(entry);
		} catch (MException e) {
			log().d(entry,e);
		}
		parent.close();
	}
	
	@Override
	public void doSave() throws IOException {
		{
			File file = new File(folder, "info.txt");
			MFile.writeFile(file, name);
		}
		HashSet<String> ids = new HashSet<>();
		for (VaultEntry entry : entries.values()) {
			ids.add(entry.getId().toString());
			saveEntry(entry);
		}
		for (File file : folder.listFiles()) {
			if (!file.getName().startsWith(".") && file.isFile() && MValidator.isUUID(file.getName())) {
				if (!ids.contains(file.getName()))
					file.delete();
			}
		}
	}

	protected void saveEntry(VaultEntry entry) throws IOException {
		File file = new File(folder,entry.getId().toString());
		FileOutputStream parent = new FileOutputStream(file);
		OutputStream os = MCrypt.createCipherOutputStream(parent, passphrase.value());
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeUTF(entry.getId().toString());
		oos.writeUTF(entry.getType());
		oos.writeUTF(entry.getDescription());
		oos.writeUTF(entry.getValue());
		oos.flush();
		parent.close();
	}

	private class FileEntry extends DefaultEntry {

		public FileEntry(ObjectInputStream ois) throws IOException {
			id = UUID.fromString(ois.readUTF());
			type = ois.readUTF();
			description = ois.readUTF();
			value = new SecureString(ois.readUTF());
		}
		
	}
	
	@Override
	public String toString() {
		return MSystem.toString(this, name, entries.size(), folder);
	}


}
