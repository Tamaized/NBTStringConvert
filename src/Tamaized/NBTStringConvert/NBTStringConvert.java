package Tamaized.NBTStringConvert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.StringTag;
import org.jnbt.Tag;

public class NBTStringConvert {

	static final List<File> files = new ArrayList<File>();

	// We want arg 1: folder path, arg 2: String Tag's Name, arg 3: string to replace, arg 4: string to be placed
	public static void main(String[] args) throws FileNotFoundException, IOException {

		if (args.length > 3) {

			String folder = args[0];
			String name = args[1];
			String replace = args[2];
			String set = args[3];

			scanFiles(folder);
			replaceFileContents(name, replace, set);

		} else {
			System.out.println("Not Enough Args");
		}

	}

	static void scanFiles(String path) {

		File folder = new File(path);

		if (folder.isDirectory()) {

			for (File file : folder.listFiles()) {

				if (file.getName().toLowerCase().endsWith(".nbt")) {

					System.out.println("Found File: " + file.getName());
					files.add(file);

				}

			}

		} else {
			System.out.println("Path was not a folder");
		}

	}

	static void replaceFileContents(String name, String replace, String set) throws FileNotFoundException, IOException {

		for (File file : files) {

			System.out.println("Scanning: " + file.getName());
			Tag tag;
			NBTInputStream stream = new NBTInputStream(new FileInputStream(file));
			{
				tag = stream.readTag();
				if (tag instanceof StringTag) {
					StringTag stringTag = (StringTag) tag;
					if (stringTag.getName().equals(name) && stringTag.getValue().equals(replace)) {
						System.out.println("(StringTag) Replacing: " + replace + " with: " + set);
						stringTag = new StringTag(name, set);
					}
				} else if (tag instanceof CompoundTag) {
					// System.out.println("(CompoundTag) enter: " + tag.getName());
					CompoundTag ct = (CompoundTag) tag;
					tag = writeNewTag(ct, name, replace, set);
				} else if (tag instanceof ListTag) {
					// System.out.println("(ListTag) enter: " + tag.getName());
					tag = writeNewTag((ListTag) tag, name, replace, set);
				}
			}
			stream.close();
			NBTOutputStream out = new NBTOutputStream(new FileOutputStream(file));
			out.writeTag(tag);
			out.close();
		}

	}

	static CompoundTag writeNewTag(CompoundTag ct, String name, String replace, String set) {
		Iterator<Entry<String, Tag>> iter = ct.getValue().entrySet().iterator();
		Map<String, Tag> map = new HashMap<String, Tag>();
		while (iter.hasNext()) {
			Entry<String, Tag> entry = iter.next();
			Tag tag = entry.getValue();
			if (tag instanceof StringTag) {
				StringTag stringTag = ((StringTag) tag);
				if (stringTag.getName().equals(name) && stringTag.getValue().equals(replace)) {
					System.out.println("(StringTag) Replacing: " + replace + " with: " + set);
					tag = new StringTag(name, set);
				}
			} else if (tag instanceof CompoundTag) {
				// System.out.println("(CompoundTag) enter: " + tag.getName());
				tag = writeNewTag((CompoundTag) tag, name, replace, set);
			} else if (tag instanceof ListTag) {
				// System.out.println("(ListTag) enter: " + tag.getName());
				tag = writeNewTag((ListTag) tag, name, replace, set);
			}
			map.put(entry.getKey(), tag);
		}
		return new CompoundTag(ct.getName(), map);
	}

	static ListTag writeNewTag(ListTag lt, String name, String replace, String set) {
		List<Tag> list = new ArrayList<Tag>();
		for (Tag tag : lt.getValue()) {
			if (tag instanceof StringTag) {
				StringTag stringTag = ((StringTag) tag);
				if (stringTag.getName().equals(name) && stringTag.getValue().equals(replace)) {
					System.out.println("(StringTag) Replacing: " + replace + " with: " + set);
					tag = new StringTag(name, set);
				}
			} else if (tag instanceof CompoundTag) {
				// System.out.println("(CompoundTag) enter: " + tag.getName());
				tag = writeNewTag((CompoundTag) tag, name, replace, set);
			} else if (tag instanceof ListTag) {
				// System.out.println("(ListTag) enter: " + tag.getName());
				tag = writeNewTag((ListTag) tag, name, replace, set);
			}
			list.add(tag);
		}
		return new ListTag(lt.getName(), lt.getType(), list);
	}

}
