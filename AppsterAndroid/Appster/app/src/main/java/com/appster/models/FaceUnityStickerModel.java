package com.appster.models;


import androidx.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

public class FaceUnityStickerModel{
	public static final int TYPE_NONE = -1;
	public static final int TYPE_STICKER = 0;
	public static final int TYPE_DUMP = 1;

	@IntDef({TYPE_NONE, TYPE_STICKER, TYPE_DUMP})
	public @interface STICKER_TYPE{}

	@SerializedName("Description")
	private String description;

	@SerializedName("Available")
	private boolean available;

	@SerializedName("Id")
	private int id=0;

	@SerializedName("Image")
	private String image;

	@SerializedName("File")
	private String file="";

	@SerializedName("Name")
	private String name;

	@STICKER_TYPE
	private int type;

	private boolean isSelected;

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setAvailable(boolean available){
		this.available = available;
	}

	public boolean isAvailable(){
		return available;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setFile(String file){
		this.file = file;
	}

	public String getFile(){
		return file;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public @STICKER_TYPE int getType() {
		return type;
	}

	public void setType(@STICKER_TYPE int type) {
		this.type = type;
	}

	@Override
 	public String toString(){
		return 
			"FaceUnityStickerModel{" + 
			"description = '" + description + '\'' + 
			",available = '" + available + '\'' + 
			",id = '" + id + '\'' + 
			",image = '" + image + '\'' + 
			",file = '" + file + '\'' + 
			",name = '" + name + '\'' + 
			"}";
		}
}