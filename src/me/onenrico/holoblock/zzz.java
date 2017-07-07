package me.onenrico.holoblock;

import org.apache.commons.lang.StringUtils;

public class zzz {

	public static void main(String[] args) {
		print("Hai World");
		String tes = "meow adalah nama kota di Indonesia dan sekarang sudah gila";
		tes = StringUtils.capitalise(tes);
		String center = "Tengah";
		center = StringUtils.center(center, 51);
		print(tes);
		print(center);
	}

	public static void print(String msg) {
		System.out.println(msg);
	}
}
