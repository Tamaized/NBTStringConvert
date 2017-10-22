package Tamaized.NBTStringConvert;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NBTStringConvert {

	private static final List<File> files = new ArrayList<>();

	// We want arg 1: folder path, arg 2: String Tag's Name, arg 3: string to replace, arg 4: string to be placed
	public static void main(String[] args) throws IOException {
		if (args.length > 3) {
			String folder = args[0];
			String name = args[1];
			String replace = args[2];
			String set = args[3];
			scanFiles(folder);
			replaceFileContents(name, replace, set);
		} else
			System.out.println("Not Enough Args");
	}

	private static void scanFiles(String path) {
		File folder = new File(path);
		if (folder.isDirectory()) {
			File[] list = folder.listFiles();
			if (list != null)
				for (File file : list)
					if (file.getName().toLowerCase().endsWith(".nbt")) {
						System.out.println("Found File: " + file.getName());
						files.add(file);
					}
		} else
			System.out.println("Path was not a folder");
	}

	private static void replaceFileContents(String name, String replace, String set) throws IOException {

		for (File file : files) {
			System.out.println("Scanning: " + file.getName());
			Tag tag;
			NBTInputStream stream = new NBTInputStream(new FileInputStream(file));
			tag = stream.readTag();
			if (tag instanceof StringTag) {
				StringTag stringTag = (StringTag) tag;
				if (stringTag.getName().equals(name) && stringTag.getValue().equals(replace))
					System.out.println("(StringTag) Replacing: " + replace + " with: " + set);
			} else if (tag instanceof CompoundTag) {
				CompoundTag ct = (CompoundTag) tag;
				tag = writeNewTag(ct, name, replace, set);
			} else if (tag instanceof ListTag)
				tag = writeNewTag((ListTag) tag, name, replace, set);
			stream.close();
			NBTOutputStream out = new NBTOutputStream(new FileOutputStream(file));
			out.writeTag(tag);
			out.close();
		}
	}

	private static CompoundTag writeNewTag(CompoundTag ct, String name, String replace, String set) {
		Iterator<Entry<String, Tag>> iter = ct.getValue().entrySet().iterator();
		Map<String, Tag> map = new HashMap<>();
		while (iter.hasNext()) {
			Entry<String, Tag> entry = iter.next();
			Tag tag = entry.getValue();
			if (tag instanceof StringTag) {
				StringTag stringTag = ((StringTag) tag);
				if (stringTag.getName().equals(name) && stringTag.getValue().equals(replace)) {
					System.out.println("(StringTag) Replacing: " + replace + " with: " + set);
					tag = new StringTag(name, set);
				}
			} else if (tag instanceof CompoundTag)
				tag = writeNewTag((CompoundTag) tag, name, replace, set);
			else if (tag instanceof ListTag)
				tag = writeNewTag((ListTag) tag, name, replace, set);
			map.put(entry.getKey(), tag);
		}
		return new CompoundTag(ct.getName(), map);
	}

	private static ListTag writeNewTag(ListTag lt, String name, String replace, String set) {
		List<Tag> list = new ArrayList<>();
		for (Tag tag : lt.getValue()) {
			if (tag instanceof StringTag) {
				StringTag stringTag = ((StringTag) tag);
				if (stringTag.getName().equals(name) && stringTag.getValue().equals(replace)) {
					System.out.println("(StringTag) Replacing: " + replace + " with: " + set);
					tag = new StringTag(name, set);
				}
			} else if (tag instanceof CompoundTag)
				tag = writeNewTag((CompoundTag) tag, name, replace, set);
			else if (tag instanceof ListTag)
				tag = writeNewTag((ListTag) tag, name, replace, set);
			list.add(tag);
		}
		return new ListTag(lt.getName(), lt.getType(), list);
	}

}
