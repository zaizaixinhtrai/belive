package com.appster.models;

import com.google.gson.annotations.SerializedName;

public class ProductModel{

	@SerializedName("Description")
	private String description;

	@SerializedName("PromoCode")
	private String promoCode;

	@SerializedName("Price")
	private float price;

	@SerializedName("ImageUrl")
	private String imageUrl;

	@SerializedName("Id")
	private int id;

	@SerializedName("Url")
	private String url;

	@SerializedName("Name")
	private String name;

	@SerializedName("PromotionPrice")
	private float promotionPrice;

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setPromoCode(String promoCode){
		this.promoCode = promoCode;
	}

	public String getPromoCode(){
		return promoCode;
	}

	public void setPrice(float price){
		this.price = price;
	}

	public float getPrice(){
		return price;
	}

	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}

	public String getImageUrl(){
		return imageUrl;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPromotionPrice(float promotionPrice){
		this.promotionPrice = promotionPrice;
	}

	public float getPromotionPrice(){
		return promotionPrice;
	}

	@Override
 	public String toString(){
		return 
			"ProductModel{" + 
			"description = '" + description + '\'' + 
			",promoCode = '" + promoCode + '\'' + 
			",price = '" + price + '\'' + 
			",imageUrl = '" + imageUrl + '\'' + 
			",id = '" + id + '\'' + 
			",url = '" + url + '\'' + 
			",name = '" + name + '\'' + 
			",promotionPrice = '" + promotionPrice + '\'' + 
			"}";
		}
}